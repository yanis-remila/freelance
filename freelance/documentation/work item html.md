<style>
  table {
    width: 100%;
    font-size: 12px;
    text-align: justify;
    border-collapse: collapse;
    color: black; /* Définit la couleur de la police en noir */
  }
  th, td {
    padding: 10px;
    border: 1px solid #ddd;
  }
  /* Assurer que les deux dernières colonnes ont la même largeur */
  td:nth-child(2), td:nth-child(3), th:nth-child(2), th:nth-child(3) {
    width: 50%;
  }
</style>

<table>
  <tr>
    <th><strong>Cas d'utilisation</strong></th>
    <th><strong>Description</strong></th>
    <th><strong>Fonctionnalités</strong></th>
  </tr>
  <tr>
    <td><strong>Afficher les freelances recommandés</strong></td>
    <td>
      Ce cas d'utilisation permet au client de visualiser une liste de freelances recommandés. Il se base sur un algorithme qui analyse les informations des freelances pour identifier les meilleurs profils selon les besoins du client.<br/>
      L'algorithme compare les compétences requises pour le projet avec celles des freelances inscrits, puis filtre les candidats selon leur expérience et leur note moyenne.<br/>
      Il s'appuie aussi sur les projets passés pour suggérer des freelances ayant travaillé sur des missions similaires.
    </td>
    <td>
      - Création d'un <strong>mock</strong> pour les freelances à stocker dans une base PostgreSQL.<br/>
      - Développement d'un <strong>algorithme de filtrage collaboratif</strong> comparant les compétences.<br/>
      - Ajout progressif de raffinements comme <strong>l'analyse de l'expérience</strong>, <strong>des notes</strong>, et <strong>des projets passés</strong>.<br/>
      - Implémentation du <strong>calcul du score</strong> de pertinence basé sur : 
      <ul>
        <li>Compétences partagées entre la mission et les freelances.</li>
        <li>Niveau d'expérience pondéré par les années d'expérience.</li>
        <li>Note moyenne dans les missions similaires.</li>
      </ul>
      - Affichage de la liste des freelances recommandés dans le frontend.
    </td>
  </tr>
  <tr>
    <td><strong>Afficher les missions recommandées</strong></td>
    <td>
      Ce cas d'utilisation permet au freelance de visualiser une liste de missions grâce à un algorithme de recommandation.<br/>
      L'algorithme propose des missions correspondant aux compétences et préférences du freelance.<br/>
      Il analyse les compétences du freelance et compare les exigences des missions disponibles pour trouver celles qui lui conviennent le mieux.<br/>
      Il peut aussi tenir compte des missions précédemment acceptées ou des évaluations reçues.
    </td>
    <td>
      - Création d'un <strong>mock</strong> pour les missions à stocker dans une base PostgreSQL.<br/>
      - Identification des missions ayant des exigences proches des projets réalisés par le freelance, basé sur l'historique des projets.<br/>
      - Calcul d'un score de pertinence en combinant : 
      <ul>
        <li>Correspondance des compétences : nombre de compétences communes entre le freelance et la mission.</li>
        <li>Historique pertinent : pondéré par les projets similaires réalisés avec succès.</li>
        <li>Évaluations passées : pondéré par les notes reçues pour des projets similaires.</li>
      </ul>
      - Affichage de la liste des missions recommandées dans l'interface frontend.
    </td>
  </tr>
</table>
