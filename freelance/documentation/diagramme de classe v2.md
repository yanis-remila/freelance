````plantuml
@startuml
class Candidature {
    Long id
    Date dateCandidature
    CandidatureStatut statut
    String commentaire
    Freelancer freelancer
    Mission mission
}

class Client {
    Long id
    String nom
    String prenom
    String email
    Double budget
}

class Competence {
    Long id
    String nom
    String description
}

class Evaluation {
    Long id
    Double note
    String commentaire
    Date dateEvaluation
    Client client
    Freelancer freelancer
    Mission mission
}

class Freelancer {
    Long id
    String nom
    String prenom
    String email
    Double experience
    Integer age
    Gender gender
    String profil
    Set<Competence> competences
}

class Gerant {
    Long id
    String nom
    String prenom
    String email
}

class Historique {
    Long id
    Date dateAction
    String description
    Mission mission
    Client clientAssocie
    Freelancer freelancerAssocie
    Gerant gerantAssocie
}

class Mission {
    Long id
    String titre
    String description
    Double budget
    String duree
    MissionStatut statut
    Client client
    Set<Competence> competences
}

class Recommandation {
    Long id
    String nom
    String description
    Double score
    Freelancer freelancer
    Mission mission
}

class Validation {
    Long id
    Date dateValidation
    String commentaire
    ValidationStatut statut
    Client client
    Freelancer freelancer
    Gerant gerant
}

enum CandidatureStatut {
    EN_ATTENTE
    ACCEPTEE
    REFUSEE
}

enum Gender {
    HOMME
    FEMME
}

enum MissionStatut {
    EN_ATTENTE
    ACCEPTEE
    TERMINEE
    REFUSEE
}

enum ValidationStatut {
    EN_ATTENTE
    VALIDE
    REFUSE
}

Freelancer "1" -- "0..*" Candidature
Mission "1" -- "0..*" Candidature
Freelancer "1" -- "0..*" Evaluation
Client "1" -- "0..*" Evaluation
Mission "1" -- "0..*" Evaluation
Freelancer "1" -- "0..*" Validation
Client "1" -- "0..*" Validation
Gerant "1" -- "0..*" Validation
Mission "1" -- "0..*" Historique
Client "0..1" -- "0..*" Historique
Freelancer "0..1" -- "0..*" Historique
Gerant "0..1" -- "0..*" Historique
Freelancer "1" -- "0..*" Recommandation
Mission "1" -- "0..*" Recommandation
Mission "1" -- "0..*" Competence
Freelancer "1" -- "0..*" Competence
Mission "1" -- "1" Client
@enduml

````