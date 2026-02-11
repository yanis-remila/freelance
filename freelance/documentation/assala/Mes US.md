# 📌 Recommandation de freelances pour une mission

## 📌 Épic : Recommandation de freelances pour une mission

En tant que plateforme, je souhaite recommander des freelances pertinents pour une mission afin d'aider les entreprises à trouver rapidement des professionnels qualifiés.

---

## 📝 US1 : Récupération des détails de la mission cible

**En tant que** système,  
**je veux** récupérer les informations d’une mission donnée, y compris ses compétences requises,  
**afin de** comparer ces compétences avec celles des freelances disponibles.

### ✅ Critères d'acceptation
- La mission est récupérée à partir de son `missionId`.
- Si la mission n'existe pas, un message d’erreur est retourné.
- Les compétences associées à la mission sont extraites correctement.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US2 : Identification des missions similaires

**En tant que** système,  
**je veux** identifier les missions précédentes ayant des compétences similaires à celles de la mission cible,  
**afin de** trouver des freelances ayant déjà travaillé sur des missions comparables.

### ✅ Critères d'acceptation
- Une mission est considérée comme similaire si elle partage au moins `MIN_SIMILAR` compétences avec la mission cible.
- Une liste d'IDs de missions similaires est extraite de la base de données.
- Si aucune mission similaire n'est trouvée, aucune recommandation ne doit être générée.

⏳ **Poids de complexité : 2 jours**

---

## 📝 US3 : Récupération des évaluations des missions similaires

**En tant que** système,  
**je veux** récupérer les évaluations des freelances ayant travaillé sur des missions similaires,  
**afin de** déterminer leur performance et leur pertinence pour la mission cible.

### ✅ Critères d'acceptation
- Seules les évaluations des missions similaires sont récupérées.
- Si aucune évaluation n'existe, aucune recommandation ne doit être générée.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US4 : Regroupement des évaluations par freelance

**En tant que** système,  
**je veux** regrouper les évaluations obtenues par freelance,  
**afin de** faciliter l’analyse de leur performance passée.

### ✅ Critères d'acceptation
- Chaque freelance est identifié par son `freelancerId`.
- Toutes les évaluations associées à un freelance sont regroupées sous son identifiant.
- Si aucun freelance n'a été évalué, aucune recommandation ne doit être générée.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US5 : Récupération des freelances et de leurs compétences

**En tant que** système,  
**je veux** récupérer les freelances ayant travaillé sur des missions similaires ainsi que leurs compétences,  
**afin de** pouvoir comparer leur profil avec la mission cible.

### ✅ Critères d'acceptation
- Seuls les freelances ayant une évaluation sur une mission similaire sont récupérés.
- Les compétences de chaque freelance sont extraites et associées à son profil.
- Si aucun freelance n'est trouvé, aucune recommandation ne doit être générée.

⏳ **Poids de complexité : 2 jours**

---

## 📝 US6 : Calcul de la correspondance des compétences

**En tant que** système,  
**je veux** comparer les compétences des freelances avec celles de la mission cible,  
**afin de** déterminer le niveau d’adéquation de chaque freelance.

### ✅ Critères d'acceptation
- Le nombre de compétences communes entre le freelance et la mission cible est calculé.
- Un freelance n'est considéré que si son nombre de compétences communes est supérieur ou égal à `MIN_SIMILAR`.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US7 : Évaluation de l'expérience des freelances

**En tant que** système,  
**je veux** récupérer l’expérience des freelances,  
**afin de** privilégier ceux ayant une expérience plus significative.

### ✅ Critères d'acceptation
- L’expérience d’un freelance est récupérée à partir de son profil.
- Si l’expérience est nulle, une valeur par défaut de `0.0` est utilisée.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US8 : Calcul de la note moyenne des freelances

**En tant que** système,  
**je veux** calculer la note moyenne des freelances sur les missions similaires,  
**afin de** favoriser ceux ayant de bonnes évaluations.

### ✅ Critères d'acceptation
- La note moyenne d’un freelance est calculée en prenant en compte toutes ses évaluations.
- Si un freelance n’a pas de note, une valeur par défaut de `0.0` est utilisée.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US9 : Calcul du score de recommandation des freelances

**En tant que** système,  
**je veux** attribuer un score à chaque freelance en fonction de ses compétences, de son expérience et de ses évaluations,  
**afin de** proposer les freelances les plus adaptés à la mission cible.

### ✅ Critères d'acceptation
- Le score est calculé selon la formule :  
  `score = (compétences communes * 2.0) + expérience + (note moyenne * 2.0)`.
- Seuls les freelances ayant un score valide sont retenus.

⏳ **Poids de complexité : 2 jours**

---

## 📝 US10 : Tri des freelances recommandés

**En tant que** système,  
**je veux** trier les freelances recommandés en fonction de leur score,  
**afin de** présenter les meilleurs profils en priorité.

### ✅ Critères d'acceptation
- Les freelances sont triés par score décroissant.
- La liste triée est retournée sous forme de recommandations.

⏳ **Poids de complexité : 1 jour**

---

## 📝 US11 : Génération de la liste des recommandations

**En tant que** système,  
**je veux** retourner une liste de freelances recommandés avec leur nom, prénom et score,  
**afin de** fournir une sélection pertinente aux recruteurs.

### ✅ Critères d'acceptation
- Chaque freelance recommandé est représenté par un `FreelancerRecommendationDTO`.
- Si aucune recommandation valide n’est trouvée, une liste vide est retournée.

⏳ **Poids de complexité : 1 jour**

---