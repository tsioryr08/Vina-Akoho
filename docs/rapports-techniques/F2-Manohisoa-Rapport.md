# Rapport technique - F2 Registre des depenses

Module : Gestion des depenses pour le comptable
Auteur : Manohisoa
Derniere mise a jour : 2026-07-03

---

## 1. Objectif du module

Le module F2 ajoute un registre de depenses accessible depuis l'espace Comptable. Il permet :

- d'afficher toutes les sorties d'argent de l'entreprise ;
- de creer une nouvelle depense depuis un formulaire Thymeleaf ;
- de rattacher chaque depense a une categorie, une phase de production et un statut de paiement ;
- d'afficher des indicateurs simples : montant total, nombre d'operations et total par categorie.

Le point d'entree utilisateur se trouve dans la sidebar Comptable : `Registre des Depenses`.

---

## 2. Structure des fichiers du module

```text
src/main/java/mg/vinaAkoho/vina_akoho/
|
├── controller/depense/
│   └── DepenseController.java
|
├── dto/depense/
│   ├── CategorieDepenseDTO.java
│   ├── DepenseDTO.java
│   ├── DepenseRequestDTO.java
│   ├── PhaseDTO.java
│   └── StatutDepenseDTO.java
|
├── entity/depense/
│   ├── CategorieDepense.java
│   ├── Depense.java
│   ├── Phase.java
│   └── StatutDepense.java
|
├── exception/depense/
│   ├── CategorieDepenseNotFoundException.java
│   ├── DepenseDtoNonSupporteException.java
│   ├── DepenseNotFoundException.java
│   ├── PhaseNotFoundException.java
│   └── StatutDepenseNotFoundException.java
|
├── repository/depense/
│   ├── CategorieDepenseRepository.java
│   ├── DepenseRepository.java
│   ├── PhaseRepository.java
│   └── StatutDepenseRepository.java
|
└── service/depense/
    ├── CategorieDepenseService.java
    ├── DepenseService.java
    ├── PhaseService.java
    └── StatutDepenseService.java

src/main/resources/
|
├── templates/depense/
│   ├── comptable-depenses.html
│   └── comptable-depenses-nouveau.html
|
├── templates/layout/
│   ├── comptable.html
│   └── header-comptable.html
|
└── static/css/
    └── style.css

docs/
├── cahiers-de-test/F2-Manohisoa-Test.md
└── rapports-techniques/F2-Manohisoa-Rapport.md
```

---

## 3. Routes et vues Thymeleaf

| Methode | URL                                        | Template / action                         | Role                                                           |
| ------- | ------------------------------------------ | ----------------------------------------- | -------------------------------------------------------------- |
| GET     | `/api/depenses/comptable-depenses`         | `depense/comptable-depenses.html`         | Affiche le registre, les statistiques et la liste des depenses |
| GET     | `/api/depenses/comptable-depenses-nouveau` | `depense/comptable-depenses-nouveau.html` | Affiche le formulaire de creation                              |
| POST    | `/api/depenses`                            | Redirection vers le registre              | Valide et enregistre une depense                               |

Remarque : le prefixe `/api/depenses` est utilise par le controleur, mais les routes retournent des pages Thymeleaf et non une reponse JSON.

---

## 4. Modele de donnees

### Table `depense`

| Champ                  | Type Java                   | Role                          |
| ---------------------- | --------------------------- | ----------------------------- |
| `id`                   | `Integer`                   | Identifiant auto-genere       |
| `date`                 | `LocalDate`                 | Date de la depense            |
| `designation`          | `String`                    | Libelle de la sortie d'argent |
| `id_categorie_depense` | relation `CategorieDepense` | Categorie comptable           |
| `id_phase`             | relation `Phase`            | Phase de production concernee |
| `montant`              | `BigDecimal`                | Montant en Ariary             |
| `id_statut_depense`    | relation `StatutDepense`    | Statut du paiement            |
| `created_at`           | `LocalDateTime`             | Date de creation              |
| `updated_at`           | `LocalDateTime`             | Date de mise a jour           |

L'entite `Depense` utilise `@PrePersist` pour renseigner automatiquement `createdAt`, `updatedAt` et la date du jour si aucune date n'est fournie.

### Tables de reference

- `categorie_depense` : contient les categories comme Charges fixes, Matieres premieres et Livraison.
- `phase` : contient les phases de production ou l'option hors production.
- `statut_depense` : contient les statuts Regle et En attente.

---

## 5. DTO et validation

### `DepenseRequestDTO`

Ce DTO recoit les donnees du formulaire de creation.

| Champ                | Validation                               |
| -------------------- | ---------------------------------------- |
| `date`               | `@NotNull`                               |
| `designation`        | `@NotBlank`, `@Size(max = 255)`          |
| `idCategorieDepense` | `@NotNull`                               |
| `idPhase`            | `@NotNull`                               |
| `montant`            | `@NotNull`, `@DecimalMin(value = "0.0")` |
| `idStatutDepense`    | `@NotNull`                               |

Les messages de validation sont en francais et servent a empecher l'enregistrement d'une depense incomplete ou invalide.

### `DepenseDTO`

Ce DTO est utilise pour afficher le registre. Il contient les identifiants techniques et les libelles lisibles :

- date ;
- designation ;
- categorie et libelle de categorie ;
- phase et libelle de phase ;
- montant ;
- statut et libelle de statut ;
- dates de creation et de mise a jour.

---

## 6. Services

### `DepenseService`

Responsabilites principales :

```java
List<DepenseDTO> listerToutes()
DepenseDTO trouverParId(Integer id)
BigDecimal calculerMontantTotal()
BigDecimal calculerMontantTotalParCategorie(Integer idCategorieDepense)
long compterToutes()
DepenseDTO creer(DepenseRequestDTO requete)
DepenseDTO modifier(Integer id, DepenseRequestDTO requete)
void supprimer(Integer id)
```

Le service convertit les entites en DTO pour l'affichage et transforme les DTO de reference en entites avant la sauvegarde. Les erreurs de conversion sont encadrees par `DepenseDtoNonSupporteException`.

### Services de referentiel

- `CategorieDepenseService` : liste les categories et recherche une categorie par identifiant.
- `PhaseService` : liste les phases et recherche une phase par identifiant.
- `StatutDepenseService` : liste les statuts et recherche un statut par identifiant.

Chaque service leve une exception personnalisee si l'identifiant demande n'existe pas.

---

## 7. Repositories

Tous les repositories heritent de `JpaRepository`.

| Repository                   | Entite             | Role                                                             |
| ---------------------------- | ------------------ | ---------------------------------------------------------------- |
| `DepenseRepository`          | `Depense`          | CRUD des depenses et somme des depenses reglees entre deux dates |
| `CategorieDepenseRepository` | `CategorieDepense` | Acces aux categories                                             |
| `PhaseRepository`            | `Phase`            | Acces aux phases                                                 |
| `StatutDepenseRepository`    | `StatutDepense`    | Acces aux statuts                                                |

`DepenseRepository.sumDepensesEntreDeuxDatesEtCategorie` calcule les depenses reglees (`statutDepense.id = 1`) sur une periode, avec une categorie optionnelle.

---

## 8. Fonctionnement des pages

### `comptable-depenses.html`

Cette page affiche :

- le titre "Registre des Depenses" ;
- un bouton "+ Nouvelle depense" ;
- les statistiques du jour ;
- un tableau des operations avec reference `DEP-{id}` ;
- la date, la designation, la categorie, le montant et le statut de chaque depense.

Les filtres visibles dans la page sont actuellement des elements d'interface. Le filtrage dynamique n'est pas encore connecte au backend dans ce commit.

### `comptable-depenses-nouveau.html`

Cette page affiche le formulaire de saisie. Les listes deroulantes sont remplies par :

- `categoriesDepense` ;
- `phases` ;
- `statutsDepense`.

Lors de la soumission, le formulaire envoie un POST vers `/api/depenses`. Si les validations echouent, la meme page est rechargee avec les referentiels. Si les validations passent, la depense est sauvegardee et l'utilisateur revient au registre.

---

## 9. Donnees necessaires pour tester

Les donnees minimales a inserer avant le test sont les categories, phases et statuts suivants :

- categories : Charges fixes, Matieres premieres, Livraison ;
- phases : Aucune / Hors production, Phase 1 - Preparation, Phase 2 - Broyage/Melange, Phase 3 - Conditionnement ;
- statuts : Regle, En attente.

Sans ces donnees de reference, le formulaire de creation ne peut pas proposer les choix obligatoires.

---

## 10. Exceptions personnalisees

Le module respecte la convention backend qui interdit les `RuntimeException` brutes. Les exceptions ajoutees sont :

- `DepenseNotFoundException` ;
- `CategorieDepenseNotFoundException` ;
- `PhaseNotFoundException` ;
- `StatutDepenseNotFoundException` ;
- `DepenseDtoNonSupporteException`.

Elles rendent les erreurs metier plus explicites, par exemple lorsqu'une categorie, une phase, un statut ou une depense n'existe pas.

---

## 11. Integration au layout comptable

Le lien du menu est ajoute dans `templates/layout/comptable.html` :

```html
<a class="sidebar-link" th:href="@{/api/depenses/comptable-depenses}">
  Registre des Depenses <span class="sidebar-code">F2</span>
</a>
```

Le header comptable a aussi ete adapte avec `templates/layout/header-comptable.html` pour afficher le contexte de la page.
