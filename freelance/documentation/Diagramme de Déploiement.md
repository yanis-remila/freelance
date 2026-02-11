# Documentation du Diagramme de Déploiement

## Objectif
Ce diagramme représente le processus complet de déploiement de notre application.

---

## 1. Acteurs et Composants

- **Développeur** :
    - L'utilisateur qui écrit, pousse et gère le code source.
    - Rôles principaux : création de **Merge Requests**, revue de code, suivi des pipelines CI/CD.

- **GitLab** :
    - Contient les dépôts de code front-end et back-end.
    - Fournit des fonctionnalités CI/CD pour orchestrer les pipelines de build, test et déploiement.
    - Envoie des notifications sur l'état des pipelines et les résultats des revues de code.

- **GitLab Runner** :
    - Un service indépendant exécutant les tâches CI/CD définies dans GitLab.
    - Effectue les déploiements dans les environnements front-end et back-end.

- **Front-End VM** :
    - Héberge l'application web front-end développée en React.
    - Accessible via HTTP/HTTPS par les utilisateurs.

- **Back-End VM** :
    - Héberge l'API REST Spring Boot, qui est le back-end de l'application.
    - Communique avec le front-end et la base de données.

---

## 2. Flux du Déploiement

1. **Contribution du Développeur** :
    - **Action** : Le développeur pousse du code ou crée une Merge Request dans GitLab (front-end ou back-end).
    - **Retour** : GitLab fournit un retour d'information via des revues de code ou des résultats de pipelines (succès, échec).

2. **Pipeline CI/CD** :
    - GitLab déclenche les pipelines CI/CD après un événement (commit/push, Merge Request).
    - Les tâches sont exécutées par le **GitLab Runner** :
        - Build des artefacts (front-end et/ou back-end).
        - Tests automatiques pour garantir la qualité du code.
        - Déploiement dans les environnements cibles.

3. **Déploiement Automatisé** :
    - **Front-End** : Les artefacts front-end (React) sont déployés sur la **Front-End VM**.
    - **Back-End** : Les artefacts back-end (Spring Boot) sont déployés sur la **API REST VM**.
    - Les mises à jour des pipelines sont synchronisées avec GitLab pour informer les développeurs.

---

## 3. Meilleures Pratiques Implémentées

1. **Séparation des responsabilités** :
    - Dépôts distincts pour le front-end et le back-end.
    - Pipelines indépendants pour permettre une gestion modulaire des déploiements.

2. **Automatisation** :
    - Utilisation de GitLab Runner pour minimiser les interventions manuelles.
    - Les builds, tests et déploiements sont gérés automatiquement via CI/CD.

3. **Retour d'information rapide** :
    - GitLab notifie les développeurs des résultats des pipelines en temps réel.
    - Les statuts de build, tests et déploiements sont facilement accessibles.

4. **Environnements isolés** :
    - Chaque composant (front-end, back-end, base de données) est hébergé sur des machines virtuelles séparées pour des raisons de sécurité et de maintenance.

---

## 4. Diagramme de Déploiement


```plantuml
@startuml
actor "Développeur" as Dev

cloud "GitLab" as GitLab {

  node "Front-End Repository" as GitLabFront {
    [Front-End Code Repository]
    [Front-End CI/CD Pipeline]
  }

  node "Back-End Repository" as GitLabBack {
      [Back-End Code Repository]
      [Back-End CI/CD Pipeline]
  }
}

package "Réseau EPISEN" as EpisenRx {
  node "GitLab Runner VM" as RunnerVM {
    [GitLab Runner]
  }
  
  package "Env Dev" as DevEnv {
    node "Dev Front-End VM" as FrontVM {
      [Dev Front-End Application]
    }
    
    node "Dev Back-End VM" as ApiVM {
      [Dev Back-End Application]
    }
  }
  package "Env PProd" as PPrEnv {
    node "PProd Front-End VM" as PProdFrontVM {
      [PProd Front-End Application]
    }
    
    node "PProd Back-End VM" as PProdApiVM {
      [PProd Back-End Application]
    }
  }
  
  
}

' Actions du Développeur
Dev ---> GitLab : [Push Code / Merge Request]       
GitLab ---> Dev : [Feedback (Code Review, CI Status)]

' GitLab CI/CD Relations
GitLab ---> RunnerVM : [Trigger Pipeline]
RunnerVM ---> ApiVM : [Deploy Dev Back-End Spring Boot API Code]
RunnerVM ---> FrontVM : [Deploy Dev Front-End React Code]
RunnerVM ---> GitLab : [Status Update]
RunnerVM ---> PProdApiVM : [Deploy Pprod Back-End Spring Boot API Code]
RunnerVM ---> PProdFrontVM : [Deploy Pprod Front-End React Code]

@enduml
```

```plantuml
@startuml
' Définition des classes
class RefClient {
    - id: Long
    - nom: String
    - prenom: String
    - email: String
    - missionsPubliees: List<Mission>
    - budget: Double
}
 
class RefFreelancer {
    - id: Long
    - nom: String
    - prenom: String
    - email: String
    - competences: List<Competence>
    - experience: Double
    - missionsPostulees: List<Mission>
    - age: Double
    - gender: Gender
}
 
class Mission {
    - id: Long
    - titre: String
    - description: String
    - budget: Double
    - duree: String
    - statut: MissionStatut
    - client: RefClient
}
 
class RefGerant {
    - id: Long
    - nom: String
    - email: String
    - missionsSuivies: List<Mission>
}
 
class Recommandation {
    - id: Long
    - nom: String
    - description: String
    - freelancers: List<RefFreelancer>
    - missions: List<Mission>
}
 
class Historique {
    - id: Long
    - dateAction: Date
    - description: String
    - missionAssociee: Mission
    - freelancerAssocie: RefFreelancer
    - clientAssocie: RefClient
}
 
class Competence {
    - id: Long
    - nom: String
    - description: String
    - freelancers: List<RefFreelancer>
    - missions: List<Mission>
}
 
' Définition des énumérations
enum Gender {
    homme
    femme
}
 
enum MissionStatut {
    En_attente
    Acceptee
    Terminee
    Refusee
}
 
' Relations avec cardinalités
RefClient "1" -- "0..n" Mission : publie
Mission "1" -- "0..1" RefFreelancer : assignée_à
RefFreelancer "0..n" -- "0..n" Competence : possède
Mission "0..n" -- "0..n" Competence : nécessite
Recommandation "0..n" -- "0..n" RefFreelancer : inclut
Recommandation "0..n" -- "0..n" Mission : inclut
RefGerant "1" -- "0..n" Mission : supervise
Historique "0..n" -- "0..n" Mission : concerne
Historique "0..n" -- "0..n" RefFreelancer : implique
Historique "0..n" -- "0..n" RefClient : concerne
 
@enduml

Dispose d’un menu contextuel

```

---

Cette documentation présente un processus de déploiement conforme aux meilleures pratiques, combinant modularité, automatisation et retour rapide pour les développeurs.
