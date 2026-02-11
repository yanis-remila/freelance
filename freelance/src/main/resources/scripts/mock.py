#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import random
import psycopg2
from faker import Faker
from math import ceil

fake = Faker(["fr_FR"])

# =============================================================================
# 1) PARAMÈTRES DE CONNEXION
# =============================================================================
DB_HOST = "172.31.252.97"
DB_PORT = 5432
DB_NAME = "dev_freelance"
DB_USER = "dev_freelance_user"
DB_PASSWORD = "freelance"

# =============================================================================
# 2) VOLUMÉTRIE
# =============================================================================
NB_CLIENTS = 5
NB_FREELANCERS = 36000
NB_GERANTS = 2
NB_MISSIONS = 500
NB_CANDIDATURES = 15
NB_EVALUATIONS = 500

# Taille des batchs (pour les insertions groupées)
BATCH_SIZE = 1000

# =============================================================================
# 3) LISTE GLOBALE DE COMPÉTENCES
# =============================================================================
ALL_COMPETENCES = [
    ("Frontend", "Maîtrise des technologies HTML, CSS, JavaScript, frameworks modernes (React, Angular, Vue.js)"),
    ("HTML/CSS", "Intégration et mise en page web, responsive design"),
    ("React", "Bibliothèque JavaScript pour interfaces utilisateur"),
    ("Angular", "Framework TypeScript pour applications web complexes"),
    ("Vue.js", "Framework JavaScript progressif pour interfaces utilisateur"),
    ("Backend", "Conception d’API RESTful, logique métier côté serveur, gestion des bases de données"),
    ("Node.js", "Plateforme JavaScript côté serveur pour applications évolutives"),
    ("PHP", "Langage de script côté serveur, frameworks MVC (Laravel, Symfony)"),
    ("Python", "Langage polyvalent utilisé en développement web (Django, Flask) et en data science"),
    ("Java", "Langage orienté objet pour applications backend robustes"),
    ("C#", "Langage développé par Microsoft pour applications backend et desktop"),
    ("FullStack", "Compétences à la fois en frontend et backend"),
    ("Android", "Développement d’applications mobiles natives (Java/Kotlin)"),
    ("iOS", "Développement mobile sur iPhone/iPad (Swift, Objective-C)"),
    ("Flutter", "Framework cross-platform mobile (Dart) pour apps iOS/Android"),
    ("React Native", "Framework JavaScript pour développement mobile multiplateforme"),
    ("DevOps", "CI/CD, Docker, Kubernetes, automatisation, infra"),
    ("Cloud Computing", "Maîtrise des services cloud (AWS, Azure, GCP)"),
    ("Machine Learning", "Dév. de modèles prédictifs, Python/Scikit-learn, TensorFlow"),
    ("Data Analysis", "Analyse de données, visualisation, SQL, outils de BI"),
    ("Big Data", "Traitement de grands volumes de données, Hadoop, Spark"),
    ("Cybersecurity", "Sécurité des SI, protection des données, conformité"),
    ("Pentest", "Tests d’intrusion, vulnérabilités, sécurité réseau"),
    ("Blockchain", "Registres distribués, smart contracts, Ethereum"),
    ("Internet of Things (IoT)", "Dispositifs connectés, M2M, capteurs"),
    ("Artificial Intelligence", "Systèmes intelligents, NLP, vision par ordinateur"),
    ("Augmented Reality/Virtual Reality", "Unity, Unreal Engine, apps immersives"),
    ("Project Management", "Gestion de projets IT, Agile/Scrum"),
    ("UI/UX Design", "Conception d'interfaces, prototypage, design thinking"),
    ("Quality Assurance", "Tests logiciels, assurance qualité, automatisation"),
    ("SRE (Site Reliability Engineering)", "Fiabilité des systèmes, monitoring"),
    ("Database Administration", "DB relationnelles/NoSQL, optimisation, tuning"),
    ("Network Administration", "Réseaux, routeurs, switches, sécurité"),
    ("System Administration", "Gestion serveurs Linux/Windows, scripts"),
    ("IT Support", "Assistance technique, résolution de problèmes"),
    ("Technical Writing", "Documentation technique, guides utilisateurs"),
    ("SEO/SEA", "Référencement naturel et payant, marketing en ligne"),
    ("Digital Marketing", "Stratégies marketing web, réseaux sociaux"),
    ("E-commerce", "Plateformes en ligne, solutions de paiement"),
    ("Game Development", "Développement de jeux vidéo, moteurs, gameplay"),
    ("Robotics", "Programmation de robots, automatisation industrielle"),
    ("Embedded Systems", "Systèmes embarqués, microcontrôleurs, firmware"),
    ("IT Consulting", "Conseil en stratégie informatique"),
    ("IT Training", "Formation des utilisateurs, e-learning"),
    ("IT Sales", "Vente de solutions IT, relation client"),
    ("IT Recruitment", "Recrutement de talents IT, gestion RH"),
    ("IT Legal Compliance", "Conformité réglementaire, RGPD"),
    ("IT Financial Management", "Gestion budgétaire projets IT"),
    ("IT Innovation Management", "Gestion de l'innovation, veille techno"),
    ("IT Ethics", "Éthique dans l'utilisation des technologies"),
    ("IT Sustainability", "Pratiques IT durables, éco-conception"),
    ("IT Disaster Recovery", "Reprise après sinistre, continuité"),
    ("IT Change Management", "Gestion du changement technologique"),
    ("IT Vendor Management", "Gestion des fournisseurs IT"),
    ("IT Asset Management", "Gestion des actifs, licences, parc"),
    ("IT Service Management", "ITIL, amélioration continue des services"),
    ("IT Governance", "Gouvernance SI, alignement stratégique"),
    ("IT Risk Management", "Identification et gestion des risques IT"),
    ("IT Audit", "Audit des SI, évaluation de processus"),
    ("IT Procurement", "Achat de solutions et services IT"),
    ("IT Infrastructure Management", "Gestion data centers, virtualisation"),
    ("IT Research and Development", "R&D en technologies de l'info"),
    ("IT Entrepreneurship", "Startups technologiques, innovation"),
    ("IT Policy Development", "Politiques informatiques, directives"),
    ("IT User Experience", "Amélioration de l'UX des systèmes"),
    ("IT Accessibility", "Solutions accessibles, normes d'accessibilité"),
    ("IT Localization", "Adaptation logicielle pour marchés locaux"),
    ("IT Data Privacy", "Protection de la vie privée, RGPD"),
    ("IT Data Governance", "Qualité et intégrité des données"),
    ("IT Data Architecture", "Architecture des données, modélisation"),
    ("IT Data Integration", "Intégration de sources multiples, ETL"),
    ("IT Data Warehousing", "Entrepôts de données, BI"),
    ("IT Data Mining", "Exploration de données, découverte de patterns"),
    ("IT Data Visualization", "Visualisation compréhensible des données"),
    ("IT Data Quality", "Nettoyage, validation de données"),
]

# =============================================================================
# 4) PROFILS FREELANCERS + COMPÉTENCES PAR PROFIL
# =============================================================================

PROFILS_FREELANCERS = [
    "Développement Web",
    "Développement Mobile",
    "DevOps & Cloud",
    "Data Science & IA",
    "Sécurité Informatique",
    "Design UI/UX",
    "Gestion de Projet IT",
    "Analyse de Données",
    "Administration Systèmes & Réseaux",
    "Consultant en Cybersécurité",
    "Spécialiste en Blockchain",
    "Ingénieur en Intelligence Artificielle",
    "Développeur en Réalité Augmentée/Virtuale",
    "Expert en Internet des Objets (IoT)",
    "Formateur IT",
    "Rédacteur Technique",
    "Spécialiste en Marketing Digital",
    "Analyste en Big Data",
    "Architecte Logiciel",
    "Ingénieur en Systèmes Embarqués"
]

# Pour chaque profil, listez les compétences "logiques".
# (Ici, simplifié à titre d'exemple ; adaptez selon vos besoins.)
COMPETENCES_PAR_PROFIL = {
    "Développement Web": [
        "Frontend", "HTML/CSS", "React", "Angular", "Vue.js", "Backend",
        "Node.js", "PHP", "Python", "Java", "C#", "FullStack"
    ],
    "Développement Mobile": [
        "Android", "iOS", "Flutter", "React Native"
    ],
    "DevOps & Cloud": [
        "DevOps", "Cloud Computing", "Database Administration", "System Administration"
    ],
    "Data Science & IA": [
        "Machine Learning", "Data Analysis", "Python", "Big Data", "Artificial Intelligence"
    ],
    "Sécurité Informatique": [
        "Cybersecurity", "Pentest", "Network Administration", "IT Risk Management"
    ],
    "Design UI/UX": [
        "UI/UX Design", "Frontend", "HTML/CSS"
    ],
    "Gestion de Projet IT": [
        "Project Management", "IT Service Management"
    ],
    "Analyse de Données": [
        "Data Analysis", "SQL", "Data Mining", "Data Visualization"
    ],
    "Administration Systèmes & Réseaux": [
        "System Administration", "Network Administration", "IT Infrastructure Management"
    ],
    "Consultant en Cybersécurité": [
        "Cybersecurity", "Pentest", "IT Audit", "IT Legal Compliance"
    ],
    "Spécialiste en Blockchain": [
        "Blockchain", "Smart Contracts", "Cryptography"
    ],
    "Ingénieur en Intelligence Artificielle": [
        "Artificial Intelligence", "Machine Learning", "Deep Learning", "Python"
    ],
    "Développeur en Réalité Augmentée/Virtuale": [
        "Augmented Reality/Virtual Reality", "Unity", "C#", "3D Modeling"
    ],
    "Expert en Internet des Objets (IoT)": [
        "Internet of Things (IoT)", "Embedded Systems", "Node.js"
    ],
    "Formateur IT": [
        "IT Training", "Technical Writing"
    ],
    "Rédacteur Technique": [
        "Technical Writing", "Documentation"
    ],
    "Spécialiste en Marketing Digital": [
        "Digital Marketing", "SEO/SEA"
    ],
    "Analyste en Big Data": [
        "Big Data", "Data Analysis", "Spark", "Hadoop"
    ],
    "Architecte Logiciel": [
        "Software Architecture", "Design Patterns", "FullStack"
    ],
    "Ingénieur en Systèmes Embarqués": [
        "Embedded Systems", "C/C++ Programming", "Firmware Development"
    ],
}

# =============================================================================
# 5) GESTION DU NOMBRE DE COMPÉTENCES PAR EXPÉRIENCE
# =============================================================================
def compute_competence_range(experience: float) -> (int, int):
    """
    Logique pour le nombre de compétences en fonction de l'expérience.
    """
    if experience < 2:
        return (2, 3)
    elif experience < 5:
        return (3, 5)
    elif experience < 8:
        return (5, 8)
    elif experience < 10:
        return (8, 10)
    else:
        return (10, 15)

# =============================================================================
# 6) GESTION D'EMAILS UNIQUES (pour éviter le 'duplicate key' sur email)
# =============================================================================
EMAIL_CACHE = set()

def get_unique_email():
    while True:
        candidate = fake.email()
        if candidate not in EMAIL_CACHE:
            EMAIL_CACHE.add(candidate)
            return candidate

# =============================================================================
# 7) FONCTIONS D'INSERTION
# =============================================================================
def create_clients(cursor, nb_clients=5):
    client_ids = []
    for _ in range(nb_clients):
        nom = fake.last_name().replace("'", "''")
        prenom = fake.first_name().replace("'", "''")
        email = get_unique_email().replace("'", "''")
        budget = round(random.uniform(2000, 15000), 2)

        cursor.execute(f"""
            INSERT INTO clients (nom, prenom, email, budget)
            VALUES ('{nom}', '{prenom}', '{email}', {budget})
            RETURNING id;
        """)
        client_ids.append(cursor.fetchone()[0])
    return client_ids

def create_gerants(cursor, nb_gerants=2):
    gerant_ids = []
    for _ in range(nb_gerants):
        nom = fake.last_name().replace("'", "''")
        prenom = fake.first_name().replace("'", "''")
        email = get_unique_email().replace("'", "''")

        cursor.execute(f"""
            INSERT INTO gerants (nom, prenom, email)
            VALUES ('{nom}', '{prenom}', '{email}')
            RETURNING id;
        """)
        gerant_ids.append(cursor.fetchone()[0])
    return gerant_ids

def bulk_insert_competences(cursor):
    """
    Insère toutes les compétences de ALL_COMPETENCES dans la table `competences` (sans doublon).
    Retourne un dict {nom_competence: id_competence}.
    """
    cursor.execute("SELECT lower(nom), id FROM competences;")
    existing = dict(cursor.fetchall())  # { "frontend": <id>, ... }

    compet_name_to_id = {}

    new_compet_insert_rows = []
    for (comp_name, comp_desc) in ALL_COMPETENCES:
        lower_name = comp_name.lower()
        if lower_name in existing:
            # La compétence existe déjà
            compet_name_to_id[comp_name] = existing[lower_name]
        else:
            # On prépare l'insertion
            comp_name_sql = comp_name.replace("'", "''")
            comp_desc_sql = comp_desc.replace("'", "''")
            new_compet_insert_rows.append((comp_name_sql, comp_desc_sql))

    # Insertion des compétences manquantes (en une seule requête)
    if new_compet_insert_rows:
        values_str = ",".join(
            f"('{row[0]}','{row[1]}')" for row in new_compet_insert_rows
        )
        insert_sql = f"""
            INSERT INTO competences (nom, description)
            VALUES {values_str}
            RETURNING id, nom;
        """
        cursor.execute(insert_sql)
        for (cid, cname) in cursor.fetchall():
            compet_name_to_id[cname] = cid

    # Compléter compet_name_to_id pour celles déjà existantes
    for (comp_name, comp_desc) in ALL_COMPETENCES:
        if comp_name not in compet_name_to_id:
            compet_name_to_id[comp_name] = existing[comp_name.lower()]

    return compet_name_to_id

def bulk_insert_freelancers(cursor, nb_freelancers=1000, batch_size=1000):
    """
    Insertion groupée de `nb_freelancers` freelances.
    Retourne la liste de tous leurs IDs.
    """
    all_freelancer_ids = []
    nb_batches = ceil(nb_freelancers / batch_size)

    for batch_index in range(nb_batches):
        current_batch_size = min(batch_size, nb_freelancers - batch_index * batch_size)
        rows_values = []

        for _ in range(current_batch_size):
            nom = fake.last_name().replace("'", "''")
            prenom = fake.first_name().replace("'", "''")
            email = get_unique_email().replace("'", "''")
            experience = round(random.uniform(0.5, 12.0), 1)
            age = random.randint(20, 60)
            gender = random.choice(["HOMME", "FEMME"])
            profil = random.choice(PROFILS_FREELANCERS)
            rows_values.append((nom, prenom, email, experience, age, gender, profil))

        # Construction de la requête INSERT
        values_str = ",".join(
            f"('{r[0]}','{r[1]}','{r[2]}',{r[3]},{r[4]},'{r[5]}','{r[6]}')"
            for r in rows_values
        )

        insert_sql = f"""
            INSERT INTO freelancers (nom, prenom, email, experience, age, gender, profil)
            VALUES {values_str}
            RETURNING id;
        """
        cursor.execute(insert_sql)
        batch_ids = [row[0] for row in cursor.fetchall()]
        all_freelancer_ids.extend(batch_ids)

    return all_freelancer_ids

def bulk_insert_freelancer_competences(cursor, freelancer_ids, compet_name_to_id):
    """
    Associe à chaque freelance un ensemble de compétences cohérentes avec son expérience (nombre),
    piochées aléatoirement dans TOUTES les compétences.

    (Vous pourriez affiner pour prendre en compte
     le profil du freelance s'il faut être plus réaliste.)
    """
    data_to_insert = []

    # On pourrait filtrer les compétences par profil du freelance,
    # mais ici c'est simplifié : on pioche dans toutes.
    all_compet_names = list(compet_name_to_id.keys())

    for fid in freelancer_ids:
        experience = random.uniform(0.5, 12.0)
        min_comp, max_comp = compute_competence_range(experience)
        nb_to_create = random.randint(min_comp, max_comp)

        chosen_competences = random.sample(all_compet_names, min(nb_to_create, len(all_compet_names)))
        for comp_name in chosen_competences:
            comp_id = compet_name_to_id[comp_name]
            data_to_insert.append((fid, comp_id))

    # Insertion en batch
    nb_rows = len(data_to_insert)
    nb_batches = ceil(nb_rows / BATCH_SIZE)

    for batch_index in range(nb_batches):
        start_i = batch_index * BATCH_SIZE
        end_i = min(start_i + BATCH_SIZE, nb_rows)
        sub_data = data_to_insert[start_i:end_i]
        if not sub_data:
            break

        values_str = ",".join(
            f"({row[0]},{row[1]})" for row in sub_data
        )
        sql = f"""
            INSERT INTO freelancer_competences (freelancer_id, competence_id)
            VALUES {values_str};
        """
        cursor.execute(sql)

# -----------------------------------------------------------------------------
# FONCTIONS POUR CRÉER DES MISSIONS AVEC COMPÉTENCES COHÉRENTES AU PROFIL
# -----------------------------------------------------------------------------

def create_mission_coherente(cursor, client_id, compet_name_to_id):
    """
    Crée UNE mission avec un profil aléatoire (parmi PROFILS_FREELANCERS),
    et lui associe des compétences cohérentes avec ce profil.
    Retourne l'ID de la mission.
    """
    # 1) Choisir un profil de mission
    mission_profile = random.choice(PROFILS_FREELANCERS)

    # 2) Générer titre + description
    titre = f"Mission {mission_profile} - " + fake.word().capitalize().replace("'", "''")
    description = fake.paragraph(nb_sentences=3).replace("'", "''")
    budget = round(random.uniform(800, 5000), 2)
    duree = f"{random.randint(1, 6)} mois"
    statut = random.choice(["EN_ATTENTE", "ACCEPTEE", "REFUSEE"])

    # 3) Insérer la mission (vous pouvez si vous voulez stocker mission_profile
    # dans une colonne 'profil' de la table 'missions'. S'il n'existe pas,
    # commentez simplement la ligne correspondante dans l'INSERT.)
    cursor.execute(f"""
        INSERT INTO missions (titre, description, budget, duree, statut, client_id)
        VALUES ('{titre}', '{description}', {budget}, '{duree}', '{statut}', {client_id})
        RETURNING id;
    """)
    mission_id = cursor.fetchone()[0]

    # 4) Récupérer la liste de compétences associées à ce profil
    #    et en choisir quelques-unes (ex: 3 à 7) au hasard
    if mission_profile in COMPETENCES_PAR_PROFIL:
        compet_names_for_profile = COMPETENCES_PAR_PROFIL[mission_profile]
    else:
        # fallback au cas où non défini
        compet_names_for_profile = list(compet_name_to_id.keys())

    how_many = random.randint(3, 7)
    chosen_comps = random.sample(compet_names_for_profile, min(how_many, len(compet_names_for_profile)))

    # 5) Associer ces compétences à la mission
    for comp_name in chosen_comps:
        if comp_name in compet_name_to_id:
            comp_id = compet_name_to_id[comp_name]
            cursor.execute(f"""
                INSERT INTO mission_competences (mission_id, competence_id)
                VALUES ({mission_id}, {comp_id});
            """)

    return mission_id

# -----------------------------------------------------------------------------
# AUTRES ENTITÉS
# -----------------------------------------------------------------------------

def create_evaluation(cursor, client_id, freelancer_id, mission_id):
    note = round(random.uniform(1, 5), 1)
    commentaire = fake.sentence().replace("'", "''")
    date_eval = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')

    cursor.execute(f"""
        INSERT INTO evaluations (note, commentaire, date_evaluation, client_id, freelancer_id, mission_id)
        VALUES ({note}, '{commentaire}', '{date_eval}', {client_id}, {freelancer_id}, {mission_id});
    """)

def create_candidature(cursor, freelancer_id, mission_id):
    date_candidature = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')
    statut = random.choice(["EN_ATTENTE", "ACCEPTEE", "REFUSEE"])
    commentaire = fake.sentence().replace("'", "''")

    cursor.execute(f"""
        INSERT INTO candidatures (date_candidature, statut, commentaire, freelancer_id, mission_id)
        VALUES ('{date_candidature}', '{statut}', '{commentaire}', {freelancer_id}, {mission_id});
    """)

# =============================================================================
# 8) SCRIPT PRINCIPAL
# =============================================================================
def main():
    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            dbname=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD
        )
    except Exception as e:
        print("Erreur de connexion à la base :", e)
        return

    conn.autocommit = False
    cursor = conn.cursor()

    try:
        # 1) Clients
        client_ids = create_clients(cursor, NB_CLIENTS)

        # 2) Gérants
        gerant_ids = create_gerants(cursor, NB_GERANTS)

        # 3) Compétences (unique)
        compet_name_to_id = bulk_insert_competences(cursor)

        # 4) Freelances (36 000) + emails uniques
        freelancer_ids = bulk_insert_freelancers(cursor, NB_FREELANCERS, BATCH_SIZE)

        # 5) Associer des compétences aux freelances
        bulk_insert_freelancer_competences(cursor, freelancer_ids, compet_name_to_id)

        # 6) Missions cohérentes avec un profil
        mission_ids = []
        for _ in range(NB_MISSIONS):
            cl = random.choice(client_ids)
            mid = create_mission_coherente(cursor, cl, compet_name_to_id)
            mission_ids.append(mid)

        # 7) Évaluations
        for _ in range(NB_EVALUATIONS):
            c = random.choice(client_ids)
            f = random.choice(freelancer_ids)
            m = random.choice(mission_ids)
            create_evaluation(cursor, c, f, m)

        # 8) Candidatures
        for _ in range(NB_CANDIDATURES):
            f = random.choice(freelancer_ids)
            m = random.choice(mission_ids)
            create_candidature(cursor, f, m)

        conn.commit()
        print("Insertion terminée avec succès !")

    except Exception as e:
        conn.rollback()
        print("Erreur pendant l’insertion, rollback :", e)
    finally:
        cursor.close()
        conn.close()

if __name__ == "__main__":
    main()
