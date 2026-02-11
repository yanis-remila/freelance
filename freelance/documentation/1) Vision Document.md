# Vision Document - Plateforme Freelance Linker

## Introduction
La plateforme **Freelance Linker** est une application web conçue pour faciliter la mise en relation entre freelances et clients. Elle permet aux freelances de se connecter avec des entreprises ou des particuliers cherchant leurs compétences, tout en offrant des outils intuitifs pour la gestion des missions.

Cette plateforme s'adresse à un large éventail de secteurs (développement logiciel, design, marketing, rédaction, etc.) et vise à devenir un outil indispensable pour les freelances et les entreprises souhaitant collaborer efficacement.

---

## Objectifs

### Objectif principal :
Créer une plateforme web permettant une collaboration fluide entre freelances et clients.

### Objectifs fonctionnels :
- **Recommandation intelligente de freelances :** Utiliser un algorithme pour suggérer les freelances les plus pertinents en fonction des besoins des clients.
- **Proposition de missions :** Les clients peuvent publier des missions et inviter des freelances spécifiques.
- **Candidature des freelances :** Les freelances peuvent postuler aux offres publiées sur la plateforme.
- **Validation des profils :** Un gérant peut approuver ou rejeter les profils après vérification.

### Objectifs techniques :
- Développer une application web **responsive** fonctionnant sur desktop, tablette, et mobile.
- Offrir une infrastructure évolutive (scalabilité) pour gérer un nombre croissant d'utilisateurs.
- Garantir la sécurité des données sur la plateforme.

---

## Contexte et Enjeux

### Utilisateurs cibles :
1. **Clients :**
    - Entreprises ou particuliers cherchant à externaliser des compétences pour leurs projets.
    - Besoin : trouver rapidement des freelances qualifiés et gérer facilement leurs missions.

2. **Freelances :**
    - Travailleurs indépendants en quête de projets correspondant à leurs compétences.
    - Besoin : postuler à des missions, collaborer efficacement avec les clients, et gérer leurs revenus.

3. **Gérant :**
    - Administrateur responsable de la modération et de la gestion de la plateforme.
    - Besoin : approuver les profils, résoudre les problèmes et garantir la qualité de la communauté.

### Enjeux techniques :
- **Scalabilité :**
    - La plateforme doit pouvoir accueillir un nombre important d'utilisateurs simultanés, avec une performance constante.
- **Sécurité :**
    - Mise en place d’un système de protection des données.

---

## Architecture du Système

### Backend :
- **Technologie :** Spring Boot (Java 21)
- **Fonctionnalités :**
    - Exposition d'APIs REST pour les opérations frontend.
    - Gestion des utilisateurs (authentification, rôles : freelance, client, gérant).
    - Service de recommandation.
    - CRUD.

### Frontend :
- **Technologie :** React
- **Fonctionnalités :**
    - Interface utilisateur moderne et responsive.
    - Tableau de bord pour chaque type d’utilisateur.

### Base de données :
- **Technologie :** PostgreSQL
- **Fonctionnalités :**
    - Stockage.

### Autres composants :
- **CI/CD :** GitLab CI pour l'intégration et le déploiement continus.
- **Infrastructure :** Hébergement sur 3 VMs distinctes pour les environnements (développement, pré-production, production).