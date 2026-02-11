-- ============================================
-- JEU DE DONNEES DE TEST - PLATEFORME FREELANCE
-- Optimise pour tester les notifications de fin de mission
-- ============================================

-- Nettoyage des tables (ordre inverse des dependances)
DELETE FROM candidatures;
DELETE FROM mission_freelance;
DELETE FROM mission_competences;
DELETE FROM missions;
DELETE FROM competences;
DELETE FROM freelancers;
DELETE FROM clients;

-- ============================================
-- CLIENTS (5 clients)
-- ============================================
INSERT INTO clients (id, nom, prenom, email, budget) VALUES
(1, 'Dupont', 'Marie', 'marie.dupont@techcorp.fr', 50000.00),
(2, 'Martin', 'Pierre', 'pierre.martin@startupinno.com', 30000.00),
(3, 'Bernard', 'Sophie', 'sophie.bernard@bigcompany.fr', 100000.00),
(4, 'Petit', 'Lucas', 'lucas.petit@agenceweb.fr', 25000.00),
(5, 'Robert', 'Emma', 'emma.robert@fintech.io', 75000.00);

-- ============================================
-- FREELANCERS (8 freelancers)
-- ============================================
INSERT INTO freelancers (id, nom, prenom, email, experience, age, gender, profil, status) VALUES
(1, 'Leroy', 'Jean', 'jean.leroy@freelance.dev', 5.0, 32, 'HOMME', 'Developpeur Full Stack Java/React', 'IN_MISSION'),
(2, 'Moreau', 'Claire', 'claire.moreau@devmail.com', 8.0, 35, 'FEMME', 'Architecte Cloud AWS', 'IN_MISSION'),
(3, 'Fournier', 'Thomas', 'thomas.fournier@codemail.fr', 3.0, 28, 'HOMME', 'Developpeur Frontend React/Vue', 'IN_MISSION'),
(4, 'Girard', 'Julie', 'julie.girard@techfreelance.com', 10.0, 40, 'FEMME', 'Lead Developer Java/Spring', 'IN_MISSION'),
(5, 'Bonnet', 'Antoine', 'antoine.bonnet@devpro.fr', 6.0, 33, 'HOMME', 'DevOps Engineer', 'IN_MISSION'),
(6, 'Durand', 'Camille', 'camille.durand@webdev.io', 4.0, 29, 'FEMME', 'Designer UX/UI', 'AVAILABLE'),
(7, 'Lambert', 'Nicolas', 'nicolas.lambert@datamail.com', 7.0, 36, 'HOMME', 'Data Scientist Python', 'IN_MISSION'),
(8, 'Rousseau', 'Lea', 'lea.rousseau@mobiledev.fr', 5.5, 31, 'FEMME', 'Developpeuse Mobile iOS/Android', 'AVAILABLE');

-- ============================================
-- COMPETENCES (24 competences liees aux freelancers)
-- ============================================
INSERT INTO competences (id, nom, description, freelancer_id) VALUES
-- Jean Leroy (id=1) - Full Stack
(1, 'Java', 'Developpement backend avec Spring Boot 3.x et Hibernate', 1),
(2, 'React', 'Developpement frontend avec React 18, Hooks et Redux', 1),
(3, 'PostgreSQL', 'Administration et optimisation de bases de donnees PostgreSQL', 1),

-- Claire Moreau (id=2) - Cloud
(4, 'AWS', 'Architecture cloud avec EC2, S3, Lambda, RDS, EKS', 2),
(5, 'Terraform', 'Infrastructure as Code avec Terraform et modules', 2),
(6, 'Kubernetes', 'Orchestration de conteneurs avec K8s et Helm', 2),

-- Thomas Fournier (id=3) - Frontend
(7, 'Vue.js', 'Developpement frontend avec Vue 3 et Pinia', 3),
(8, 'TypeScript', 'Typage statique avec TypeScript 5.x', 3),
(9, 'CSS/Tailwind', 'Stylisation avec Tailwind CSS et animations', 3),

-- Julie Girard (id=4) - Lead Java
(10, 'Spring Boot', 'Expertise Spring Boot 3, Security, WebFlux', 4),
(11, 'Microservices', 'Architecture microservices avec Spring Cloud', 4),
(12, 'Kafka', 'Messaging asynchrone avec Apache Kafka', 4),

-- Antoine Bonnet (id=5) - DevOps
(13, 'Docker', 'Containerisation avec Docker et Docker Compose', 5),
(14, 'CI/CD', 'Pipelines GitLab CI, GitHub Actions, Jenkins', 5),
(15, 'Linux', 'Administration systeme Linux (Debian, Ubuntu, RHEL)', 5),

-- Camille Durand (id=6) - UX/UI
(16, 'Figma', 'Prototypage et design systeme avec Figma', 6),
(17, 'UX Research', 'Recherche utilisateur, personas, journey maps', 6),
(18, 'Design System', 'Creation de design systems et composants', 6),

-- Nicolas Lambert (id=7) - Data
(19, 'Python', 'Data Science avec Python, Pandas, NumPy, Matplotlib', 7),
(20, 'Machine Learning', 'Modeles ML avec Scikit-learn, TensorFlow, PyTorch', 7),
(21, 'SQL', 'Requetes complexes, optimisation, data warehousing', 7),

-- Lea Rousseau (id=8) - Mobile
(22, 'Swift', 'Developpement iOS natif avec SwiftUI', 8),
(23, 'Kotlin', 'Developpement Android natif avec Jetpack Compose', 8),
(24, 'Flutter', 'Developpement cross-platform avec Flutter/Dart', 8);

-- ============================================
-- MISSIONS (10 missions avec differents statuts)
-- ============================================
INSERT INTO missions (id, titre, description, budget, duree, statut, client_id, freelancer_id) VALUES
-- Missions EN_COURS assignees a des freelancers (pour tester les notifications)
(1, 'API REST E-commerce', 'Developpement d''une API REST complete pour plateforme e-commerce avec gestion produits, commandes, paiements Stripe', 15000.00, '3 mois', 'EN_COURS', 1, 1),
(2, 'Migration Infrastructure AWS', 'Migration complete infrastructure on-premise vers AWS avec auto-scaling, monitoring CloudWatch, alerting', 28000.00, '4 mois', 'EN_COURS', 3, 2),
(3, 'Refonte Frontend Application', 'Refonte complete du frontend d''une application SaaS B2B avec Vue.js 3 et TypeScript', 12000.00, '2 mois', 'EN_COURS', 4, 3),
(4, 'Architecture Microservices', 'Conception et implementation d''une architecture microservices avec Spring Cloud, Eureka, Config Server', 35000.00, '6 mois', 'EN_COURS', 3, 4),
(5, 'Pipeline CI/CD Complet', 'Mise en place pipeline CI/CD avec tests automatises, analyse code, deploiement blue-green', 10000.00, '2 mois', 'EN_COURS', 2, 5),
(6, 'Plateforme Analytics ML', 'Developpement plateforme analytics avec dashboards temps reel et modeles predictifs ML', 22000.00, '4 mois', 'EN_COURS', 5, 7),

-- Missions en attente (sans freelancer assigne)
(7, 'Application Mobile Banking', 'Developpement application mobile iOS/Android pour consultation comptes et virements', 30000.00, '5 mois', 'EN_ATTENTE', 5, NULL),
(8, 'Refonte UX/UI Portail Client', 'Redesign complet du portail client avec focus accessibilite et mobile-first', 8000.00, '2 mois', 'EN_ATTENTE', 4, NULL),

-- Mission terminee
(9, 'Site Vitrine Responsive', 'Creation site vitrine moderne responsive avec animations CSS et optimisation SEO', 5000.00, '1 mois', 'TERMINEE', 4, 3),

-- Mission annulee
(10, 'Chatbot IA Support', 'Developpement chatbot IA pour service client avec NLP', 18000.00, '3 mois', 'ANNULEE', 2, NULL);

-- ============================================
-- MISSION_COMPETENCES (liaison missions-competences requises)
-- ============================================
INSERT INTO mission_competences (mission_id, competence_id) VALUES
-- Mission 1: API E-commerce -> Java, PostgreSQL, Spring Boot
(1, 1), (1, 3), (1, 10),
-- Mission 2: Migration AWS -> AWS, Terraform, Kubernetes
(2, 4), (2, 5), (2, 6),
-- Mission 3: Refonte Frontend -> Vue.js, TypeScript, CSS
(3, 7), (3, 8), (3, 9),
-- Mission 4: Microservices -> Spring Boot, Microservices, Kafka
(4, 10), (4, 11), (4, 12),
-- Mission 5: CI/CD -> Docker, CI/CD, Linux
(5, 13), (5, 14), (5, 15),
-- Mission 6: Analytics ML -> Python, ML, SQL
(6, 19), (6, 20), (6, 21),
-- Mission 7: App Mobile -> Swift, Kotlin, Flutter
(7, 22), (7, 23), (7, 24),
-- Mission 8: UX/UI -> Figma, UX Research, Design System
(8, 16), (8, 17), (8, 18);

-- ============================================
-- MISSION_FREELANCE (assignations avec dates)
-- IMPORTANT: Missions qui se terminent dans 3 jours pour tester les notifications!
-- ============================================
INSERT INTO mission_freelance (id, mission_id, freelancer_id, date_debut, date_fin, notification_envoyee) VALUES
-- *** MISSIONS QUI SE TERMINENT DANS 3 JOURS (declenchent les notifications) ***
(1, 1, 1, (CURRENT_DATE - 87)::DATE, (CURRENT_DATE + 3)::DATE, false),  -- Jean Leroy - API E-commerce
(2, 2, 2, (CURRENT_DATE - 117)::DATE, (CURRENT_DATE + 3)::DATE, false), -- Claire Moreau - Migration AWS
(3, 3, 3, (CURRENT_DATE - 57)::DATE, (CURRENT_DATE + 3)::DATE, false),  -- Thomas Fournier - Refonte Frontend

-- *** MISSIONS QUI SE TERMINENT DANS 1 SEMAINE ***
(4, 4, 4, (CURRENT_DATE - 170)::DATE, (CURRENT_DATE + 7)::DATE, false), -- Julie Girard - Microservices

-- *** MISSIONS QUI SE TERMINENT DANS 2 SEMAINES ***
(5, 5, 5, (CURRENT_DATE - 46)::DATE, (CURRENT_DATE + 14)::DATE, false), -- Antoine Bonnet - CI/CD

-- *** MISSION QUI SE TERMINE DANS 1 MOIS ***
(6, 6, 7, (CURRENT_DATE - 90)::DATE, (CURRENT_DATE + 30)::DATE, false), -- Nicolas Lambert - Analytics

-- *** MISSION TERMINEE (notification deja envoyee) ***
(7, 9, 3, (CURRENT_DATE - 60)::DATE, (CURRENT_DATE - 30)::DATE, true);  -- Thomas Fournier - Site Vitrine (terminee)

-- ============================================
-- CANDIDATURES (freelancers qui postulent aux missions)
-- ============================================
INSERT INTO candidatures (id, date_candidature, statut, commentaire, freelancer_id, mission_id) VALUES
-- Candidatures pour la mission "Application Mobile Banking" (id=7)
(1, NOW() - INTERVAL '5 days', 'EN_ATTENTE', 'Tres interesse par ce projet bancaire. J''ai 5 ans d''experience en developpement mobile.', 8, 7),

-- Candidatures pour la mission "Refonte UX/UI" (id=8)
(2, NOW() - INTERVAL '3 days', 'EN_ATTENTE', 'Mon expertise UX/UI correspond parfaitement a ce projet. Portfolio disponible sur demande.', 6, 8),

-- Candidatures acceptees (historique)
(3, NOW() - INTERVAL '90 days', 'ACCEPTEE', 'Disponible immediatement pour commencer le projet.', 1, 1),
(4, NOW() - INTERVAL '120 days', 'ACCEPTEE', 'Expert AWS certifie, je peux gerer cette migration.', 2, 2),
(5, NOW() - INTERVAL '60 days', 'ACCEPTEE', 'Specialiste Vue.js avec plusieurs projets similaires.', 3, 3),

-- Candidature refusee (historique)
(6, NOW() - INTERVAL '95 days', 'REFUSEE', 'Je souhaite postuler pour cette mission API.', 4, 1);

-- ============================================
-- Reset des sequences PostgreSQL
-- ============================================
SELECT setval('clients_id_seq', (SELECT COALESCE(MAX(id), 1) FROM clients));
SELECT setval('freelancers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM freelancers));
SELECT setval('competences_id_seq', (SELECT COALESCE(MAX(id), 1) FROM competences));
SELECT setval('missions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM missions));
SELECT setval('mission_freelance_id_seq', (SELECT COALESCE(MAX(id), 1) FROM mission_freelance));
SELECT setval('candidatures_id_seq', (SELECT COALESCE(MAX(id), 1) FROM candidatures));
