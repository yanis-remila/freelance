# Guide de Présentation - Système de Notification WebSocket

## Introduction (1-2 min)

> "J'ai implémenté un système de notifications en temps réel qui alerte les freelances 3 jours avant la fin de leur mission pour leur rappeler de rédiger leur rapport de fin de mission."

### Problématique résolue
- Les freelances oublient souvent de rédiger leur rapport de fin de mission
- Besoin d'un rappel automatique et proactif
- Notification en temps réel (pas besoin de rafraîchir la page)

---

## Architecture Technique (2-3 min)

### Schéma à dessiner au tableau

```
┌─────────────────┐                              ┌──────────────────────┐
│                 │     WebSocket (STOMP)        │   Spring Boot App    │
│  Navigateur     │◄────────────────────────────►│                      │
│                 │                              │  Scheduler @9h00     │
└─────────────────┘                              │  ↓                   │
                                                 │  Vérifie missions    │
                                                 │  ↓                   │
                                                 │  Envoie notification │
                                                 └──────────┬───────────┘
                                                            │
                                                            ▼
                                                 ┌──────────────────────┐
                                                 │     PostgreSQL       │
                                                 │  • notifications     │
                                                 │  • mission_freelance │
                                                 └──────────────────────┘
```

### Technologies utilisées
| Technologie | Rôle |
|-------------|------|
| **Spring WebSocket** | Communication bidirectionnelle temps réel |
| **STOMP** | Protocole de messagerie au-dessus de WebSocket |
| **SockJS** | Fallback pour navigateurs sans WebSocket |
| **@Scheduled** | Tâche planifiée (cron job) |
| **JPA/Hibernate** | Persistance des notifications |

---

## Démonstration Live (5-7 min)

### Étape 1 : Lancer l'application
```bash
mvn spring-boot:run
```

### Étape 2 : Ouvrir l'interface de test
```
http://localhost:9091/websocket-test.html
```

### Étape 3 : Démontrer le flux complet

1. **Se connecter au WebSocket**
   - Entrer l'ID freelancer : `1`
   - Cliquer sur "Se connecter"
   - Montrer le statut "Connecté"

2. **Envoyer une notification de test**
   - Cliquer sur "Envoyer notification test"
   - **La notification apparaît instantanément** (temps réel)

3. **Montrer la persistance en base**
   - Via l'API : `GET http://localhost:9091/api/notifications/freelancer/1`
   - Ou directement en base PostgreSQL

4. **Montrer les fonctionnalités de l'API**
   - Compter les non lues : `/api/notifications/freelancer/1/count`
   - Marquer comme lu : `PUT /api/notifications/1/read`

---

## Points Techniques à Expliquer (3-4 min)

### 1. Configuration WebSocket
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // Fallback pour compatibilité
    }
}
```
> "SockJS permet de supporter les anciens navigateurs qui n'ont pas WebSocket natif."

### 2. Scheduler avec Expression Cron
```java
@Scheduled(cron = "0 0 9 * * *")  // Tous les jours à 9h00
public void sendMissionEndReminders() {
    LocalDate targetDate = LocalDate.now().plusDays(3);
    // Cherche les missions qui se terminent dans 3 jours
}
```
> "L'expression cron `0 0 9 * * *` signifie : seconde 0, minute 0, heure 9, tous les jours."

### 3. Envoi de notification via WebSocket
```java
public void sendNotificationViaWebSocket(Notification notification) {
    NotificationDTO dto = NotificationDTO.fromEntity(notification);
    String destination = "/topic/notifications/" + notification.getFreelancerId();
    messagingTemplate.convertAndSend(destination, dto);
}
```
> "Chaque freelancer a son propre canal `/topic/notifications/{id}`, ce qui permet de cibler les notifications."

### 4. Éviter les doublons
```java
@Query("SELECT mf FROM MissionFreelance mf WHERE mf.dateFin = :targetDate AND mf.notificationEnvoyee = false")
List<MissionFreelance> findMissionsEndingOn(@Param("targetDate") LocalDate targetDate);
```
> "Le flag `notificationEnvoyee` empêche d'envoyer plusieurs fois la même notification."

---

## Structure des Fichiers (1-2 min)

### Fichiers créés (8)
```
src/main/java/com/example/freelance/
├── model/
│   ├── Notification.java          ← Entité JPA
│   └── enums/
│       └── NotificationType.java  ← Enum (RAPPEL_FIN_MISSION, MISSION_ASSIGNEE)
├── repository/
│   └── NotificationRepository.java
├── dto/
│   └── NotificationDTO.java       ← Transfer Object
├── config/
│   └── WebSocketConfig.java       ← Config STOMP
├── service/
│   └── NotificationService.java   ← Logique métier
├── scheduler/
│   └── MissionEndReminderScheduler.java  ← Cron job
└── controller/
    └── NotificationController.java       ← API REST
```

### Fichiers modifiés (4)
- `pom.xml` → Ajout dépendance WebSocket
- `MissionFreelance.java` → Ajout champs date
- `MissionFreelanceRepository.java` → Nouvelle requête
- `FreelanceApplication.java` → `@EnableScheduling`

---

## API REST Disponible (1 min)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/notifications/freelancer/{id}` | Toutes les notifications |
| GET | `/api/notifications/freelancer/{id}/unread` | Non lues uniquement |
| GET | `/api/notifications/freelancer/{id}/count` | Compteur non lues |
| PUT | `/api/notifications/{id}/read` | Marquer comme lue |
| PUT | `/api/notifications/freelancer/{id}/read-all` | Tout marquer lu |

---

## Questions Potentielles du Prof et Réponses

### Q: Pourquoi WebSocket plutôt que du polling HTTP ?
> "Le polling consomme beaucoup de ressources (requêtes répétées). WebSocket maintient une connexion persistante et permet au serveur de pousser les données uniquement quand nécessaire."

### Q: Comment gérer la scalabilité avec plusieurs instances ?
> "Actuellement j'utilise un simple broker en mémoire. En production, on utiliserait un broker externe comme RabbitMQ ou Redis pour partager les messages entre instances."

### Q: Que se passe-t-il si le freelancer n'est pas connecté ?
> "La notification est persistée en base de données. Quand il se connecte à l'application, il peut récupérer ses notifications via l'API REST `/api/notifications/freelancer/{id}`."

### Q: Comment sécuriser les WebSocket ?
> "On pourrait ajouter Spring Security avec JWT. Le token serait envoyé lors du handshake WebSocket et validé côté serveur."

### Q: Pourquoi STOMP ?
> "STOMP (Simple Text Oriented Messaging Protocol) ajoute une couche d'abstraction au-dessus de WebSocket avec des concepts comme les destinations et les subscriptions, ce qui simplifie la gestion des messages."

---

## Conclusion (30 sec)

> "Ce système permet donc de notifier automatiquement les freelances en temps réel, 3 jours avant la fin de leur mission. Les notifications sont persistées en base pour ne pas être perdues, et l'API REST permet de les gérer (lecture, marquage comme lu). L'architecture est extensible pour ajouter d'autres types de notifications."

---

## Checklist Avant la Présentation

- [ ] Application démarrée sur le port 9091
- [ ] Base de données PostgreSQL accessible
- [ ] Page `websocket-test.html` ouverte dans le navigateur
- [ ] Postman prêt avec les endpoints de test
- [ ] Avoir ce document ouvert pour référence

---

## Commandes Utiles

```bash
# Démarrer l'application
mvn spring-boot:run

# Compiler uniquement
mvn compile

# Lancer les tests
mvn test
```

```sql
-- Voir les notifications en base
SELECT * FROM notifications ORDER BY created_at DESC;

-- Créer une mission de test (termine dans 3 jours)
INSERT INTO mission_freelance (mission_id, freelancer_id, date_debut, date_fin, notification_envoyee)
VALUES (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days', false);
```
