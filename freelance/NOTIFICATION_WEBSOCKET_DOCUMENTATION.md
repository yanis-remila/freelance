# Documentation : Système de Notification WebSocket - Rappel Fin de Mission

## Table des matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Fichiers créés](#fichiers-créés)
4. [Fichiers modifiés](#fichiers-modifiés)
5. [Endpoints REST API](#endpoints-rest-api)
6. [Configuration WebSocket](#configuration-websocket)
7. [Guide de test](#guide-de-test)

---

## Vue d'ensemble

Ce système permet de notifier les freelances **3 jours avant la fin de leur mission** pour leur rappeler de rédiger un rapport de fin de mission. Les notifications sont :
- Persistées en base de données PostgreSQL
- Envoyées en temps réel via WebSocket (STOMP over SockJS)
- Accessibles via une API REST

---

## Architecture

```
┌─────────────────┐     WebSocket (STOMP)      ┌──────────────────────┐
│  Client Browser │◄──────────────────────────►│  Spring Boot App     │
│  /topic/notif/  │                            │                      │
│  {freelancerId} │                            │  @Scheduled (9h00)   │
└─────────────────┘                            │  ► Check missions    │
                                               │  ► Send notifications│
                                               └──────────┬───────────┘
                                                          │
                                                          ▼
                                               ┌──────────────────────┐
                                               │     PostgreSQL       │
                                               │  - notifications     │
                                               │  - mission_freelance │
                                               └──────────────────────┘
```

### Flux de données

1. **Scheduler (9h00 chaque jour)** → Recherche les missions se terminant dans 3 jours
2. **NotificationService** → Crée la notification en BDD + envoie via WebSocket
3. **Client** → Reçoit la notification en temps réel sur `/topic/notifications/{freelancerId}`

---

## Fichiers créés

### 1. `model/enums/NotificationType.java`

**Chemin** : `src/main/java/com/example/freelance/model/enums/NotificationType.java`

**Description** : Énumération définissant les types de notifications possibles.

```java
public enum NotificationType {
    RAPPEL_FIN_MISSION,  // Rappel 3 jours avant fin de mission
    MISSION_ASSIGNEE     // Notification quand une mission est assignée (extensible)
}
```

**Utilisation** : Permet de catégoriser les notifications et de filtrer par type si nécessaire.

---

### 2. `model/Notification.java`

**Chemin** : `src/main/java/com/example/freelance/model/Notification.java`

**Description** : Entité JPA représentant une notification en base de données.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long | Identifiant unique auto-généré |
| `freelancerId` | Long | ID du freelancer destinataire |
| `missionId` | Long | ID de la mission concernée |
| `type` | NotificationType | Type de notification (enum) |
| `message` | String | Contenu textuel de la notification |
| `createdAt` | LocalDateTime | Date/heure de création (auto-rempli via @PrePersist) |
| `isRead` | Boolean | Indique si la notification a été lue (défaut: false) |
| `sentViaWebsocket` | Boolean | Indique si envoyée via WebSocket (défaut: false) |

**Table PostgreSQL générée** : `notifications`

**Annotations importantes** :
- `@PrePersist` : Remplit automatiquement `createdAt` à la création
- `@Enumerated(EnumType.STRING)` : Stocke l'enum en texte lisible

---

### 3. `repository/NotificationRepository.java`

**Chemin** : `src/main/java/com/example/freelance/repository/NotificationRepository.java`

**Description** : Interface JPA Repository pour accéder aux notifications.

| Méthode | Description |
|---------|-------------|
| `findByFreelancerIdOrderByCreatedAtDesc(Long)` | Toutes les notifications d'un freelancer (triées par date décroissante) |
| `findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(Long)` | Notifications non lues uniquement |
| `countByFreelancerIdAndIsReadFalse(Long)` | Compte les notifications non lues |

---

### 4. `dto/NotificationDTO.java`

**Chemin** : `src/main/java/com/example/freelance/dto/NotificationDTO.java`

**Description** : Data Transfer Object pour les réponses API et WebSocket.

**Champs** :
- `id`, `freelancerId`, `missionId`, `type`, `message`, `createdAt`, `isRead`

**Méthode statique** :
```java
public static NotificationDTO fromEntity(Notification notification)
```
Convertit une entité `Notification` en DTO (évite d'exposer l'entité JPA directement).

---

### 5. `config/WebSocketConfig.java`

**Chemin** : `src/main/java/com/example/freelance/config/WebSocketConfig.java`

**Description** : Configuration du broker de messages WebSocket avec STOMP.

**Configuration** :

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| Endpoint | `/ws` | Point d'entrée WebSocket |
| SockJS | Activé | Fallback pour navigateurs sans WebSocket natif |
| Message Broker | `/topic`, `/queue` | Préfixes pour les destinations de broadcast |
| Application Prefix | `/app` | Préfixe pour les messages envoyés au serveur |
| User Prefix | `/user` | Préfixe pour les messages utilisateur spécifiques |
| Allowed Origins | `*` | Autorise toutes les origines (CORS) |

**Canal de notification** : `/topic/notifications/{freelancerId}`

---

### 6. `service/NotificationService.java`

**Chemin** : `src/main/java/com/example/freelance/service/NotificationService.java`

**Description** : Service contenant la logique métier des notifications.

| Méthode | Description |
|---------|-------------|
| `createAndSendNotification(...)` | Crée une notification en BDD et l'envoie via WebSocket |
| `sendNotificationViaWebSocket(...)` | Envoie une notification sur le canal WebSocket du freelancer |
| `getNotificationsByFreelancerId(Long)` | Récupère toutes les notifications d'un freelancer |
| `getUnreadNotificationsByFreelancerId(Long)` | Récupère les notifications non lues |
| `countUnreadNotifications(Long)` | Compte les notifications non lues |
| `markAsRead(Long)` | Marque une notification comme lue |
| `markAllAsRead(Long)` | Marque toutes les notifications d'un freelancer comme lues |

**Dépendances injectées** :
- `NotificationRepository` : Accès aux données
- `SimpMessagingTemplate` : Envoi de messages WebSocket

---

### 7. `scheduler/MissionEndReminderScheduler.java`

**Chemin** : `src/main/java/com/example/freelance/scheduler/MissionEndReminderScheduler.java`

**Description** : Tâche planifiée qui s'exécute tous les jours à 9h00.

**Expression Cron** : `0 0 9 * * *`
- Seconde : 0
- Minute : 0
- Heure : 9
- Jour du mois : tous
- Mois : tous
- Jour de la semaine : tous

**Logique** :
1. Calcule la date cible (`aujourd'hui + 3 jours`)
2. Recherche les `MissionFreelance` avec `dateFin = date cible` ET `notificationEnvoyee = false`
3. Pour chaque mission trouvée :
   - Crée une notification avec le message de rappel
   - Envoie via WebSocket
   - Met à jour `notificationEnvoyee = true` pour éviter les doublons

**Logging** : Utilise SLF4J pour tracer l'exécution et les erreurs.

---

### 8. `controller/NotificationController.java`

**Chemin** : `src/main/java/com/example/freelance/controller/NotificationController.java`

**Description** : Contrôleur REST pour l'API des notifications.

**Base URL** : `/api/notifications`

| Méthode | Endpoint | Description | Réponse |
|---------|----------|-------------|---------|
| GET | `/freelancer/{id}` | Toutes les notifications du freelancer | `List<NotificationDTO>` |
| GET | `/freelancer/{id}/unread` | Notifications non lues | `List<NotificationDTO>` |
| GET | `/freelancer/{id}/count` | Compteur de non lues | `{"unreadCount": N}` |
| PUT | `/{id}/read` | Marquer comme lue | `NotificationDTO` |
| PUT | `/freelancer/{id}/read-all` | Tout marquer comme lu | `200 OK` |

---

## Fichiers modifiés

### 1. `pom.xml`

**Ajout** : Dépendance WebSocket

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

Cette dépendance inclut :
- Support STOMP (Simple Text Oriented Messaging Protocol)
- SockJS pour le fallback navigateur
- `SimpMessagingTemplate` pour l'envoi de messages

---

### 2. `model/MissionFreelance.java`

**Ajouts** :

| Champ | Type | Description |
|-------|------|-------------|
| `dateDebut` | LocalDate | Date de début de la mission |
| `dateFin` | LocalDate | Date de fin de la mission |
| `notificationEnvoyee` | Boolean | Flag pour éviter d'envoyer plusieurs rappels (défaut: false) |

**Colonnes PostgreSQL ajoutées** :
- `date_debut` (DATE)
- `date_fin` (DATE)
- `notification_envoyee` (BOOLEAN)

---

### 3. `repository/MissionFreelanceRepository.java`

**Ajout** : Requête JPQL personnalisée

```java
@Query("SELECT mf FROM MissionFreelance mf WHERE mf.dateFin = :targetDate AND mf.notificationEnvoyee = false")
List<MissionFreelance> findMissionsEndingOn(@Param("targetDate") LocalDate targetDate);
```

Cette requête :
- Filtre par date de fin exacte
- Exclut les missions déjà notifiées
- Retourne une liste de `MissionFreelance`

---

### 4. `FreelanceApplication.java`

**Ajout** : Annotation `@EnableScheduling`

```java
@SpringBootApplication
@EnableScheduling  // ← Ajouté
public class FreelanceApplication { ... }
```

Cette annotation active le support des tâches planifiées (`@Scheduled`) dans Spring Boot.

---

## Endpoints REST API

### Récupérer toutes les notifications

```http
GET /api/notifications/freelancer/1
```

**Réponse** :
```json
[
  {
    "id": 1,
    "freelancerId": 1,
    "missionId": 42,
    "type": "RAPPEL_FIN_MISSION",
    "message": "Rappel : Votre mission (ID: 42) se termine dans 3 jours...",
    "createdAt": "2026-01-16T09:00:00",
    "isRead": false
  }
]
```

### Récupérer les notifications non lues

```http
GET /api/notifications/freelancer/1/unread
```

### Compter les notifications non lues

```http
GET /api/notifications/freelancer/1/count
```

**Réponse** :
```json
{
  "unreadCount": 3
}
```

### Marquer une notification comme lue

```http
PUT /api/notifications/1/read
```

### Marquer toutes les notifications comme lues

```http
PUT /api/notifications/freelancer/1/read-all
```

---

## Configuration WebSocket

### Connexion côté client (JavaScript)

```javascript
// Import des librairies (via CDN ou npm)
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
// <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

const freelancerId = 1; // ID du freelancer connecté

// 1. Créer la connexion SockJS
const socket = new SockJS('http://localhost:8080/ws');

// 2. Créer le client STOMP
const stompClient = Stomp.over(socket);

// 3. Se connecter et s'abonner
stompClient.connect({}, function(frame) {
    console.log('Connecté: ' + frame);

    // S'abonner au canal de notifications du freelancer
    stompClient.subscribe('/topic/notifications/' + freelancerId, function(message) {
        const notification = JSON.parse(message.body);
        console.log('Nouvelle notification:', notification);

        // Afficher la notification à l'utilisateur
        showNotification(notification);
    });
});

// 4. Fonction d'affichage (exemple)
function showNotification(notification) {
    alert(notification.message);
    // Ou utiliser une bibliothèque de toast/notification
}
```

### Avec React (exemple)

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useEffect, useState } from 'react';

function useNotifications(freelancerId) {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            onConnect: () => {
                client.subscribe(`/topic/notifications/${freelancerId}`, (message) => {
                    const notification = JSON.parse(message.body);
                    setNotifications(prev => [notification, ...prev]);
                });
            }
        });

        client.activate();

        return () => client.deactivate();
    }, [freelancerId]);

    return notifications;
}
```

---

## Guide de test

### 1. Test manuel de l'API

```bash
# Récupérer les notifications du freelancer 1
curl http://localhost:8080/api/notifications/freelancer/1

# Compter les notifications non lues
curl http://localhost:8080/api/notifications/freelancer/1/count

# Marquer la notification 1 comme lue
curl -X PUT http://localhost:8080/api/notifications/1/read
```

### 2. Test du scheduler

Pour tester sans attendre 9h00, vous pouvez :

**Option A** : Modifier temporairement le cron
```java
@Scheduled(cron = "0 * * * * *") // Toutes les minutes
```

**Option B** : Appeler manuellement le scheduler via un endpoint de test
```java
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final MissionEndReminderScheduler scheduler;

    @PostMapping("/trigger-scheduler")
    public void triggerScheduler() {
        scheduler.sendMissionEndReminders();
    }
}
```

### 3. Créer des données de test

```sql
-- Insérer une mission freelance qui se termine dans 3 jours
INSERT INTO mission_freelance (mission_id, freelancer_id, date_debut, date_fin, notification_envoyee)
VALUES (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days', false);
```

### 4. Vérifier en base de données

```sql
-- Voir les notifications créées
SELECT * FROM notifications ORDER BY created_at DESC;

-- Voir les missions avec leur statut de notification
SELECT * FROM mission_freelance WHERE notification_envoyee = true;
```

---

## Notes importantes

1. **Sécurité** : En production, pensez à sécuriser le WebSocket avec l'authentification (Spring Security + JWT)

2. **CORS** : La configuration actuelle autorise toutes les origines (`*`). En production, restreignez aux domaines autorisés.

3. **Scalabilité** : Pour une architecture distribuée (plusieurs instances), utilisez un broker externe (RabbitMQ, Redis) au lieu du simple broker en mémoire.

4. **Fuseaux horaires** : Le scheduler s'exécute à 9h00 dans le fuseau horaire du serveur. Configurez `spring.jackson.time-zone` si nécessaire.

---

## Auteur

Généré automatiquement par Claude Code
