### **Contrôle Continu**

---

#### **Question 1 (1 pt)**
Y a-t-il une anomalie dans ce bout de code JSP ? Pourquoi ?

```jsp  
<jsp:forward page="/page.jsp" />  
<% request.getRequestDispatcher("/pageSecondaire.jsp").forward(request, response); %>  
```

#### Solution: 

Oui, il y a une anomalie.

Les deux lignes de code tentent de rediriger la requête vers deux pages différentes, ce qui n'est pas autorisé.

Une seule redirection doit être effectuée dans une même page JSP.
Il faut choisir entre le tag `<jsp:forward>` ou la méthode `RequestDispatcher`.

---

#### **Question 2 (2 pts)**
Soit une application web nommée **ShoppingCart** avec ces deux fichiers :
- `ShoppingCart/customer/addProductToCart.jsp`
- `ShoppingCart/customer/showCart.jsp`

Quel code dans `addProductToCart.jsp` permet d'envoyer la requête cliente à `showCart.jsp` ?

#### Solution:

### Option 1 : Redirection avec `<jsp:redirect>`
```jsp:redirect page="showCart.jsp" /```
Cette méthode redirige le client vers `showCart.jsp` avec une nouvelle requête HTTP.

### Option 2 : Transfert de requête avec `<jsp:forward>`
```jsp:forward page="showCart.jsp" /```
Cette méthode fait un transfert de la requête sur le serveur sans que le client en soit informé (la même requête HTTP est utilisée).


---

#### **Question 3 (1 pt)**
Quelle méthode HTTP est invoquée lorsqu'un utilisateur clique sur un lien ?

#### Solution:

Lorsque l'utilisateur clique sur un lien, la méthode HTTP invoquée est GET.

---

#### **Question 4 (2 pts)**
Que se passe-t-il lorsque la servlet ci-dessous est déployée et qu'un utilisateur appuie plusieurs fois sur le bouton d'actualisation de son navigateur ?

```java  
public class Test extends HttpServlet {  
private Integer nbr = new Random().nextInt();

    public void doGet(HttpServletRequest request, HttpServletResponse response)  
        throws ServletException, IOException {  
        response.getWriter().println(nbr);  
    }  
}  
```

#### Solution:

Lors de chaque actualisation, le même nombre aléatoire sera affiché dans la réponse, car la valeur de `nbr` ne change pas entre les requêtes.

Chaque appui sur le bouton d'actualisation (même avec plusieurs requêtes) retournera la même valeur de `nbr`.

---

#### **Question 5 (3 pts)**
`HttpSessionAttributeListener` est une interface. Celle-ci est informée par le conteneur Web en cas de modification des attributs de la session d'une application Web, par exemple :
- lorsqu’un attribut est **ajouté** dans une session,
- lorsqu’un attribut est **supprimé**,
- ou lorsqu’un attribut est **remplacé** par un autre attribut.

Quel est l’affichage en fenêtre console après l’exécution de la servlet suivante ?

```java  
public class MyAttributeListener implements HttpSessionAttributeListener {

    @Override  
    public void attributeAdded(HttpSessionBindingEvent event) {  
        String attributeName = event.getName();  
        Object attributeValue = event.getValue();  
        System.out.println("Added: " + attributeName + " = " + attributeValue);  
    }  

    @Override  
    public void attributeRemoved(HttpSessionBindingEvent event) {  
        String attributeName = event.getName();  
        Object attributeValue = event.getValue();  
        System.out.println("Removed: " + attributeName + " = " + attributeValue);  
    }  

    @Override  
    public void attributeReplaced(HttpSessionBindingEvent event) {  
        String attributeName = event.getName();  
        Object attributeValue = event.getValue();  
        System.out.println("Replaced: " + attributeName + " = " + attributeValue);  
    }  
}

```

```java
public class AttributListener extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AttributListener() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("url", "localhost:8080");
        session.setAttribute("url", "localhost:8080");
        session.removeAttribute("url");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

```

#### Solution:


Dans le scénario décrit, la servlet AttributListener manipule un attribut de session, ce qui déclenche les événements sur l'écouteur MyAttributeListener. Voici l'analyse de l'exécution :

1. **Ajout de l'attribut `url` à la session** :
    - La première ligne de code `session.setAttribute("url", "localhost:8080")` ajoute l'attribut `url` à la session avec la valeur `"localhost:8080"`. Cela déclenche la méthode `attributeAdded`.
    - **Affichage en console** :
      ```Added: url = localhost:8080```

2. **Remplacement de l'attribut `url`** :
    - La deuxième ligne `session.setAttribute("url", "localhost:8080")` remplace l'attribut `url` par une nouvelle valeur identique. Comme l'attribut existe déjà, cela déclenche l'événement `attributeReplaced`.
    - **Affichage en console** :
      ```Replaced: url = localhost:8080```

3. **Suppression de l'attribut `url`** :
    - La ligne `session.removeAttribute("url")` supprime l'attribut `url` de la session, ce qui déclenche l'événement `attributeRemoved`.
    - **Affichage en console** :
      ```Removed: url = localhost:8080```

### Résumé de l'affichage en console après l'exécution de la servlet :

```Added: url = localhost:8080```  
```Replaced: url = localhost:8080```  
```Removed: url = localhost:8080```


---

#### **Question 6 (2 pts)**

Quelle est l'erreur à ne pas commettre si on surcharge la méthode `service()`?


#### Solution:

L'erreur à ne pas commettre si on surcharge la méthode `service()` dans une servlet est de ne pas appeler la méthode `super.service(request, response)`.

### Explication :
- La méthode `service()` est appelée par le conteneur pour traiter une requête HTTP. Si vous surchargez cette méthode, vous devez toujours appeler `super.service(request, response)` pour garantir que la gestion correcte des requêtes et des réponses se fasse dans le cadre du cycle de vie de la servlet.
- **Sans cet appel**, la requête ne sera pas correctement traitée, ce qui peut entraîner des erreurs ou des comportements inattendus.

### Exemple incorrect :

```java 
@Override
public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// Si on oublie d'appeler super.service(), la requête ne sera pas traitée correctement
// traitement personnalisé ici, mais pas d'appel à super.service()
}
```

### Exemple correct :

```java 
@Override
public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// Traitement personnalisé
super.service(request, response);  // Appel de la méthode de la classe parente
}
```

Ainsi, l'erreur principale est de ne pas appeler `super.service(request, response)` lors de la surcharge de la méthode `service()`.


---

#### **Question 7 (2 pts)**

Comment (avec quelle classe) gérer les sessions avec les servlets?


#### Solution:

Pour gérer les sessions avec les servlets, on utilise la classe **`HttpSession`**. Cette classe permet de suivre l'état de la session de l'utilisateur à travers plusieurs requêtes HTTP.

### Méthodes principales de `HttpSession` :
1. **Création ou récupération d'une session** :
    - Utiliser `HttpServletRequest.getSession()` pour récupérer la session de l'utilisateur. Si la session n'existe pas, elle est créée automatiquement.
    - Exemple :  
      
      ```
      HttpSession session = request.getSession();
      ```

2. **Ajouter un attribut à la session** :
    - Utiliser `setAttribute()` pour stocker des données dans la session.
    - Exemple :  
      
      ```
      session.setAttribute("username", "JohnDoe");
      ```

3. **Récupérer un attribut de la session** :
    - Utiliser `getAttribute()` pour obtenir des données stockées dans la session.
    - Exemple :  
      
      ```
      String username = (String) session.getAttribute("username");
      ```

4. **Supprimer un attribut de la session** :
    - Utiliser `removeAttribute()` pour supprimer un attribut de la session.
    - Exemple :
   
      ```
        session.removeAttribute("username");
      ```

5. **Détruire la session** :
    - Utiliser `invalidate()` pour invalider la session et supprimer toutes les données associées à cette session.
    - Exemple :  
    
   ```
      session.invalidate();
    ```

### Exemple d'utilisation de `HttpSession` :

``` java 
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        response.getWriter().println("Session créée pour : " + username);
    }
}
```

### En résumé :
La classe **`HttpSession`** est utilisée pour gérer les sessions avec les servlets, permettant de stocker, récupérer, supprimer des attributs et gérer le cycle de vie de la session.


---

#### **Question 8 (2 pts)**

Quelles sont les étapes du cycle de vie d'un servlet?

#### Solution:

### Cycle de vie d'un servlet

Le cycle de vie d'un servlet est géré par le conteneur de servlets et se compose des étapes suivantes :

### 1. **Chargement et instanciation** :
- Le servlet est chargé en mémoire par le conteneur lorsque nécessaire (premier appel ou au démarrage du serveur).
- Le conteneur crée une instance du servlet en appelant son constructeur.

### 2. **Initialisation (`init()`)** :
- Le conteneur appelle la méthode `init()` pour initialiser le servlet.
- Cette méthode est appelée une seule fois dans le cycle de vie du servlet.
- Exemple :
  
  ```java 
  @Override
  public void init() throws ServletException {
  // Initialisation
  }
  ```

### 3. **Traitement des requêtes (`service()`)** :
- Le conteneur appelle la méthode `service(HttpServletRequest, HttpServletResponse)` pour traiter chaque requête.
- `service()` détermine le type de requête (GET, POST, etc.) et appelle la méthode correspondante (`doGet()`, `doPost()`, etc.).
- Exemple :

  ```java
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     response.getWriter().println("Requête GET traitée.");
  }
  ```

### 4. **Destruction (`destroy()`)** :
- Avant que le servlet soit retiré (par exemple, lors de l'arrêt du serveur), le conteneur appelle la méthode `destroy()`.
- Cette méthode est utilisée pour libérer les ressources (par exemple, fermer des connexions à une base de données).
- Exemple :
  
  ```java
  @Override
  public void destroy() {
     // Libération des ressources
  }
  ```

### 5. **Suppression de l'objet servlet** :
- Après l'appel à `destroy()`, l'objet du servlet est supprimé de la mémoire.

### En résumé :
1. **Chargement et instanciation**
2. **Initialisation (`init()`)**
3. **Traitement des requêtes (`service()`)**
4. **Destruction (`destroy()`)**
5. **Suppression de l'objet**

Ce cycle garantit que chaque servlet fonctionne de manière efficace tout en libérant les ressources utilisées.


---

#### **Question 9 (1 pts)**

Qu'est-ce qu'une servlet?

1. Une application client
2. Un programme Java s'exécutant sur un serveur
3. Un type de base de données
4. Un langage de programmation

#### Solution:

2. Un programme Java s'exécutant sur un serveur

#### **Question 10 (1 pts)**

Lequel des items est vrai concernant le cycle de vie d'un servlet ?

1. Les servlets sont instanciées à chaque requête
2. Une servlet peut traiter plusieurs requêtes simultanément
3. La méthode service() est appelée avant init()
4. La méthode destroy() est appelée pour chaque requête

#### Solution:

2. Une servlet peut traiter plusieurs requêtes simultanément




