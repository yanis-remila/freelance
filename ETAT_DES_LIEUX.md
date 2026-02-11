# Freelance Linker Platform - État des lieux

## Vue d'ensemble

Plateforme de mise en relation freelances/clients, construite en architecture microservices.

---

## Stack technique

| Couche | Technologie | Port |
|--------|-------------|------|
| Backend principal | Java 21, Spring Boot 3.3.5, Maven | 9091 |
| Microservice notifications | Spring Boot, Kafka consumer | 9092 |
| Frontend | React 18 + TypeScript, Tailwind CSS | 3000 |
| Base de données (freelance) | PostgreSQL 15 | 5432 |
| Base de données (notifications) | PostgreSQL 15 | 5433 |
| Message broker | Apache Kafka | 9093 |
| Zookeeper | confluentinc/cp-zookeeper 7.5.0 | 2181 |
| Kafka UI | provectuslabs/kafka-ui | 8090 |
| Infrastructure | Docker Compose | — |

---

## Structure du projet

```
freelance/
├── freelance/                    # Backend principal (Spring Boot)
├── freelance-front/              # Frontend (React/TypeScript)
├── notification-service/         # Microservice notifications (Spring Boot)
├── docker-compose.yml            # Infrastructure Docker
├── ARCHITECTURE_KAFKA_MICROSERVICES.drawio
├── DIAGRAMME_FIN_MISSION.drawio
├── DIAGRAMME_NOTIFICATIONS.drawio
├── DIAGRAMME_SEQUENCE_TRAVAIL.drawio
├── PRESENTATION_NOTIFICATIONS.md
└── NOTIFICATION_WEBSOCKET_DOCUMENTATION.md
```

---

## Architecture & fonctionnement

```
Frontend React (3000)
    ↕ REST API + WebSocket
Backend Spring Boot (9091)
    ↕ Kafka events
Notification Service (9092)
    ↕
PostgreSQL (5432 / 5433)
```

### Flux principal

1. Les **clients** publient des **missions** avec des compétences requises
2. Les **freelances** postulent via des **candidatures**
3. Un **algorithme de recommandation** suggère les meilleurs freelances pour une mission
4. Une fois assigné, un **événement Kafka** (`mission-assigned`) déclenche une notification
5. Un **scheduler** vérifie chaque jour à 9h les missions se terminant dans 3 jours et publie un événement `mission-ending`
6. Le **service de notifications** consomme ces événements Kafka, les persiste en base, et les envoie en **temps réel via WebSocket** (STOMP/SockJS)
7. Le frontend affiche les notifications via une **cloche** avec compteur et panneau déroulant

### Topics Kafka

| Topic | Description |
|-------|-------------|
| `mission-ending` | Rappel 3 jours avant la fin d'une mission |
| `mission-assigned` | Notification d'assignation d'un freelance à une mission |

---

## Entités principales

| Entité | Description |
|--------|-------------|
| Freelancer | Profil du freelance avec compétences |
| Client | Entreprise ou personne publiant des missions |
| Mission | Projet avec dates, compétences requises, statut |
| Competence | Compétence technique ou métier |
| Candidature | Postulation d'un freelance à une mission |
| Evaluation | Note et commentaire sur une mission terminée |
| Notification | Alerte temps réel (fin de mission, assignation) |
| MissionFreelance | Relation mission-freelance (assignation) |
| MissionCompetence | Compétences requises par mission |
| Recommandation | Suggestion algorithmique de freelances |
| FreelancerReport | Rapport d'activité freelance |
| Historique | Historique des actions |
| Validation | Validation de mission |
| Platform | Configuration plateforme |
| Gerant | Gestionnaire de la plateforme |

---

## Architecture backend (couches)

```
Controller Layer (REST API)
├── FreelanceController
├── MissionController
├── ClientController
├── NotificationController
├── EvaluationController
├── RecommendationController
├── PlatformController
├── FreelancerReportController
└── TestNotificationController

Service Layer (logique métier)
├── FreelanceService
├── MissionService
├── EvaluationService
├── NotificationService
├── RecommendationService
├── PlatformService
├── FreelancerReportService
├── MissionEventProducer (Kafka)
└── MissionEventListener

Repository Layer (accès données)
├── FreelancerRepository
├── ClientRepository
├── MissionRepository
├── CompetenceRepository
├── CandidatureRepository
├── EvaluationRepository
├── NotificationRepository
└── ...
```

### Configuration backend

- **KafkaProducerConfig** : Bootstrap servers `localhost:9093`, sérialisation JSON, acks=all, retries=3
- **WebSocketConfig** : Endpoint `/ws`, broker in-memory, préfixes `/topic`, `/queue`, `/app`, CORS `*`
- **Profils Spring** : `default`, `dev`, `pprod`, `prod`

---

## Architecture frontend

### Pages

```
src/pages/
├── LoginPage.tsx
├── mission/
│   ├── MissionPage.tsx
│   └── ClientMissionsPage.tsx
├── client/
│   └── ClientPage.tsx
└── freelance/
    ├── FreelancePage.tsx
    └── FreelanceMission.tsx
```

### Composants

```
src/components/
├── freelance/
│   └── FreelanceGride.tsx
└── notifications/
    ├── NotificationBell.tsx
    ├── NotificationItem.tsx
    └── NotificationPanel.tsx
```

### Services & contexte

- **notificationService.ts** : Communication HTTP avec l'API notifications
- **websocketService.ts** : Connexion WebSocket/STOMP
- **NotificationContext.tsx** : État global des notifications (liste, compteur non lus, WebSocket)
- **Client API auto-généré** : Types et services TypeScript générés depuis le Swagger du backend (`src/api/`)

### Variables d'environnement

```
REACT_APP_BACK_API_URL=http://localhost:9091
REACT_APP_NOTIFICATION_API_URL=http://localhost:9092
```

Fichiers disponibles : `.env`, `.env.dev`, `.env.pprod`, `.env.prod`

---

## État actuel (février 2026)

| Composant | État | Détails |
|-----------|------|---------|
| Backend principal | Compilé | JAR ~81 Mo dans `freelance/target/` |
| Frontend | Build OK | `node_modules/` installés, `build/` présent |
| Service notifications | Code source uniquement | Pas de JAR compilé |
| Infrastructure Docker | Configurée | `docker-compose.yml` prêt, non lancé |
| Données de test | Présentes | `data.sql` : 5 clients, 8 freelances, 24+ compétences |

---

## Données de test (data.sql)

- 5 clients exemples
- 8 freelances exemples
- 24+ compétences
- Missions et relations mission-freelance pré-configurées

---

## Commandes de lancement

### 1. Infrastructure (Docker)

```bash
docker-compose up -d
```

Lance : Kafka, Zookeeper, PostgreSQL (notifications), Kafka UI.

### 2. Backend principal

```bash
cd freelance
mvn spring-boot:run
```

Accessible sur `http://localhost:9091`

### 3. Service notifications

```bash
cd notification-service
mvn spring-boot:run
```

Accessible sur `http://localhost:9092`

### 4. Frontend

```bash
cd freelance-front
npm start
```

Accessible sur `http://localhost:3000`

### Commandes utiles

```bash
# Générer les types API depuis le Swagger
cd freelance-front && npm run generate:api-types

# Build production frontend
cd freelance-front && npm run build

# Build backend
cd freelance && mvn clean install

# Tests backend
cd freelance && mvn test

# Arrêter l'infra Docker
docker-compose down
```

---

## Documentation existante

| Fichier | Contenu |
|---------|---------|
| `PRESENTATION_NOTIFICATIONS.md` | Présentation du système de notifications |
| `NOTIFICATION_WEBSOCKET_DOCUMENTATION.md` | Guide technique WebSocket |
| `ARCHITECTURE_KAFKA_MICROSERVICES.drawio` | Diagramme architecture Kafka |
| `DIAGRAMME_FIN_MISSION.drawio` | Diagramme fin de mission |
| `DIAGRAMME_NOTIFICATIONS.drawio` | Diagramme notifications |
| `DIAGRAMME_SEQUENCE_TRAVAIL.drawio` | Diagramme de séquence |
| `freelance/documentation/` | Documentation technique backend |
