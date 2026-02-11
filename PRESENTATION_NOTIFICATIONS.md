# Système de Notifications - Présentation Complète

## Table des matières
1. [Vue d'ensemble de l'Architecture](#1-vue-densemble-de-larchitecture)
2. [Backend (Java / Spring Boot)](#2-backend-java--spring-boot)
3. [Frontend (React / TypeScript)](#3-frontend-react--typescript)
4. [Flux de Données](#4-flux-de-données)
5. [Base de Données](#5-base-de-données)
6. [Points Forts du Système](#6-points-forts-du-système)
7. [Dépendances Techniques](#7-dépendances-techniques)
8. [Points d'Amélioration Possibles](#8-points-damélioration-possibles)

---

## 1. Vue d'ensemble de l'Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      ARCHITECTURE GLOBALE                           │
├──────────────────────────────┬──────────────────────────────────────┤
│        BACKEND (Spring)      │         FRONTEND (React)             │
├──────────────────────────────┼──────────────────────────────────────┤
│  • REST API                  │  • NotificationContext (état global) │
│  • WebSocket (STOMP/SockJS)  │  • Services HTTP & WebSocket         │
│  • Scheduler automatique     │  • Composants UI (Bell, Panel, Item) │
│  • Event Listeners           │  • Toast notifications               │
└──────────────────────────────┴──────────────────────────────────────┘
```

### Technologies utilisées

| Couche | Technologies |
|--------|--------------|
| Backend | Java 17, Spring Boot, Spring WebSocket, JPA/Hibernate |
| Frontend | React 18, TypeScript, STOMP.js, SockJS |
| Base de données | PostgreSQL |
| Communication temps réel | WebSocket avec STOMP protocol |

---

## 2. Backend (Java / Spring Boot)

### 2.1 Modèle de données

#### Entité `Notification`
**Fichier:** `src/main/java/com/example/freelance/model/Notification.java`

| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long | Identifiant unique (auto-généré) |
| `freelancerId` | Long | ID du freelancer destinataire |
| `missionId` | Long | ID de la mission concernée |
| `type` | NotificationType | Type de notification (enum) |
| `message` | String | Contenu du message |
| `createdAt` | LocalDateTime | Date de création (auto) |
| `timestamp` | LocalDateTime | Timestamp additionnel |
| `isRead` | Boolean | Statut lu/non lu |
| `sentViaWebsocket` | Boolean | Indicateur d'envoi WebSocket |

```java
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long freelancerId;
    private Long missionId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdAt;
    private boolean isRead = false;
    private boolean sentViaWebsocket = false;

    @ManyToOne
    @JoinColumn(name = "freelancer_ref_id")
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "mission_ref_id")
    private Mission mission;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

#### Enum `NotificationType`
**Fichier:** `src/main/java/com/example/freelance/model/enums/NotificationType.java`

| Valeur | Description |
|--------|-------------|
| `RAPPEL_FIN_MISSION` | Rappel envoyé 3 jours avant la fin d'une mission |
| `MISSION_ASSIGNEE` | Notification lors de l'assignation d'une nouvelle mission |

```java
public enum NotificationType {
    RAPPEL_FIN_MISSION,
    MISSION_ASSIGNEE
}
```

#### DTO `NotificationDTO`
**Fichier:** `src/main/java/com/example/freelance/dto/NotificationDTO.java`

Objet de transfert de données pour les réponses API et WebSocket (exclut les champs internes).

```java
public class NotificationDTO {
    private Long id;
    private Long freelancerId;
    private Long missionId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

    public static NotificationDTO fromEntity(Notification notification) {
        // Conversion Entity -> DTO
    }
}
```

---

### 2.2 Repository

**Fichier:** `src/main/java/com/example/freelance/repository/NotificationRepository.java`

```java
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupérer toutes les notifications d'un freelancer (triées par date décroissante)
    List<Notification> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);

    // Récupérer uniquement les notifications non lues
    List<Notification> findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(Long freelancerId);

    // Compter les notifications non lues
    Long countByFreelancerIdAndIsReadFalse(Long freelancerId);
}
```

---

### 2.3 Service Principal

**Fichier:** `src/main/java/com/example/freelance/service/NotificationService.java`

#### Méthodes principales

| Méthode | Description |
|---------|-------------|
| `createAndSendNotification()` | Crée, sauvegarde et envoie via WebSocket |
| `sendNotificationViaWebSocket()` | Envoi temps réel vers le topic STOMP |
| `getNotificationsByFreelancerId()` | Récupère toutes les notifications |
| `getUnreadNotificationsByFreelancerId()` | Récupère les non lues |
| `countUnreadNotifications()` | Compte les non lues |
| `markAsRead()` | Marque une notification comme lue |
| `markAllAsRead()` | Marque toutes les notifications comme lues |

```java
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FreelancerRepository freelancerRepository;
    private final MissionRepository missionRepository;

    // Créer et envoyer une notification
    public NotificationDTO createAndSendNotification(
            Long freelancerId,
            Long missionId,
            NotificationType type,
            String message) {

        Notification notification = new Notification();
        notification.setFreelancerId(freelancerId);
        notification.setMissionId(missionId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);

        // Sauvegarde en base
        Notification saved = notificationRepository.save(notification);

        // Conversion en DTO
        NotificationDTO dto = NotificationDTO.fromEntity(saved);

        // Envoi via WebSocket
        sendNotificationViaWebSocket(freelancerId, dto);

        // Mise à jour du flag
        saved.setSentViaWebsocket(true);
        notificationRepository.save(saved);

        return dto;
    }

    // Envoi WebSocket
    private void sendNotificationViaWebSocket(Long freelancerId, NotificationDTO dto) {
        String destination = "/topic/notifications/" + freelancerId;
        messagingTemplate.convertAndSend(destination, dto);
    }

    // Marquer comme lu
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
    }

    // Marquer toutes comme lues
    public void markAllAsRead(Long freelancerId) {
        List<Notification> unread = notificationRepository
            .findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
```

---

### 2.4 API REST Controller

**Fichier:** `src/main/java/com/example/freelance/controller/NotificationController.java`

**Base URL:** `/api/notifications`

| Endpoint | Méthode | Description | Réponse |
|----------|---------|-------------|---------|
| `/freelancer/{id}` | GET | Liste toutes les notifications | `List<NotificationDTO>` |
| `/freelancer/{id}/unread` | GET | Liste les notifications non lues | `List<NotificationDTO>` |
| `/freelancer/{id}/count` | GET | Compte les non lues | `{ "unreadCount": number }` |
| `/{id}/read` | PUT | Marque une notification comme lue | `200 OK` |
| `/freelancer/{id}/read-all` | PUT | Marque toutes comme lues | `200 OK` |
| `/create` | POST | Crée une nouvelle notification | `NotificationDTO` |

```java
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationsByFreelancerId(id));
    }

    @GetMapping("/freelancer/{id}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByFreelancerId(id));
    }

    @GetMapping("/freelancer/{id}/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long id) {
        Long count = notificationService.countUnreadNotifications(id);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/freelancer/{id}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long id) {
        notificationService.markAllAsRead(id);
        return ResponseEntity.ok().build();
    }
}
```

---

### 2.5 Configuration WebSocket

**Fichier:** `src/main/java/com/example/freelance/config/WebSocketConfig.java`

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Préfixes pour les destinations du broker
        config.enableSimpleBroker("/topic", "/queue");
        // Préfixe pour les destinations de l'application
        config.setApplicationDestinationPrefixes("/app");
        // Préfixe pour les destinations utilisateur
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // CORS
                .withSockJS();  // Fallback SockJS
    }
}
```

#### Configuration résumée

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| Endpoint WebSocket | `/ws` | Point d'entrée de connexion |
| Message Broker | `/topic`, `/queue` | Préfixes des destinations broker |
| App Destination | `/app` | Préfixe des messages entrants |
| User Destination | `/user` | Préfixe pour messages privés |
| Topic Notifications | `/topic/notifications/{freelancerId}` | Canal par freelancer |
| Fallback | SockJS | Compatibilité navigateurs anciens |

---

### 2.6 Scheduler Automatique

**Fichier:** `src/main/java/com/example/freelance/scheduler/MissionEndReminderScheduler.java`

Ce scheduler envoie automatiquement des rappels aux freelancers 3 jours avant la fin de leur mission.

```java
@Component
public class MissionEndReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MissionEndReminderScheduler.class);

    private final MissionFreelanceRepository missionFreelanceRepository;
    private final NotificationService notificationService;

    // Exécution tous les jours à 9h (production)
    // @Scheduled(cron = "0 0 9 * * *")

    // Exécution toutes les 2 minutes (test)
    @Scheduled(fixedRate = 120000)
    public void sendMissionEndReminders() {
        logger.info("Démarrage du scheduler de rappels de fin de mission");

        // Date cible = aujourd'hui + 3 jours
        LocalDate targetDate = LocalDate.now().plusDays(3);

        // Récupérer les missions se terminant à la date cible
        List<MissionFreelance> missions = missionFreelanceRepository
            .findByDateFinAndNotificationEnvoyeeFalse(targetDate);

        for (MissionFreelance mf : missions) {
            try {
                // Créer le message de notification
                String message = String.format(
                    "Rappel : Votre mission '%s' se termine dans 3 jours (le %s)",
                    mf.getMission().getTitre(),
                    mf.getDateFin()
                );

                // Envoyer la notification
                notificationService.createAndSendNotification(
                    mf.getFreelancer().getId(),
                    mf.getMission().getId(),
                    NotificationType.RAPPEL_FIN_MISSION,
                    message
                );

                // Marquer comme envoyée (évite les doublons)
                mf.setNotificationEnvoyee(true);
                missionFreelanceRepository.save(mf);

                logger.info("Notification envoyée au freelancer {}", mf.getFreelancer().getId());

            } catch (Exception e) {
                // Gestion individuelle des erreurs (une erreur ne bloque pas les autres)
                logger.error("Erreur lors de l'envoi de la notification pour mission {}: {}",
                    mf.getId(), e.getMessage());
            }
        }

        logger.info("Fin du scheduler - {} notifications traitées", missions.size());
    }
}
```

#### Logique du Scheduler

```
┌─────────────────────────────────────────────────────────────────┐
│                  SCHEDULER - FLUX DE TRAITEMENT                  │
└─────────────────────────────────────────────────────────────────┘

1. DÉCLENCHEMENT
   │  Cron: 0 0 9 * * * (tous les jours à 9h)
   │  Test: fixedRate = 120000 (toutes les 2 min)
   ▼
2. CALCUL DATE CIBLE
   │  targetDate = LocalDate.now().plusDays(3)
   ▼
3. REQUÊTE BASE DE DONNÉES
   │  SELECT * FROM mission_freelance
   │  WHERE date_fin = targetDate
   │  AND notification_envoyee = false
   ▼
4. POUR CHAQUE MISSION TROUVÉE
   │
   ├─► Créer message personnalisé
   │
   ├─► notificationService.createAndSendNotification()
   │   │
   │   ├─► Sauvegarde en base
   │   └─► Envoi WebSocket
   │
   ├─► Mettre notification_envoyee = true
   │
   └─► Logger le résultat

5. FIN DU SCHEDULER
```

---

### 2.7 Event Listeners

#### MissionEventListener
**Fichier:** `src/main/java/com/example/freelance/service/MissionEventListener.java`

Écoute les événements de création de mission et notifie les freelancers disponibles.

```java
@Component
public class MissionEventListener {

    private final NotificationService notificationService;
    private final FreelancerRepository freelancerRepository;

    @Async
    @EventListener
    public void handleMissionCreated(MissionCreatedEvent event) {
        Mission mission = event.getMission();

        // Trouver les freelancers disponibles avec compétences correspondantes
        List<Freelancer> freelancers = freelancerRepository
            .findAvailableWithMatchingCompetencies(mission.getCompetencesRequises());

        for (Freelancer freelancer : freelancers) {
            String message = String.format(
                "Nouvelle mission disponible : %s",
                mission.getTitre()
            );

            notificationService.createAndSendNotification(
                freelancer.getId(),
                mission.getId(),
                NotificationType.MISSION_ASSIGNEE,
                message
            );
        }
    }
}
```

#### MissionListener (JPA)
**Fichier:** `src/main/java/com/example/freelance/service/listeners/MissionListener.java`

Listener JPA déclenché après la persistance d'une mission.

```java
public class MissionListener {

    @PostPersist
    public void afterMissionAdded(Mission mission) {
        if (mission.getStatut() == StatutMission.EN_ATTENTE) {
            // Notifier les freelancers disponibles
            // ...
        }
    }
}
```

---

### 2.8 Activation du Scheduling

**Fichier:** `src/main/java/com/example/freelance/FreelanceApplication.java`

```java
@SpringBootApplication
@EnableScheduling  // Active les tâches planifiées
public class FreelanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreelanceApplication.class, args);
    }
}
```

---

## 3. Frontend (React / TypeScript)

### 3.1 Types TypeScript

**Fichier:** `src/types/notification.ts`

```typescript
// Types de notifications
export type NotificationType = 'RAPPEL_FIN_MISSION' | 'MISSION_ASSIGNEE';

// Interface principale
export interface Notification {
  id: number;
  freelancerId: number;
  missionId: number;
  type: NotificationType;
  message: string;
  createdAt: string;
  isRead: boolean;
}

// Réponse du compteur
export interface UnreadCountResponse {
  unreadCount: number;
}
```

---

### 3.2 Service HTTP

**Fichier:** `src/services/notificationService.ts`

Service pour les appels API REST.

```typescript
import axios from 'axios';
import { Notification, UnreadCountResponse } from '../types/notification';

const API_URL = process.env.REACT_APP_BACK_API_URL || 'http://localhost:9091';
const BASE_URL = `${API_URL}/api/notifications`;

export const notificationService = {

  // Récupérer toutes les notifications
  getNotifications: async (freelancerId: number): Promise<Notification[]> => {
    const response = await axios.get(`${BASE_URL}/freelancer/${freelancerId}`);
    return response.data;
  },

  // Récupérer les notifications non lues
  getUnreadNotifications: async (freelancerId: number): Promise<Notification[]> => {
    const response = await axios.get(`${BASE_URL}/freelancer/${freelancerId}/unread`);
    return response.data;
  },

  // Compter les notifications non lues
  getUnreadCount: async (freelancerId: number): Promise<UnreadCountResponse> => {
    const response = await axios.get(`${BASE_URL}/freelancer/${freelancerId}/count`);
    return response.data;
  },

  // Marquer une notification comme lue
  markAsRead: async (notificationId: number): Promise<void> => {
    await axios.put(`${BASE_URL}/${notificationId}/read`);
  },

  // Marquer toutes les notifications comme lues
  markAllAsRead: async (freelancerId: number): Promise<void> => {
    await axios.put(`${BASE_URL}/freelancer/${freelancerId}/read-all`);
  }
};
```

---

### 3.3 Service WebSocket

**Fichier:** `src/services/websocketService.ts`

Service pour la communication temps réel via STOMP/SockJS.

```typescript
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notification } from '../types/notification';

const WS_URL = process.env.REACT_APP_BACK_API_URL || 'http://localhost:9091';

type NotificationCallback = (notification: Notification) => void;

class WebSocketService {
  private client: Client | null = null;
  private callbacks: NotificationCallback[] = [];
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  // Établir la connexion
  connect(freelancerId: number): void {
    if (this.client?.active) {
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(`${WS_URL}/ws`),

      // Configuration heartbeat
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      // Callback de connexion réussie
      onConnect: () => {
        console.log('WebSocket connecté');
        this.reconnectAttempts = 0;

        // Souscrire au topic de notifications
        this.client?.subscribe(
          `/topic/notifications/${freelancerId}`,
          (message: IMessage) => {
            const notification: Notification = JSON.parse(message.body);
            this.notifyCallbacks(notification);
          }
        );
      },

      // Callback de déconnexion
      onDisconnect: () => {
        console.log('WebSocket déconnecté');
      },

      // Gestion des erreurs avec reconnexion
      onStompError: (frame) => {
        console.error('Erreur STOMP:', frame);
        this.handleReconnect(freelancerId);
      }
    });

    this.client.activate();
  }

  // Gestion de la reconnexion
  private handleReconnect(freelancerId: number): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Tentative de reconnexion ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
      setTimeout(() => this.connect(freelancerId), 2000 * this.reconnectAttempts);
    }
  }

  // Déconnexion
  disconnect(): void {
    if (this.client?.active) {
      this.client.deactivate();
    }
    this.client = null;
    this.callbacks = [];
  }

  // Ajouter un callback
  addCallback(callback: NotificationCallback): void {
    this.callbacks.push(callback);
  }

  // Retirer un callback
  removeCallback(callback: NotificationCallback): void {
    this.callbacks = this.callbacks.filter(cb => cb !== callback);
  }

  // Notifier tous les callbacks
  private notifyCallbacks(notification: Notification): void {
    this.callbacks.forEach(callback => callback(notification));
  }

  // Vérifier si connecté
  isActive(): boolean {
    return this.client?.active ?? false;
  }
}

export const websocketService = new WebSocketService();
```

---

### 3.4 Context (Gestion d'état global)

**Fichier:** `src/context/NotificationContext.tsx`

```typescript
import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import { Notification } from '../types/notification';
import { notificationService } from '../services/notificationService';
import { websocketService } from '../services/websocketService';

// Interface du contexte
interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  loading: boolean;
  freelancerId: number | null;
  setFreelancerId: (id: number | null) => void;
  refreshNotifications: () => Promise<void>;
  markAsRead: (notificationId: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
}

// Création du contexte
const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

// Provider
export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [freelancerId, setFreelancerId] = useState<number | null>(null);

  // Rafraîchir les notifications depuis l'API
  const refreshNotifications = useCallback(async () => {
    if (!freelancerId) return;

    setLoading(true);
    try {
      const [notifs, countResponse] = await Promise.all([
        notificationService.getNotifications(freelancerId),
        notificationService.getUnreadCount(freelancerId)
      ]);
      setNotifications(notifs);
      setUnreadCount(countResponse.unreadCount);
    } catch (error) {
      console.error('Erreur lors du chargement des notifications:', error);
    } finally {
      setLoading(false);
    }
  }, [freelancerId]);

  // Gérer une nouvelle notification (WebSocket)
  const handleNewNotification = useCallback((notification: Notification) => {
    // Ajouter au début de la liste
    setNotifications(prev => [notification, ...prev]);

    // Incrémenter le compteur
    setUnreadCount(prev => prev + 1);

    // Afficher un toast
    toast.info(notification.message, {
      position: 'top-right',
      autoClose: 5000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true
    });
  }, []);

  // Marquer une notification comme lue
  const markAsRead = useCallback(async (notificationId: number) => {
    try {
      await notificationService.markAsRead(notificationId);

      setNotifications(prev =>
        prev.map(n =>
          n.id === notificationId ? { ...n, isRead: true } : n
        )
      );

      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Erreur lors du marquage comme lu:', error);
    }
  }, []);

  // Marquer toutes comme lues
  const markAllAsRead = useCallback(async () => {
    if (!freelancerId) return;

    try {
      await notificationService.markAllAsRead(freelancerId);

      setNotifications(prev =>
        prev.map(n => ({ ...n, isRead: true }))
      );

      setUnreadCount(0);
    } catch (error) {
      console.error('Erreur lors du marquage de toutes comme lues:', error);
    }
  }, [freelancerId]);

  // Effet: connexion WebSocket et chargement initial
  useEffect(() => {
    if (freelancerId) {
      // Charger les notifications
      refreshNotifications();

      // Connecter WebSocket
      websocketService.connect(freelancerId);
      websocketService.addCallback(handleNewNotification);

      // Cleanup
      return () => {
        websocketService.removeCallback(handleNewNotification);
        websocketService.disconnect();
      };
    }
  }, [freelancerId, refreshNotifications, handleNewNotification]);

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        loading,
        freelancerId,
        setFreelancerId,
        refreshNotifications,
        markAsRead,
        markAllAsRead
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};

// Hook personnalisé
export const useNotifications = (): NotificationContextType => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
};
```

---

### 3.5 Composants UI

#### Structure des composants

```
src/components/notifications/
├── index.ts              # Barrel exports
├── NotificationBell.tsx  # Icône cloche avec badge
├── NotificationPanel.tsx # Panel dropdown
└── NotificationItem.tsx  # Item individuel
```

#### NotificationBell.tsx

Icône cloche affichée dans la navbar avec badge de compteur.

```typescript
import React, { useState } from 'react';
import { useNotifications } from '../../context/NotificationContext';
import NotificationPanel from './NotificationPanel';

const NotificationBell: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { unreadCount, freelancerId } = useNotifications();

  // Ne pas afficher si pas de freelancerId
  if (!freelancerId) return null;

  return (
    <div className="relative">
      {/* Bouton cloche */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 rounded-full hover:bg-gray-100 transition-colors"
        aria-label="Notifications"
      >
        {/* Icône cloche */}
        <svg
          className={`w-6 h-6 ${unreadCount > 0 ? 'text-blue-600' : 'text-gray-600'}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
          />
        </svg>

        {/* Badge compteur */}
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs
                          font-bold rounded-full h-5 w-5 flex items-center justify-center
                          animate-pulse">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Panel de notifications */}
      {isOpen && <NotificationPanel onClose={() => setIsOpen(false)} />}
    </div>
  );
};

export default NotificationBell;
```

#### NotificationPanel.tsx

Panel dropdown affichant la liste des notifications.

```typescript
import React from 'react';
import { useNotifications } from '../../context/NotificationContext';
import NotificationItem from './NotificationItem';

interface NotificationPanelProps {
  onClose: () => void;
}

const NotificationPanel: React.FC<NotificationPanelProps> = ({ onClose }) => {
  const { notifications, unreadCount, loading, markAllAsRead } = useNotifications();

  return (
    <>
      {/* Overlay pour fermer le panel */}
      <div
        className="fixed inset-0 z-40"
        onClick={onClose}
      />

      {/* Panel */}
      <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl
                      border border-gray-200 z-50 overflow-hidden">

        {/* Header */}
        <div className="px-4 py-3 bg-gray-50 border-b border-gray-200
                        flex justify-between items-center">
          <h3 className="font-semibold text-gray-800">
            Notifications
            {unreadCount > 0 && (
              <span className="ml-2 text-sm text-blue-600">
                ({unreadCount} non lue{unreadCount > 1 ? 's' : ''})
              </span>
            )}
          </h3>

          {unreadCount > 0 && (
            <button
              onClick={markAllAsRead}
              className="text-sm text-blue-600 hover:text-blue-800
                        hover:underline transition-colors"
            >
              Tout marquer comme lu
            </button>
          )}
        </div>

        {/* Contenu */}
        <div className="max-h-96 overflow-y-auto">
          {loading ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2
                            border-blue-600 mx-auto" />
              <p className="mt-2 text-gray-500">Chargement...</p>
            </div>
          ) : notifications.length === 0 ? (
            <div className="p-8 text-center text-gray-500">
              <span className="text-4xl">🔔</span>
              <p className="mt-2">Aucune notification</p>
            </div>
          ) : (
            <div className="divide-y divide-gray-100">
              {notifications.map(notification => (
                <NotificationItem
                  key={notification.id}
                  notification={notification}
                  onClose={onClose}
                />
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        {notifications.length > 0 && (
          <div className="px-4 py-2 bg-gray-50 border-t border-gray-200
                          text-center text-sm text-gray-500">
            {notifications.length} notification{notifications.length > 1 ? 's' : ''}
          </div>
        )}
      </div>
    </>
  );
};

export default NotificationPanel;
```

#### NotificationItem.tsx

Composant pour afficher une notification individuelle.

```typescript
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Notification } from '../../types/notification';
import { useNotifications } from '../../context/NotificationContext';

interface NotificationItemProps {
  notification: Notification;
  onClose: () => void;
}

const NotificationItem: React.FC<NotificationItemProps> = ({ notification, onClose }) => {
  const navigate = useNavigate();
  const { markAsRead } = useNotifications();

  // Formater le temps relatif
  const formatRelativeTime = (dateString: string): string => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return "À l'instant";
    if (diffMins < 60) return `Il y a ${diffMins} min`;
    if (diffHours < 24) return `Il y a ${diffHours}h`;
    if (diffDays < 7) return `Il y a ${diffDays}j`;
    return date.toLocaleDateString('fr-FR');
  };

  // Obtenir l'icône selon le type
  const getIcon = (): string => {
    switch (notification.type) {
      case 'RAPPEL_FIN_MISSION':
        return '⏰';
      case 'MISSION_ASSIGNEE':
        return '📋';
      default:
        return '🔔';
    }
  };

  // Obtenir le label du type
  const getTypeLabel = (): string => {
    switch (notification.type) {
      case 'RAPPEL_FIN_MISSION':
        return 'Rappel';
      case 'MISSION_ASSIGNEE':
        return 'Nouvelle mission';
      default:
        return 'Notification';
    }
  };

  // Obtenir la couleur du badge
  const getBadgeColor = (): string => {
    switch (notification.type) {
      case 'RAPPEL_FIN_MISSION':
        return 'bg-orange-100 text-orange-800';
      case 'MISSION_ASSIGNEE':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Gérer le clic
  const handleClick = async () => {
    if (!notification.isRead) {
      await markAsRead(notification.id);
    }
    onClose();
    // Navigation vers la mission
    if (notification.missionId) {
      navigate(`/missions/${notification.missionId}`);
    }
  };

  return (
    <div
      onClick={handleClick}
      className={`p-4 hover:bg-gray-50 cursor-pointer transition-colors
                  ${!notification.isRead ? 'bg-blue-50' : ''}`}
    >
      <div className="flex items-start gap-3">
        {/* Icône */}
        <span className="text-2xl">{getIcon()}</span>

        {/* Contenu */}
        <div className="flex-1 min-w-0">
          {/* Header avec badge et temps */}
          <div className="flex items-center justify-between mb-1">
            <span className={`text-xs px-2 py-0.5 rounded-full ${getBadgeColor()}`}>
              {getTypeLabel()}
            </span>
            <span className="text-xs text-gray-500">
              {formatRelativeTime(notification.createdAt)}
            </span>
          </div>

          {/* Message */}
          <p className={`text-sm ${!notification.isRead ? 'font-medium' : 'text-gray-600'}`}>
            {notification.message}
          </p>
        </div>

        {/* Indicateur non lu */}
        {!notification.isRead && (
          <span className="w-2 h-2 bg-blue-600 rounded-full flex-shrink-0 mt-2" />
        )}
      </div>
    </div>
  );
};

export default NotificationItem;
```

#### index.ts (Barrel exports)

```typescript
export { default as NotificationBell } from './NotificationBell';
export { default as NotificationPanel } from './NotificationPanel';
export { default as NotificationItem } from './NotificationItem';
```

---

## 4. Flux de Données

### 4.1 Diagramme de flux complet

```
┌─────────────────────────────────────────────────────────────────────┐
│                    FLUX COMPLET DU SYSTÈME                          │
└─────────────────────────────────────────────────────────────────────┘

                      ┌─────────────────────┐
                      │    DÉCLENCHEURS     │
                      └─────────────────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
   │  SCHEDULER  │     │   EVENT     │     │  REST API   │
   │  (9h/jour)  │     │  LISTENER   │     │  (/create)  │
   └─────────────┘     └─────────────┘     └─────────────┘
          │                    │                    │
          └────────────────────┼────────────────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │  NotificationService    │
                  │  createAndSendNotif()   │
                  └─────────────────────────┘
                               │
              ┌────────────────┴────────────────┐
              │                                 │
              ▼                                 ▼
    ┌─────────────────┐              ┌─────────────────┐
    │   PostgreSQL    │              │    WebSocket    │
    │   (persistance) │              │ (temps réel)    │
    └─────────────────┘              └─────────────────┘
                                              │
                                              ▼
                               ┌─────────────────────────┐
                               │  /topic/notifications/  │
                               │     {freelancerId}      │
                               └─────────────────────────┘
                                              │
                                              ▼
                               ┌─────────────────────────┐
                               │   Frontend React        │
                               │   websocketService      │
                               └─────────────────────────┘
                                              │
              ┌────────────────┬──────────────┴───────────┐
              │                │                          │
              ▼                ▼                          ▼
    ┌─────────────┐   ┌─────────────────┐      ┌─────────────────┐
    │    Toast    │   │ NotificationContext │   │  UI Components  │
    │  (alerte)   │   │ (state update)      │   │  (re-render)    │
    └─────────────┘   └─────────────────┘      └─────────────────┘
```

### 4.2 Séquence de connexion WebSocket

```
┌──────────┐          ┌──────────┐          ┌──────────┐
│  Client  │          │  SockJS  │          │  Server  │
│  (React) │          │          │          │ (Spring) │
└────┬─────┘          └────┬─────┘          └────┬─────┘
     │                     │                     │
     │  1. new SockJS(/ws) │                     │
     │────────────────────>│                     │
     │                     │                     │
     │                     │  2. HTTP Upgrade    │
     │                     │────────────────────>│
     │                     │                     │
     │                     │  3. Connection OK   │
     │                     │<────────────────────│
     │                     │                     │
     │  4. STOMP CONNECT   │                     │
     │────────────────────>│────────────────────>│
     │                     │                     │
     │  5. STOMP CONNECTED │                     │
     │<────────────────────│<────────────────────│
     │                     │                     │
     │  6. SUBSCRIBE       │                     │
     │  /topic/notif/{id}  │                     │
     │────────────────────>│────────────────────>│
     │                     │                     │
     │                     │                     │
     │  7. MESSAGE (notif) │                     │
     │<────────────────────│<────────────────────│
     │                     │                     │
```

### 4.3 Séquence d'envoi de notification

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│ Scheduler│     │ Service  │     │   DB     │     │ WebSocket│
└────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │                │
     │ 1. findMissions│                │                │
     │───────────────>│                │                │
     │                │ 2. query       │                │
     │                │───────────────>│                │
     │                │ 3. results     │                │
     │                │<───────────────│                │
     │                │                │                │
     │ 4. createNotif │                │                │
     │───────────────>│                │                │
     │                │ 5. save        │                │
     │                │───────────────>│                │
     │                │ 6. saved       │                │
     │                │<───────────────│                │
     │                │                │                │
     │                │ 7. send        │                │
     │                │───────────────────────────────>│
     │                │                │                │
     │                │ 8. update flag │                │
     │                │───────────────>│                │
     │                │                │                │
```

---

## 5. Base de Données

### 5.1 Schéma de la table `notifications`

```sql
CREATE TABLE notifications (
    id                  BIGSERIAL PRIMARY KEY,
    freelancer_id       BIGINT,
    mission_id          BIGINT,
    type                VARCHAR(50),
    message             TEXT NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    timestamp           TIMESTAMP,
    is_read             BOOLEAN DEFAULT false,
    sent_via_websocket  BOOLEAN DEFAULT false,
    freelancer_ref_id   BIGINT REFERENCES freelancers(id),
    mission_ref_id      BIGINT REFERENCES missions(id)
);

-- Index pour les requêtes fréquentes
CREATE INDEX idx_notifications_freelancer_id ON notifications(freelancer_id);
CREATE INDEX idx_notifications_is_read ON notifications(freelancer_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
```

### 5.2 Table `mission_freelance` (champs liés aux notifications)

```sql
ALTER TABLE mission_freelance ADD COLUMN date_debut DATE;
ALTER TABLE mission_freelance ADD COLUMN date_fin DATE;
ALTER TABLE mission_freelance ADD COLUMN notification_envoyee BOOLEAN DEFAULT false;

-- Index pour le scheduler
CREATE INDEX idx_mission_freelance_date_fin
ON mission_freelance(date_fin)
WHERE notification_envoyee = false;
```

### 5.3 Exemples de données

```sql
-- Exemple de notification
INSERT INTO notifications (freelancer_id, mission_id, type, message, is_read)
VALUES (
    1,
    42,
    'RAPPEL_FIN_MISSION',
    'Rappel : Votre mission "Développement API REST" se termine dans 3 jours (le 15/02/2024)',
    false
);

-- Exemple de mission_freelance avec dates
INSERT INTO mission_freelance (mission_id, freelancer_id, date_debut, date_fin, notification_envoyee)
VALUES (42, 1, '2024-01-15', '2024-02-15', false);
```

---

## 6. Points Forts du Système

| Aspect | Implémentation | Avantages |
|--------|----------------|-----------|
| **Temps réel** | WebSocket STOMP + SockJS fallback | Notifications instantanées, compatibilité navigateurs |
| **Fiabilité** | Persistance DB avant envoi WebSocket | Aucune perte de notification |
| **Anti-doublon** | Flag `notificationEnvoyee` | Pas de spam en cas de redémarrage scheduler |
| **Résilience** | Auto-reconnexion (5 tentatives) | Connexion maintenue malgré interruptions |
| **UX** | Toast, badge, temps relatif | Expérience utilisateur fluide |
| **Maintenabilité** | Séparation claire des responsabilités | Code facile à maintenir et étendre |
| **Typage** | TypeScript côté frontend | Moins de bugs, meilleure documentation |
| **Testabilité** | Services injectables, mocks | Tests unitaires facilités |

---

## 7. Dépendances Techniques

### 7.1 Backend (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>

    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 7.2 Frontend (package.json)

```json
{
  "dependencies": {
    "@stomp/stompjs": "^7.0.0",
    "sockjs-client": "^1.6.1",
    "react-toastify": "^9.1.3",
    "axios": "^1.6.0",
    "react": "^18.2.0",
    "react-router-dom": "^6.0.0"
  },
  "devDependencies": {
    "@types/sockjs-client": "^1.5.4",
    "typescript": "^5.0.0"
  }
}
```

### 7.3 Variables d'environnement

**Frontend (.env)**
```
REACT_APP_BACK_API_URL=http://localhost:9091
```

**Backend (application.properties)**
```properties
server.port=9091
spring.datasource.url=jdbc:postgresql://localhost:5432/freelance
spring.datasource.username=postgres
spring.datasource.password=password
```

---

## 8. Points d'Amélioration Possibles

### 8.1 Sécurité

| Amélioration | Description | Priorité |
|--------------|-------------|----------|
| Auth WebSocket | Ajouter JWT au handshake WebSocket | Haute |
| CORS restrictif | Limiter les origines autorisées | Haute |
| Rate limiting | Limiter le nombre de notifications/minute | Moyenne |
| Validation | Valider les IDs reçus dans les requêtes | Haute |

### 8.2 Scalabilité

| Amélioration | Description | Priorité |
|--------------|-------------|----------|
| Broker externe | RabbitMQ ou Redis pour multi-instances | Haute (prod) |
| Pagination | Paginer les listes de notifications | Moyenne |
| Cache | Cache Redis pour les compteurs | Basse |

### 8.3 Fonctionnalités

| Amélioration | Description | Priorité |
|--------------|-------------|----------|
| Préférences | Permettre de désactiver certains types | Moyenne |
| Email fallback | Envoyer par email si non connecté | Moyenne |
| Regroupement | Grouper les notifications similaires | Basse |
| Son | Option de notification sonore | Basse |

### 8.4 Monitoring

| Amélioration | Description | Priorité |
|--------------|-------------|----------|
| Metrics | Prometheus metrics pour WebSocket | Moyenne |
| Logs structurés | JSON logs pour analyse | Moyenne |
| Health checks | Endpoint de santé WebSocket | Haute |

---

## Annexes

### A. Liste des fichiers du système

#### Backend
```
src/main/java/com/example/freelance/
├── model/
│   ├── Notification.java
│   └── enums/NotificationType.java
├── dto/
│   └── NotificationDTO.java
├── repository/
│   └── NotificationRepository.java
├── service/
│   ├── NotificationService.java
│   ├── MissionEventListener.java
│   └── listeners/MissionListener.java
├── controller/
│   └── NotificationController.java
├── config/
│   └── WebSocketConfig.java
├── scheduler/
│   └── MissionEndReminderScheduler.java
└── FreelanceApplication.java
```

#### Frontend
```
src/
├── types/
│   └── notification.ts
├── services/
│   ├── notificationService.ts
│   └── websocketService.ts
├── context/
│   └── NotificationContext.tsx
└── components/notifications/
    ├── index.ts
    ├── NotificationBell.tsx
    ├── NotificationPanel.tsx
    └── NotificationItem.tsx
```

### B. Exemple d'utilisation

```typescript
// Dans un composant React
import { useNotifications } from '../context/NotificationContext';
import { NotificationBell } from '../components/notifications';

function Header() {
  const { setFreelancerId } = useNotifications();

  // Définir l'ID du freelancer connecté
  useEffect(() => {
    setFreelancerId(currentUser.id);
  }, [currentUser]);

  return (
    <header>
      <nav>
        {/* ... autres éléments ... */}
        <NotificationBell />
      </nav>
    </header>
  );
}
```

---

*Document généré le 30/01/2026*
*Version 1.0*
