# Incohérences du projet VINA-AKOHO

## 1. Schéma SQL manquant vs Entités JPA — colonne `actif`
- **Fichiers** : `schema (1).sql` (tables `produit`, `categorie`), `Produit.java`, `Categorie.java`, `ProduitRepository.java`, `CategorieRepository.java`, `ProduitService.java`, `CategorieService.java`
- **Problème** : Les entités `Produit` et `Categorie` utilisent le champ `actif` (soft delete), mais les tables SQL n'ont pas cette colonne. Les requêtes JPQL comme `p.actif = true` échoueront au runtime.
- **Solution** : Ajouter `actif BOOLEAN NOT NULL DEFAULT TRUE` dans les tables `produit` et `categorie`, ou supprimer le concept d'`actif` dans les entités/services.
- **Pourquoi** : Spring Data JPA va générer les colonnes si `ddl-auto=update`, mais cette incohérence documentée casse la cohérence schéma/code et empêche l'utilisation de `validate`.

## 2. Types d'identifiants hétérogènes — `Integer` vs `Long`
- **Fichiers** : Toutes les entités (`Employe`, `Role`, `MatierePremiere`, `LotMp`, `MouvementStockMp`, `MouvementStockProduit`, `Fabrication`, `FabricationMp`, `TypeMouvement`, `Unite`, `Fournisseur`, `RecetteProduit`, `Client`, `StatutCommande`, `StatutVente`, `ModePaiement` utilisent `Integer` ; `Produit`, `Categorie`, `Commande`, `Vente`, `LigneVente`, `LigneCommande`, `Facture`, `LigneVenteLot` utilisent `Long`).
- **Problème** : Les casts sont mal gérés. Exemple : `EntreeProduitService.java:91` fait `produit.getCategorie().getId().intValue()` sur un ID `Long`, risquant overflow/NPE. Les repositories sont aussi incohérents (`JpaRepository<Client, Integer>` vs `JpaRepository<Produit, Long>`).
- **Solution** : Uniformiser tous les IDs sur `Long`.
- **Pourquoi** : `Long` est le standard JPA/Spring Data. Mélanger lestypes oblige à des casts explicites et augmente les risques de bugs.

## 3. Incohérence Lombok — entités et DTOs
- **Fichiers** : Entités avec Lombok (`Produit`, `Categorie`, `Vente`, `Commande`, `Facture`, `LigneVente`, `LigneVenteLot`, `ModePaiement`, `StatutCommande`, `StatutVente`, `LigneCommande`) ; entités sans Lombok (`Employe`, `Role`, `ServiceClient`, `TypeClient`, `MatierePremiere`, `LotMp`, `MouvementStockMp`, `MouvementStockProduit`, `Fabrication`, `FabricationMp`, `TypeMouvement`, `Unite`, `Fournisseur`, `RecetteProduit`, `Client`). DTOs avec Lombok : `ProduitRequestDTO`, `CategorieRequestDTO`, `LoginRequestDTO` ; sans Lombok : `ClientRequestDTO`, `ClientGestionDTO`, `ClientInscriptionDTO`, etc.
- **Problème** : Le style est fragmenté. Certains utilisent Lombok, d'autres écrivent manuellement tous les getters/setters, rendant le code difficile à maintenir et augmentant le risque d'oublis.
- **Solution** : Décider d'une convention (Lombok partout ou manuel partout) et l'appliquer via un guide de style.
- **Pourquoi** : Les conventions du projet ne mentionnent pas Lombok. Le manque de standardisation crée une dette technique.

## 4. Identifiants en dur (magic numbers)
- **Fichiers** : `VenteController.java:185` (`idEmploye = 1`), `MatierePremiereViewController.java:141` (`idEmploye = 1` dans `EntreeStockDTO`), `SortieProduitService.java:68` (`uniteRepository.findById(1)`), `SortieProduitService.java:119` (encore `findById(1)`).
- **Problème** : Des codes magiques en dur sont utilisés pour l'employé et l'unité par défaut. Si le seed data change, le comportement casse silencieusement.
- **Solution** : Récupérer l'employé depuis la session (`SessionFilter.ATTRIBUT_ID_EMPLOYE`) et utiliser l'unité de la matière première/produit au lieu de l'ID 1 en dur.
- **Pourquoi** : Le code n'est pas portable.

## 5. Chemins de controllers sans slash initial
- **Fichiers** : `ProduitController.java:25` (`@RequestMapping("api/produits")`), `CategorieController.java:21` (`@RequestMapping("api/categories")`).
- **Problème** : Ces chemins sont relatifs. Tous les autres controllers utilisent un slash initial (`/api/...`).
- **Solution** : Ajouter un slash initial : `@RequestMapping("/api/produits")` et `@RequestMapping("/api/categories")`.
- **Pourquoi** : Les conventions Spring recommandent les chemins absolus.

## 6. Duplication des chemins dans `ClientController`
- **Fichier** : `ClientController.java:21-24`
- **Problème** : `@RequestMapping({"/clients", "/api/clients"})` expose deux URLs pour la même ressource. Le reste du projet utilise un pattern unique.
- **Solution** : Choisir un préfixe unique : soit `/api/clients` (REST), soit `/clients` (views).
- **Pourquoi** : Cela crée de la confusion et risque des collisions de routage avec `ClientViewController` qui utilise déjà `/clients`.

## 7. Incohérence d'injection de dépendances
- **Fichiers** : `ClientService.java` (constructeur classique sans annotation), `LoginService.java` (`@Autowired` constructeur), `RecetteProduitService.java` (`@Autowired` constructeur), `SortieMpService.java` (`@Autowired` constructeur), `SortieProduitService.java` (`@Autowired` constructeur), `ProduitService.java` (`@RequiredArgsConstructor`), `CategorieService.java` (`@RequiredArgsConstructor`), `VenteService.java` (`@RequiredArgsConstructor`).
- **Problème** : Certains services utilisent `@RequiredArgsConstructor` (Lombok), d'autres utilisent `@Autowired` explicite, et d'autres n'ont pas d'annotation.
- **Solution** : Uniformiser avec `@RequiredArgsConstructor` partout.
- **Pourquoi** : L'incohérence rend le code moins prévisible.

## 8. `cookies.txt` à la racine du projet
- **Fichier** : `cookies.txt`
- **Problème** : Un fichier `cookies.txt` est présent à la racine. S'il contient des cookies de session, c'est une faille de sécurité.
- **Solution** : Ajouter `cookies.txt` dans `.gitignore` et vérifier qu'il n'est pas dans l'historique Git.
- **Pourquoi** : Exposer des cookies permet le vol de session.

## 9. Entités avec colonnes SQL non mappées
- **Fichiers** :
  - `Employe.java` : SQL a `contact`, `created_at`, `updated_at` — entité n'a rien
  - `Role.java` : SQL a `created_at` — entité n'a rien
  - `ServiceClient.java` : SQL a `description`, `updated_at` — entité a `description` mais pas `updated_at`
  - `TypeMouvement.java` : SQL a `created_at` — entité n'a rien
  - `Unite.java` : SQL a `created_at`, `updated_at` — entité n'a rien
  - `Fournisseur.java` : SQL a `email`, `telephone`, `created_at`, `updated_at` — entité n'a que `id` et `nom`
  - `MouvementStockMp.java` / `MouvementStockProduit.java` : SQL a `date_mouvement TIMESTAMP` — entité mappe en `LocalDate` (perte de l'heure)
- **Problème** : Les entités ne mappent pas toutes les colonnes SQL, causant une perte d'information et une traçabilité incomplète.
- **Solution** : Ajouter les champs manquants dans les entités avec `@Column`, et changer `dateMouvement` en `LocalDateTime`.
- **Pourquoi** : La perte d'information casse la traçabilité des mouvements de stock.

## 10. Incohérence `@PrePersist` / `@PreUpdate`
- **Fichiers** :
  - Sans `@PrePersist`/`@PreUpdate` : `Employe`, `Role`, `ServiceClient`, `TypeClient`, `MatierePremiere`, `LotMp`, `Fournisseur`, `FabricationMp`, `LigneVente`, `LigneCommande`, `LigneVenteLot`
  - Avec `@PrePersist` uniquement : `RecetteProduit`, `StatutCommande`, `StatutVente`, `Commande`, `Facture`, `Fabrication`, `LotProduit`, `MouvementStockProduit`
  - Avec les deux : `Client`, `Produit`, `Categorie`, `Vente`, `ModePaiement`
- **Problème** : Certaines entités initialisent `createdAt`/`updatedAt`, d'autres non. La coexistence des stratégies DB (`DEFAULT CURRENT_TIMESTAMP`) et JPA (`@PrePersist`) crée des comportements différents.
- **Solution** : Choisir une stratégie unique (DB-first ou JPA-first) et l'appliquer partout.
- **Pourquoi** : La coexistence crée des comportements incohérents selon que l'entité est sauvegardée par JPA ou par SQL direct.

## 11. Relations manquantes dans les entités
- **Fichiers** :
  - `Client` : pas de `@OneToMany` vers `Commande`
  - `Categorie` : pas de `@OneToMany` vers `Produit`
  - `Produit` : pas de `@OneToMany` vers `LotProduit`
  - `MatierePremiere` : pas de `@OneToMany` vers `LotMp`
  - `Fabrication` : pas de `@OneToMany` vers `FabricationMp`
  - `RecetteProduit` : pas de `@ManyToOne` vers `Categorie` (stocke `idCategorie` comme `Integer` brut)
  - `Commande` : pas de `@OneToMany` vers `LigneCommande`
  - `Vente` : pas de `@OneToMany` vers `LigneVente`
- **Problème** : Certaines relations sont seulement unidirectionnelles, obligeant à écrire des requêtes customisées pour naviguer dans le graphe métier.
- **Solution** : Ajouter les relations inverses (`mappedBy`) là où c'est pertinent.
- **Pourquoi** : Naviguer dans le graphe métier est plus intuitif avec des relations bidirectionnelles.

## 12. Tables SQL sans entités JPA correspondantes
- **Fichier** : `schema (1).sql`
- **Tables sans entité** : `categorie_depense`, `phase`, `statut_livraison`, `statut_depense`, `type_prix`, `livreur`, `livraison`, `historique_statut_livraison`, `depense`, `depense_lot`, `historique_prix`
- **Problème** : Beaucoup de tables sont définies dans le schéma mais n'ont aucune entité, repository, service ou controller Java associé. Le projet diverge du schéma cible.
- **Solution** : Soit créer les entités manquantes, soit supprimer les tables du schéma si hors Sprint.
- **Pourquoi** : La dette technique s'accumule et le schéma diverge de l'application.

## 13. DTOs avec champs dates en `String` au lieu de types fort
- **Fichiers** : `ClientGestionDTO.java` (`dateInscription` en `String`, `createdAt` en `String`, `updatedAt` en `String`), `ClientResumeDTO.java` (à vérifier).
- **Problème** : Les dates sont converties en `String` via `.toString()`, exposant le format interne et empêchant toute validation ou calcul côté frontend.
- **Solution** : Utiliser `LocalDate`/`LocalDateTime` dans les DTOs et laisser le frontend formater, ou utiliser `@JsonFormat`.
- **Pourquoi** : Les DTOs doivent transporter des types fort, pas des représentations texte.

## 14. `GlobalExceptionHandler` ne gère pas toutes les exceptions personnalisées
- **Fichier** : `GlobalExceptionHandler.java`
- **Problème** : Seules 7 exceptions sont gérées explicitement. Des exceptions métier comme `ClientNotFoundException` ou `IdentifiantsInvalidesException` tombent dans le `catch Exception` générique qui retourne 500 au lieu de 404/401.
- **Solution** : Ajouter un `@ExceptionHandler` pour chaque exception personnalisée (`ClientNotFoundException` -> 404, `IdentifiantsInvalidesException` -> 401, etc.).
- **Pourquoi** : Le frontend ne peut pas différencier une erreur serveur d'une ressource introuvable.

## 15. `SessionFilter` — listes de routes publiques en dur et pattern faible
- **Fichier** : `SessionFilter.java:17-27`
- **Problème** : La liste contient des chemins en dur dont `/index.php` (reliquat PHP), et `estRoutePublique` fait un `startsWith` qui peut matcher des chemins non prévus (ex: `/css` matche `/css-archive`).
- **Solution** : Utiliser des patterns explicites (`/css/**`, `/assets/**`) et externaliser les routes publiques dans la configuration.
- **Pourquoi** : Un chemin mal configuré peut laisser une page sensible accessible sans authentification.

## 16. `VenteService` a une transaction de classe entière pour de la lecture
- **Fichier** : `VenteService.java:45` (`@Transactional` au niveau classe)
- **Problème** : `@Transactional` est appliqué à toutes les méthodes, y compris `listerToutes()`, `trouverParId()` et `versDTO()` qui font seulement de la lecture.
- **Solution** : Déplacer `@Transactional` sur les seules méthodes de modification (`creer`).
- **Pourquoi** : Les transactions longues en lecture bloquent les connexions du pool et dégradent les performances.

## 17. `VenteService.creer` — validations après sauvegardes partielles
- **Fichier** : `VenteService.java:74-152`
- **Problème** : La validation du panier (`panier == null || panier.isEmpty()`) et du client/mode de paiement se fait après la création de `Commande` et `Vente`. Si une validation échoue après, la transaction rollback, mais le message d'erreur n'est pas explicite.
- **Solution** : Déplacer toutes les validations au tout début de `creer()`, avant toute sauvegarde.
- **Pourquoi** : Éviter des effets de bord et fournir des erreurs précoces.

## 18. `VenteService.versDTO` risque `LazyInitializationException`
- **Fichier** : `VenteService.java:180` : `vente.getCommande().getClient()`
- **Problème** : `commande` et `client` sont en `FetchType.LAZY`. Si le repository ne fait pas de `JOIN FETCH`, l'appel à `versDTO` hors de la transaction ou après fermeture lève `LazyInitializationException`.
- **Solution** : Utiliser `JOIN FETCH` dans `VenteRepository.findById` ou charger explicitement les relations avant le mapping.
- **Pourquoi** : Un crash 500 en production sur la consultation d'une vente.

## 19. `VenteController` — valeur placeholder en production
- **Fichier** : `VenteController.java:101` : `model.addAttribute("totalVentesKg", ventes.size() * 1); // placeholder`
- **Problème** : La valeur est un placeholder sans signification métier.
- **Solution** : Calculer la somme réelle des quantités vendues ou supprimer l'attribut.
- **Pourquoi** : Un placeholder en production trompe l'utilisateur.

## 20. `pom.xml` — dépendances de test dupliquées
- **Fichier** : `pom.xml:67-81`
- **Problème** : Les dépendances `spring-boot-starter-data-jpa-test`, `spring-boot-starter-thymeleaf-test`, `spring-boot-starter-validation-test`, `spring-boot-starter-webmvc-test` sont spécifiées en dur alors que `spring-boot-starter-test` les inclut déjà.
- **Solution** : Supprimer ces doublons et ne garder que `spring-boot-starter-test`.
- **Pourquoi** : Les doublons gonflent le classpath et créent des conflits de versions possibles.

## 21. `pom.xml` — champs vides et métadonnées manquantes
- **Fichier** : `pom.xml:14-28`
- **Problème** : `<name/>`, `<description/>`, `<url/>`, `<licenses>`, `<developers>`, `<scm>` sont vides.
- **Solution** : Remplir ces champs ou supprimer les blocs inutiles.
- **Pourquoi** : Un POM avec des champs vides déprécie le projet et empêche une génération correcte du site Maven.

## 22. `data.sql` — mots de passe hashés en dur
- **Fichier** : `data.sql:9-19`
- **Problème** : Les mots de passe hashés sont versionnés dans le dépôt. Si le hash fuit, les comptes connus (`admin@vinaakoho.mg`, `achat@vinaakoho.mg`, etc.) sont compromis.
- **Solution** : Externaliser les mots de passe ou générer le premier admin via un endpoint dédié.
- **Pourquoi** : Bonne pratique de sécurité.

## 23. `data.sql` insère des rôles avec `ON CONFLICT`, mais `employe` référence des IDs fixes
- **Fichiers** : `data.sql`, `schema (1).sql`
- **Problème** : Les `INSERT INTO employe` utilisent `id_role` en dur (1, 2, 3, 4, 5, 6). Les `ON CONFLICT (poste) DO NOTHING` ne garantissent pas la stabilité des IDs si l'ordre d'insertion change ou si des rôles sont supprimés/recréés.
- **Solution** : Utiliser des IDs explicites (`INSERT INTO role (id, poste) VALUES (1, 'Administrateur')`) ou récupérer les IDs à la volée avec des sous-requêtes.
- **Pourquoi** : La stabilité des références entre tables est critique.

## 24. `schema (1).sql` et `data.sql` incompatible avec approche hybride
- **Fichier** : `Regles_Backend_VINA-AKOHO2(1).md:219` (`spring.jpa.hibernate.ddl-auto=update`) vs `schema (1).sql` (SQL natif avec `SERIAL` et contraintes).
- **Problème** : Si Hibernate crée les tables automatiquement et que `schema.sql` est aussi exécuté, les doubles créations causent des erreurs.
- **Solution** : Soit utiliser seulement `ddl-auto=validate` avec `schema.sql`, soit migrer vers Flyway/Liquibase.
- **Pourquoi** : Mélanger auto-DDL et scripts SQL manuels est source d'erreurs.

## 25. Scripts correctifs non versionnés (`correctif-*.sql`)
- **Fichiers** : `correctif-20260624-01.sql`, `correctif-20260626-clients-soft-delete.sql`, `20260630-insert-test.sql`, `20260630-modifBase.sql`, `20260701-insert-test-entree-produit.sql`
- **Problème** : Les migrations sont faites à la main sans traçabilité. Si une étape est oubliée, la base est dans un état incohérent.
- **Solution** : Migrer vers Flyway ou Liquibase et convertir les scripts en migrations numérotées.
- **Pourquoi** : Savoir quel état de DB correspond à quel commit est essentiel.

## 26. `Regles_Backend_VINA-AKOHO2(1).md` — exemples contradictoires
- **Fichier** : `Regles_Backend_VINA-AKOHO2(1).md`
- **Problème** : La doc montre `controller/matieres-premieres/MatierePremiereController.java` en kebab-case pour les dossiers, mais le code Java utilise `matierespremieres` (un seul mot). Elle cite aussi la table `categorie_produit` mais le SQL a `categorie`.
- **Solution** : Mettre à jour la documentation pour refléter la structure réelle.
- **Pourquoi** : Les nouveaux développeurs suivent la doc et codent dans des dossiers inexistants.

## 27. `Client` — deux flags de suppression (`actif` et `estSupprimer`)
- **Fichiers** : `Client.java`, `ClientRepository.java`, `ClientService.java`
- **Problème** : `actif` est à `true` par défaut dans `@PrePersist`, et `estSupprimer` est à `false`. Les deux coexistent sans logique métier claire. `estSupprimer` est utilisé pour le soft delete, mais `actif` est aussi présent.
- **Solution** : Choisir un seul flag (soit `actif` soit `estSupprimer`) pour le soft delete.
- **Pourquoi** : Deux flags pour la même sémantique créent de la confusion.

## 28. `RecetteProduit` — `idCategorie` stocké en `Integer` brut sans relation JPA
- **Fichier** : `RecetteProduit.java:17`
- **Problème** : `idCategorie` est stocké comme `Integer`, pas en `@ManyToOne` vers `Categorie`. Cela empêche la navigation et oblige à charger `Categorie` manuellement.
- **Solution** : Ajouter `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_categorie") private Categorie categorie;`
- **Pourquoi** : Permet d'accéder aux infos de la catégorie directement depuis la recette.

## 29. `FabricationMp` — `idUnite` stocké en `Integer` brut sans relation JPA
- **Fichier** : `FabricationMp.java:34`
- **Problème** : `idUnite` est stocké comme entier, pas en `@ManyToOne` vers `Unite`.
- **Solution** : Ajouter `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_unite") private Unite unite;`
- **Pourquoi** : Cohérence avec `MouvementStockMp` et `MouvementStockProduit` qui ont la relation vers `Unite`.

## 30. `Fournisseur` seulement 2 champs mais SQL en a 4
- **Fichiers** : `Fournisseur.java` vs `schema (1).sql`
- **Problème** : L'entité n'a que `id` et `nom`. Le SQL a aussi `email`, `telephone`, `created_at`, `updated_at`.
- **Solution** : Ajouter les champs manquants dans l'entité ou supprimer les colonnes SQL inutilisées.
- **Pourquoi** : Perte d'information fonctionnelle.

## 31. `Employe` — champ `contact` manquant
- **Fichiers** : `Employe.java` vs `schema (1).sql`
- **Problème** : La table SQL `employe` a `contact VARCHAR(20)`, mais l'entité Java ne l'a pas.
- **Solution** : Ajouter `@Column(name = "contact", length = 20) private String contact;`
- **Pourquoi** : Perte d'information fonctionnelle.

## 32. `ProduitController` — pagination manuelle basée sur `List`
- **Fichier** : `ProduitController.java:42-63`
- **Problème** : `produitService.listerTous()` retourne une `List`, puis le controller fait une pagination manuelle avec `PageImpl`. Cela charge tous les produits en mémoire.
- **Solution** : Faire paginer par le repository (`produitRepository.findAllActifs(pageable)`) et retourner un `Page<ProduitDTO>`.
- **Pourquoi** : Charger tous les produits pour en paginer quelques-uns est inefficace.

## 33. `CategorieController` — redirections potentiellement incorrectes
- **Fichier** : `CategorieController.java:77` (`redirect:/categories`), `CategorieController.java:123` (`redirect:/categories`).
- **Problème** : Le controller est mappé sur `api/categories` (sans slash initial), mais les redirections utilisent `/categories`.
- **Solution** : Uniformiser vers le chemin réel des vues.
- **Pourquoi** : Si le mapping n'existe pas, l'utilisateur obtient une 404.

## 34. `ClientGestionDTO` expose `estSupprimer` au frontend
- **Fichier** : `ClientGestionDTO.java`
- **Problème** : Le flag de suppression logique est exposé dans les DTOs publics, permettant de réactiver un client supprimé sans vérification métier.
- **Solution** : Ne pas exposer `estSupprimer` dans les DTOs publics.
- **Pourquoi** : Séparation des couches de persistance et présentation.

## 35. `LoginController` — `@RequestParam` au lieu de DTO
- **Fichier** : `LoginController.java:27-29`
- **Problème** : Le controller utilise `@RequestParam String email, @RequestParam String mdp` au lieu d'un `@RequestBody LoginRequestDTO`.
- **Solution** : Accepter un `@RequestBody LoginRequestDTO` comme les autres APIs.
- **Pourquoi** : Incohérence avec la règle "DTO obligatoires" du document de référence.

## 36. `EntreeProduitService` — cast `Long` vers `Integer` risqué
- **Fichier** : `EntreeProduitService.java:91` : `Integer idCategorie = produit.getCategorie().getId().intValue();`
- **Problème** : Si l'ID d'une catégorie dépasse `Integer.MAX_VALUE`, le cast lève une `ArithmeticException`.
- **Solution** : Utiliser `Long` dans `RecetteProduitRepository` et `EntreeProduitService`, ou cast sécurisé avec vérification.
- **Pourquoi** : Crash serveur sur une valeur légitime.

## 37. `Employe` — `FetchType.EAGER` sur `role` au lieu de `LAZY`
- **Fichier** : `Employe.java:33`
- **Problème** : `@ManyToOne(fetch = FetchType.EAGER)` pour le rôle, contrairement à la plupart des relations qui utilisent `LAZY`.
- **Solution** : Soit utiliser `LAZY` partout, soit justifier pourquoi le rôle employé doit être EAGER.
- **Pourquoi** : Le chargement eager cause des problèmes de performance (N+1) quand on liste des employés.

## 38. `StatutCommande`, `StatutVente`, `TypeMouvement` manquent `updated_at`
- **Fichiers** : Ces entités ont `created_at` mais pas `updated_at`, contrairement à la plupart des autres entités.
- **Problème** : Incohérence dans la gestion des timestamps.
- **Solution** : Ajouter `updated_at` avec `@PreUpdate` pour la cohérence.
- **Pourquoi** : Toutes les entités métier devraient tracer les dates de création et de modification.

## 39. `Client` — setters créent des entités fantômes
- **Fichier** : `Client.java:191-197` et `211-218`
- **Problème** : `setIdService(Integer id)` crée un nouveau `ServiceClient()` avec seulement l'ID. Si ce service n'existe pas en DB, la FK viole la contrainte.
- **Solution** : Utiliser `serviceClientRepository.findById(id)` avant d'assigner la relation, ou lever une exception explicite.
- **Pourquoi** : Casser la FK cause une `DataIntegrityViolationException` non explicite.

## 40. `VenteController.validerVente` — `idEmploye` par défaut à 1
- **Fichier** : `VenteController.java:183-186`
- **Problème** : Si la session n'a pas d'employé, `idEmploye` est forcé à `1` (admin par défaut).
- **Solution** : Rediriger vers la login si aucun employé n'est en session, ou lever une exception métier.
- **Pourquoi** : Traçabilité faussée : la vente est enregistrée au nom de l'admin sans que l'utilisateur réel ne soit identifié.
