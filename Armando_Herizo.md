# Audit et plan de refactorisation - VINA AKOHO

Date de revue : 2026-07-02

Ce document liste les modifications conseillees pour rendre le projet plus stable, plus propre et plus facile a maintenir. Il ne modifie pas le code : c'est une feuille de route de refactorisation et de correction.

## Resume executif

Le projet fonctionne, mais il melange encore plusieurs styles : pages Thymeleaf, API JSON, scripts statiques, SQL manuel, configuration locale et logique metier. La priorite n'est pas de tout refaire, mais de stabiliser les points qui cassent souvent :

- configuration Spring et base de donnees ;
- securite/session et acces par role ;
- routes et liens frontend ;
- migrations SQL et donnees de test ;
- logique stock FIFO et transactions ;
- tests automatises ;
- organisation des modules.

### 2. Desactiver `spring.sql.init.mode=always` en usage normal

Fichier concerne : `src/main/resources/application.properties`

Probleme :

- `data.sql` est rejoue a chaque demarrage.
- Si les donnees existent deja mais avec des IDs differents, cela peut casser ou produire des incoherences.
- Les donnees de demo et les donnees obligatoires sont melangees.

Correction conseillee :

- Mettre les donnees obligatoires dans une migration SQL.
- Mettre les donnees de demo dans un profil `dev`.
- Mettre les donnees de test dans `src/test/resources`.
- Remplacer `spring.sql.init.mode=always` par `never` en profil normal.

### 3. Externaliser les identifiants et secrets

Fichier concerne : `src/main/resources/application.properties`

Probleme :

- Mot de passe PostgreSQL en clair : `spring.datasource.password=vinakoho`.
- Secret JWT en clair : `jwt.secret=...`.
- URL locale codee en dur.

Correction conseillee :

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/vinakoho}
spring.datasource.username=${DB_USERNAME:vinakoho}
spring.datasource.password=${DB_PASSWORD:vinakoho}
jwt.secret=${JWT_SECRET:dev-only-secret}
```

Ajouter ensuite un `application-dev.properties`, `application-test.properties` et `application-prod.properties`.

### 4. Reparer le Maven Wrapper

Fichiers concernes : `mvnw`, `.mvn/wrapper/maven-wrapper.properties`

Probleme :

- `./mvnw test` echoue car `.mvn/wrapper/maven-wrapper.properties` manque.
- Toute personne qui clone le projet doit avoir Maven installe localement.

Correction conseillee :

- Regenerer le wrapper Maven :

```bash
mvn -N wrapper:wrapper
```

- Committer le dossier `.mvn/wrapper/`.

### 5. Remplacer les liens `href="#"` par de vraies routes

Fichiers concernes :

- `src/main/resources/templates/dashboard/achats/index.html`
- `src/main/resources/templates/dashboard/commercial/index.html`
- `src/main/resources/templates/dashboard/comptabilite/index.html`
- `src/main/resources/templates/dashboard/production/index.html`
- `src/main/resources/templates/dashboard/stock/index.html`
- `src/main/resources/templates/layout/*.html`

Probleme :

- Plusieurs boutons et menus ne font rien.
- L'utilisateur a l'impression que "rien ne marche".

Correction conseillee :

- Remplacer chaque `href="#"` par une route existante.
- Si la page n'existe pas encore, afficher un bouton desactive ou creer une page placeholder propre.
- Ajouter un test de fumee qui verifie que les liens principaux retournent `200` ou `302` attendu.

### 6. Clarifier les chemins statiques

Fichiers concernes :

- templates Thymeleaf ;
- `src/main/resources/static/**` ;
- `src/main/java/.../config/WebConfig.java`.

Probleme :

- Le projet utilise a la fois `/css/style.css`, `/static/css/style.css`, `/assets/js/...`, `/static/assets/js/...`.
- Cela augmente les risques de 404 ou d'assets bloques par le filtre de session.

Correction conseillee :

- Choisir une convention unique, idealement :

```html
th:href="@{/css/style.css}"
th:src="@{/js/auth.js}"
th:src="@{/js/app-shell.js}"
th:src="@{/VINA_AKOHO_LOGO.png}"
```

- Garder temporairement `/static/**` comme compatibilite, puis supprimer cette compatibilite quand tous les templates sont uniformises.

## Priorite P1 - Securite et sessions

### 7. Remplacer le filtre maison par Spring Security

Fichier concerne : `SessionFilter.java`

Probleme :

- Le filtre protege seulement la presence d'une session.
- Il ne verifie pas les roles par route.
- Il n'y a pas de gestion CSRF standard pour les formulaires.
- Les routes publiques sont codees a la main.

Correction conseillee :

- Ajouter Spring Security.
- Definir les acces :
  - `/admin/**` : Administrateur ;
  - `/production/**` : Responsable production ou admin ;
  - `/stock/**` : Gestionnaire stock ou admin ;
  - `/commercial/**`, `/ventes/**`, `/clients/**` : Responsable commercial ou admin ;
  - `/comptabilite/**` : Comptable ou admin.
- Utiliser un `UserDetailsService` connecte a `EmployeRepository`.
- Garder BCrypt.

### 8. Supprimer l'authentification fake cote JavaScript

Fichiers concernes :

- `src/main/resources/static/js/auth.js`
- `src/main/resources/static/assets/js/auth.js`

Probleme :

- Un mot de passe demo `"123"` existe cote navigateur.
- Le role est gere dans `localStorage/sessionStorage`, donc facile a modifier.
- Cela peut donner une fausse impression de securite.

Correction conseillee :

- Ne pas determiner les droits depuis le JavaScript.
- Laisser le backend gerer la session et le role.
- Utiliser le JS seulement pour l'ergonomie UI.

### 9. Retirer les valeurs par defaut dangereuses

Exemples :

- `VenteController` utilise `idEmploye = 1` si la session ne contient rien.
- `SortieProduitService` cree une unite par defaut `Kg` si aucune unite n'existe.
- Des IDs fixes apparaissent encore dans certains formulaires ou scripts.

Correction conseillee :

- Refuser l'action si l'utilisateur n'est pas authentifie.
- Creer les donnees de reference au demarrage via migration.
- Ne jamais inventer silencieusement des donnees metier critiques.

## Priorite P1 - Base de donnees et SQL

### 10. Centraliser les scripts SQL

Probleme :

- Il existe plusieurs scripts a la racine : `data.sql`, `correctif-*.sql`, `20260630-*.sql`, etc.
- Il existe aussi `src/main/resources/data.sql`.
- Difficile de savoir quel script doit etre execute et dans quel ordre.

Correction conseillee :

- Creer un dossier unique :

```text
src/main/resources/db/migration/
```

- Deplacer les scripts versionnes dedans si Flyway est choisi.
- Garder les scripts de test dans `docs/sql/` ou `src/test/resources/sql/`.
- Supprimer ou archiver les doublons une fois les migrations validees.

### 11. Uniformiser les IDs

Probleme :

- Certaines entites utilisent `Integer`, d'autres `Long`.
- Exemple : `Produit.id` est `Long`, `MatierePremiere.id` est `Integer`, `Vente.id` est `Long`.

Correction conseillee :

- Choisir une convention globale.
- Pour un projet qui grandit, preferer `Long` pour les IDs.
- Migrer module par module, pas tout d'un coup.

### 12. Ajouter des contraintes metier en base

Contraintes conseillees :

- quantites toujours positives ;
- prix toujours positifs ;
- numero de facture unique ;
- email employe unique et non nul ;
- telephone client unique parmi les clients non supprimes ;
- `quantite_restante <= quantite_initiale` ;
- dates de peremption coherentes avec date de fabrication/achat.

Objectif :

- Eviter que des erreurs applicatives corrompent la base.

## Priorite P1 - Stock, ventes et production

### 13. Securiser le FIFO contre les ventes concurrentes

Fichiers concernes :

- `SortieProduitService`
- `EntreeProduitService`
- `LotProduitRepository`
- `LotMpRepository`

Probleme :

- Deux utilisateurs peuvent valider une vente ou une production en meme temps.
- Les deux peuvent lire le meme stock disponible avant que l'autre transaction ne commit.
- Risque : stock negatif ou double allocation du meme lot.

Correction conseillee :

- Utiliser un verrou pessimiste sur les lots selectionnes FIFO :

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

- Ou executer une requete SQL `FOR UPDATE`.
- Ajouter des tests de concurrence.

### 14. Stabiliser la generation des factures

Fichier concerne : `VenteService`

Probleme :

- Le numero de facture est base sur `System.currentTimeMillis()`.
- Ce n'est pas tres lisible, pas lie a l'annee/mois, et pas garanti comme sequence metier propre.

Correction conseillee :

- Ajouter une sequence ou table de numerotation.
- Format propose :

```text
FACT-2026-000001
```

- Ajouter une contrainte unique sur `facture.numero`.

### 15. Clarifier les mouvements de stock

Probleme :

- Les types de mouvement sont des chaines : `"Sortie"`, `"Entree"`.
- Une faute d'orthographe casse la logique.

Correction conseillee :

- Creer un enum Java : `ENTREE`, `SORTIE`, `AJUSTEMENT`.
- Garder une table de reference si necessaire pour l'affichage.
- Utiliser des constantes centralisees si l'enum n'est pas possible tout de suite.

### 16. Corriger les valeurs temporaires dans les pages

Exemple :

- `dashboard/production/entree-produit.html` contient `const idEmploye = 3`.

Correction conseillee :

- Injecter l'employe connecte depuis la session backend.
- Ou exposer un endpoint `/api/session/me`.

## Priorite P1 - Architecture Spring

### 17. Separer clairement MVC HTML et API JSON

Probleme :

- Certains controllers sous `/api/...` retournent des vues HTML.
- Exemple : `ProduitController` et `CategorieController` utilisent `/api/produits` mais affichent des templates.

Correction conseillee :

- Convention proposee :
  - `ProduitViewController` : routes HTML `/produits`.
  - `ProduitRestController` : routes JSON `/api/produits`.
- Faire pareil pour categories, clients, ventes, matieres premieres.

### 18. Eviter l'acces direct aux repositories depuis les controllers

Exemple :

- `VenteController` utilise directement `ClientRepository`, `ProduitRepository`, `ModePaiementRepository`.

Correction conseillee :

- Les controllers doivent appeler des services.
- Les services doivent orchestrer les repositories.
- Cela simplifie les tests et la logique metier.

### 19. Creer des mappers DTO dedies

Probleme :

- Plusieurs conversions entite -> DTO sont faites directement dans les services.
- Cela grossit les services.

Correction conseillee :

- Creer des classes `ClientMapper`, `ProduitMapper`, `VenteMapper`.
- Ou utiliser MapStruct plus tard.

### 20. Nettoyer les exceptions

Fichier concerne : `GlobalExceptionHandler`

Problemes :

- `ex.printStackTrace()` doit etre remplace par un logger.
- `@RestControllerAdvice` renvoie du JSON meme pour certaines erreurs MVC HTML.
- Certaines exceptions sont tres generiques : `RuntimeException`, `IllegalArgumentException`.

Correction conseillee :

- Utiliser `Logger`.
- Separer :
  - `RestExceptionHandler` pour API JSON ;
  - gestion MVC avec pages d'erreur ou flash messages.
- Creer des exceptions metier precises.

## Priorite P2 - Frontend Thymeleaf

### 21. Unifier les layouts

Probleme :

- Plusieurs layouts existent : `layout/admin.html`, `layout/header.html`, `layout/sidebar.html`, etc.
- Des blocs HTML sont dupliques dans les dashboards.

Correction conseillee :

- Creer un layout Thymeleaf commun :

```text
templates/layout/base.html
templates/fragments/sidebar.html
templates/fragments/header.html
templates/fragments/footer.html
```

- Chaque page injecte seulement son contenu.

### 22. Reduire le CSS monolithique

Fichier concerne : `static/css/style.css`

Probleme :

- Le fichier CSS est tres grand et contient plusieurs modules.

Correction conseillee :

- Decouper progressivement :
  - `base.css`
  - `layout.css`
  - `forms.css`
  - `tables.css`
  - `dashboard.css`
  - `modules/clients.css`
  - `modules/ventes.css`

### 23. Retirer les donnees d'exemple dans les formulaires

Exemple :

- `ClientViewController.creerClientExemple()` pre-remplit des valeurs comme `Rakoto`, `0341111111`.

Correction conseillee :

- En production : formulaires vides.
- En demo : donnees d'exemple uniquement avec profil `dev`.

## Priorite P2 - Qualite Java

### 24. Uniformiser Lombok ou getters/setters manuels

Probleme :

- Certaines classes utilisent Lombok, d'autres non.
- Le style est mixte.

Correction conseillee :

- Choisir une convention.
- Si Lombok est garde : utiliser `@Getter`, `@Setter`, `@NoArgsConstructor`, `@Builder` de facon coherente.
- Sinon supprimer Lombok.

### 25. Ajouter Bean Validation dans tous les DTO d'entree

Exemples de validations utiles :

- `@NotNull` sur les IDs requis ;
- `@NotBlank` sur les noms/libelles ;
- `@Positive` sur quantites/prix ;
- `@DecimalMin("0.01")` sur montants ;
- validation telephone client.

Objectif :

- Refuser les mauvaises donnees avant la logique metier.

### 26. Standardiser les noms

Problemes observes :

- melange francais/anglais : `isActive`, `actif`, `estSupprimer`, `createdAt`, `dateCreation`;
- accents parfois presents dans les libelles metier ;
- routes parfois `/api/...` pour HTML.

Correction conseillee :

- Garder le francais pour le domaine metier si l'equipe travaille en francais.
- Garder l'anglais technique pour les conventions Java si souhaite, mais le faire partout.
- Exemple :
  - Java : `actif`, `supprime`, `creeLe`, `modifieLe`;
  - SQL : `actif`, `supprime`, `created_at`, `updated_at` selon convention choisie.

## Priorite P2 - Tests

### 27. Ajouter un profil de test isole

Probleme :

- Le test `@SpringBootTest` utilise PostgreSQL local.
- Cela rend les tests dependants de la machine.

Correction conseillee :

- Creer `src/test/resources/application-test.properties`.
- Option A : Testcontainers PostgreSQL.
- Option B : H2 seulement si le SQL reste compatible.

### 28. Ajouter des tests metier prioritaires

Tests a ajouter :

- creation vente avec panier vide ;
- vente avec stock insuffisant ;
- vente avec plusieurs lots FIFO ;
- production qui consomme plusieurs lots MP ;
- production avec MP insuffisante ;
- soft delete client ;
- login avec mauvais mot de passe ;
- acces admin sans session et avec session.

### 29. Ajouter des tests de routes

But :

- Eviter que les menus pointent vers des routes inexistantes.

Tests proposes :

- GET `/admin` sans session -> `302`;
- login admin -> redirection `/admin`;
- GET `/admin` avec session -> `200`;
- GET `/css/style.css`, `/static/css/style.css`, `/VINA_AKOHO_LOGO.png` -> `200`;
- routes principales des dashboards -> `200`.

## Priorite P3 - Outils et hygiene Git

### 30. Nettoyer les fichiers a ne pas versionner

Fichiers suspects :

- `cookies.txt`
- `log.txt`
- fichiers SQL temporaires a la racine
- fichiers locaux comme `Sprint2.1-local.md`

Correction conseillee :

- Ajouter ou verifier `.gitignore`.
- Deplacer les documents de travail dans `docs/`.
- Ne pas commit les cookies et logs.

### 31. Ajouter formatage automatique

Outils possibles :

- Spotless ;
- Checkstyle ;
- EditorConfig.

Objectif :

- Eviter les differences de style entre modules.
- Garder les imports et l'indentation propres.

### 32. Ajouter CI minimale

GitHub Actions conseille :

```yaml
mvn test
```

Puis plus tard :

- build jar ;
- tests integration ;
- analyse statique.

## Ordre conseille pour realiser la refactorisation

1. Reparer le Maven Wrapper.
2. Ajouter profils `dev`, `test`, `prod`.
3. Migrer `data.sql` et les scripts SQL vers Flyway/Liquibase.
4. Remplacer `ddl-auto=update` par `validate`.
5. Uniformiser les chemins statiques.
6. Remplacer tous les `href="#"`.
7. Nettoyer la session et les roles.
8. Ajouter Spring Security.
9. Separer controllers HTML et API REST.
10. Ajouter les tests prioritaires ventes/stock/clients/login.
11. Ajouter les verrous FIFO.
12. Decouper les layouts Thymeleaf.
13. Decouper le CSS.
14. Nettoyer fichiers temporaires et documentation.
15. Mettre en place CI.

## A ne pas faire tout de suite

- Ne pas tout renommer en une seule fois.
- Ne pas migrer tous les IDs `Integer` vers `Long` en une seule grosse modification.
- Ne pas remplacer tout Thymeleaf par une SPA sans besoin clair.
- Ne pas supprimer les scripts SQL avant d'avoir une migration validee.
- Ne pas changer le schema de stock sans tests FIFO.

## Definition d'un projet "propre" pour VINA AKOHO

Le projet sera considere bien stabilise quand :

- `mvn test` passe sur une machine propre ;
- `./mvnw test` fonctionne ;
- un nouveau developpeur peut lancer le projet avec `README.md`;
- la base est creee par migrations versionnees ;
- les secrets ne sont plus en dur ;
- chaque bouton visible mene a une page ou action existante ;
- les routes sont protegees par role ;
- les ventes et productions ne peuvent pas creer de stock negatif ;
- les tests couvrent les modules stock, ventes, clients, login ;
- les templates partagent un layout commun ;
- les fichiers temporaires ne sont plus a la racine.

