# Rapport Technique — F2 Matières Premières

Module : Gestion des matières premières (T2.1 → T2.4) + Sprint 2 (Entree.MP)
Auteur : Rary
Dernière mise à jour : 2026-06-30

---

## 1. Structure des fichiers du module


src/main/java/mg/vinaAkoho/vina_akoho/
│
├── controller/matierespremieres/
│   ├── MatierePremiereController.java      — API REST JSON
│   └── MatierePremiereViewController.java  — Vues Thymeleaf HTML
│
├── service/matierespremieres/
│   └── MatierePremiereService.java
│
├── repository/matierespremieres/
│   ├── MatierePremiereRepository.java
│   ├── FournisseurRepository.java
│   ├── UniteRepository.java
│   ├── LotMpRepository.java
│   ├── TypeMouvementRepository.java
│   └── MouvementStockMpRepository.java
│
├── entity/matierespremieres/
│   ├── MatierePremiere.java
│   ├── Fournisseur.java
│   ├── Unite.java
│   ├── LotMp.java
│   ├── TypeMouvement.java
│   └── MouvementStockMp.java
│
├── dto/
│   ├── ApiResponse.java                    — partagé avec tous les modules ( efa navadika session de tsy ilaina tsony )
│   └── matierespremieres/
│       ├── MatierePremiereRequestDTO.java
│       ├── MatierePremiereListDTO.java
│       ├── FicheDetailDTO.java
│       ├── LotDTO.java
│       ├── EntreeStockDTO.java
│       ├── FournisseurDTO.java
│       └── UniteDTO.java
│
└── exception/
    ├── GlobalExceptionHandler.java          — partagé avec tous les modules
    └── matierespremieres/
        ├── MatierePremiereNotFoundException.java
        ├── FournisseurNotFoundException.java
        ├── UniteNotFoundException.java
        └── TypeMouvementNotFoundException.java

src/main/resources/
│
├── templates/
│   ├── layout/
│   │   ├── sidebar.html    — fragment Thymeleaf : barre latérale
│   │   └── header.html     — fragment Thymeleaf : barre supérieure
│   └── matieres-premieres/
│       ├── liste.html      — suivi stock + KPIs + couverture
│       ├── formulaire.html — création fiche matière première
│       ├── entree-stock.html — enregistrement d'un lot entrant
│       └── fiche.html      — détail MP + file FIFO des lots
│
└── static/assets/
    ├── css/style.css        — CSS du projet (3650 lignes, zéro Bootstrap)
    └── VINA_AKOHO_LOGO.png

docs/
├── cahiers-de-test/F2-Rary-Test.md
├── rapports-techniques/F2-Rary-Rapport.md  (ce fichier)
donnees-test.sql
init.md


> Note : le sous-dossier Java est nommé matierespremieres (sans tiret) car un tiret est interdit dans un nom de package Java. Les URLs HTTP utilisent la forme kebab-case /matieres-premieres, conformément aux conventions de la section 4 des règles.

---

## 2. Endpoints API REST (/api/matieres-premieres)

Toutes les réponses suivent le format ApiResponse { success, message, data }.

| Méthode | URL | Rôle | Tâche |
| --- | --- | --- | --- |
| GET | / | Liste toutes les MP avec stock, PAMP et statut | T2.2 / T2.3 |
| GET | /alertes | MP dont le stock est inférieur ou égal au seuil | T2.3 |
| GET | /{id} | Fiche détail + file FIFO des lots | T2.2 / T2.4 |
| POST | / | Créer une fiche matière première | T2.1 |
| PUT | /{id} | Modifier une fiche matière première | T2.1 |
| DELETE | /{id} | Supprimer une fiche matière première | T2.1 |
| POST | /entree-stock | Enregistrer l'arrivée d'un lot | T2.4 |
| GET | /fournisseurs | Liste des fournisseurs pour le formulaire | T2.1 |
| GET | /unites | Liste des unités pour le formulaire | T2.1 |

---

## 3. Endpoints vues Thymeleaf (/matieres-premieres)

| Méthode | URL | Template rendu | Description |
| --- | --- | --- | --- |
| GET | /matieres-premieres | liste.html | Tableau de suivi + KPIs + couverture |
| GET | /matieres-premieres/nouveau | formulaire.html | Formulaire de création |
| POST | /matieres-premieres/nouveau | Redirect → liste | Traitement du formulaire |
| GET | /matieres-premieres/{id} | fiche.html | Fiche détail + lots FIFO |
| GET | /matieres-premieres/entree-stock | entree-stock.html | Formulaire d'entrée en stock |
| POST | /matieres-premieres/entree-stock | Redirect → fiche | Traitement de l'entrée en stock |

---

## 4. Attributs de modèle Thymeleaf

| Template | Attribut | Type | Fournit |
| --- | --- | --- | --- |
| liste.html | mps | List<MatierePremiereListDTO> | Toutes les MP avec stock et statut |
| liste.html | totalStock | BigDecimal | Somme de toutes les quantiteStock |
| formulaire.html | fournisseurs | List<FournisseurDTO> | Options du menu déroulant |
| formulaire.html | unites | List<UniteDTO> | Options du menu déroulant |
| entree-stock.html | mps | List<MatierePremiereListDTO> | MP disponibles + PAMP affiché en readonly |
| fiche.html | fiche | FicheDetailDTO | Détail MP + liste des lots |

---

## 5. Fonctions principales du service

java
// Lecture
List<MatierePremiereListDTO> lister()
List<MatierePremiereListDTO> listerAlertes()
FicheDetailDTO detail(Integer id)

// Écriture
FicheDetailDTO creer(MatierePremiereRequestDTO dto)
FicheDetailDTO modifier(Integer id, MatierePremiereRequestDTO dto)
void supprimer(Integer id)
FicheDetailDTO entreeStock(EntreeStockDTO dto)

// Référentiels
List<FournisseurDTO> listerFournisseurs()
List<UniteDTO> listerUnites()

// Méthodes internes
private String genererCode(String nom)
private FicheDetailDTO versDetailDTO(MatierePremiere mp)
private String statut(BigDecimal stock, BigDecimal seuil)


---

## 6. Logique métier

### Code unique auto-généré
Format : MP-<PREMIER_MOT>-NN  
Exemple : MP-MAIS-01, MP-TOURTEAU-02

Le premier mot du nom est extrait, les accents supprimés via Normalizer.normalize(nom, NFD), puis mis en majuscules. Le suffixe NN s'incrémente avec countByCodeStartingWith(prefixe). L'utilisateur ne saisit jamais le code.

### PAMP réel pondéré par lot
Le PAMP est calculé à partir du coût unitaire enregistré sur chaque lot :  
PAMP = Σ(lot.coutUnitaire × lot.quantiteInitiale) / Σ(lot.quantiteInitiale)  
Si aucun lot n'existe, le PAMP affiche le coutUnitaire par défaut de la fiche.  
Cette formule est implémentée dans MatierePremiereService.calculerPamp().

### Stock global
Somme des quantite_restante de tous les lots de la matière première, calculée par la requête JPQL COALESCE(SUM(l.quantiteRestante), 0) dans LotMpRepository.

### Alerte de stock (RG.2.3.1)
Si stock ≤ seuilMinimum (et que le seuil est défini) : statut SEUIL ATTEINT.  
Sinon : statut Stock Correct.

### FIFO (RG.2.2.3)
Les lots sont triés par dateAchat ASC, id ASC. Le premier lot dont quantiteRestante > 0 reçoit le statut EN TÊTE DE PILE. Les lots suivants non vides reçoivent EN ATTENTE. Les lots à zéro reçoivent ÉPUISÉ.

### Entrée en stock
Une entrée crée deux enregistrements simultanément :
1. Un lot_mp avec quantiteInitiale = quantiteRestante = quantite reçue, plus cout_unitaire et id_fournisseur du lot (RG03).
2. Un mouvement_stock_mp de type ENTREE avec date_mouvement pour la traçabilité complète (RG03/RG05).

Le coût unitaire est saisi par l'utilisateur dans le formulaire (pré-rempli avec le prix de la fiche, modifiable si le fournisseur applique un tarif différent).

---

## 7. Structure des templates HTML

### Fragment layout/sidebar.html
Barre latérale fixe injectée dans chaque page via th:replace="~{layout/sidebar :: sidebar}".  
Contient : logo, nom du rôle (Responsable Production), un seul lien vers /matieres-premieres.  
Les sous-pages du module sont accessibles depuis la sous-navigation interne à chaque page.

### Fragment layout/header.html
Topbar injectée via th:replace="~{layout/header :: header}".  
Contient : fil d'Ariane (Matières Premières) et badge de rôle.

### liste.html — Suivi du Stock
- *Sous-navigation* : 3 boutons (Suivi Stock actif, Ajouter Matière, Créer Fiche Produit)
- *KPIs* : Total articles, Matières à Seuil, Stock Global (calculé côté serveur)
- *Tableau* : une ligne par MP avec badge badge-danger (SEUIL ATTEINT) ou badge-success
- *Section couverture* : coverage-grid avec une coverage-card par MP ayant un seuil défini ; la barre jauge-fill est rouge si en dessous du seuil, verte sinon ; le pourcentage affiché est calculé en SpEL Thymeleaf : quantiteStock / seuilMinimum × 100

### formulaire.html — Création de fiche
- *Sous-navigation* : Créer Fiche Produit actif
- Formulaire avec menus déroulants fournisseur et unité, champ seuil optionnel

### entree-stock.html — Entrée en stock
- *Sous-navigation* : Ajouter Matière actif
- Menu déroulant MP avec data-prix (PAMP) rempli dynamiquement en JavaScript
- Champ prix en lecture seule (Désactivé — Lié à la fiche technique d'origine)

### fiche.html — Fiche détail
- *Sous-navigation* : ← Retour à la liste + Ajouter Matière + Créer Fiche Produit
- *KPIs* : Stock Global, PAMP, Coût unitaire, Seuil minimum
- *Couverture* : card unique avec barre jauge + détail stock actuel vs seuil
- *Table FIFO* : lots triés avec badges EN TÊTE DE PILE / EN ATTENTE / ÉPUISÉ
- *Alerte* : bandeau RG.2.1.2 (journal en lecture seule)

---

## 8. Dépendances avec les autres modules

- mouvement_stock_mp.id_employe référence l'employé connecté (module *F0 Login*). En attendant F0, l'identifiant est codé en dur à 1 dans MatierePremiereViewController.java.
- Les référentiels unite, fournisseur et type_mouvement (ligne ENTREE) doivent être présents en base avant le lancement (voir donnees-test.sql et init.md).
- ApiResponse et GlobalExceptionHandler sont partagés avec tous les modules de l'équipe.
- Le CSS static/assets/css/style.css est le CSS commun du projet — à ne pas modifier dans ce module.
- Depuis le Sprint 3 (section 11), MatierePremiereService dépend aussi du module *F7 Dépenses* (CategorieDepenseRepository, PhaseRepository, StatutDepenseRepository, DepenseService) : chaque entrée de stock génère automatiquement la dépense d'achat correspondante.

---

## 9. Ajouts Sprint 2 — Conformité RG03 (Entrée.MP)

### Problèmes identifiés sur le Sprint 1

L'audit de conformité RG03 avait révélé quatre lacunes dans l'entrée de matières premières :

| # | Règle RG03 | Statut Sprint 1 |
|---|---|---|
| 1 | Chaque achat enregistre le *fournisseur* | ❌ absent dans lot_mp |
| 2 | Chaque achat enregistre le *coût unitaire* | ❌ absent dans lot_mp, non soumis par le formulaire |
| 3 | Toute entrée enregistre une *date de mouvement* | ❌ mouvement_stock_mp n'avait pas de colonne date |
| 4 | *Suggestions de réapprovisionnement* basées sur la consommation moyenne | ❌ non implémentées |

### Modifications apportées

#### LotMp.java (entité)
Deux colonnes ajoutées :
- id_fournisseur (FK nullable) — fournisseur qui a livré ce lot précisément
- cout_unitaire (nullable) — prix d'achat réel du lot (peut différer du tarif de la fiche si négociation ponctuelle)

Les deux colonnes sont nullable pour la compatibilité avec les données existantes en base. Le service garantit qu'elles sont toujours renseignées sur les nouveaux lots.

#### MouvementStockMp.java (entité)
Colonne date_mouvement ajoutée. Renseignée avec la date de réception lors d'une entrée. Nullable pour la compatibilité avec les anciens mouvements.

#### EntreeStockDTO.java (DTO)
Champ coutUnitaire ajouté (@NotNull, @DecimalMin > 0). Le formulaire soumet maintenant le prix effectif de l'achat.

#### LotDTO.java (DTO)
Champs fournisseurNom et coutUnitaire ajoutés. La fiche détail affiche désormais, pour chaque lot, le fournisseur et le prix de cet achat précis.

#### FicheDetailDTO.java (DTO)
Champ suggestionReapprovisionnement ajouté (BigDecimal). Affiché dans la fiche uniquement si > 0.

#### MouvementStockMpRepository.java
Deux requêtes JPQL ajoutées :
java
BigDecimal sommeTotalSorties(Integer idMp)
// → Σ des quantités SORTIE pour calculer la consommation totale

LocalDate premiereDateSortieAvecDate(Integer idMp)
// → date du premier mouvement SORTIE daté (anchor pour le calcul du taux journalier)


#### MatierePremiereService.java
Trois méthodes privées ajoutées ou modifiées :

*calculerPamp(lots, coutDefaut)*  
PAMP pondéré réel : Σ(coutLot × qtéInitiale) / Σ(qtéInitiale).  
Retourne coutDefaut si aucun lot n'a de coût renseigné (backward-compat).

*calculerSuggestion(idMp, stock, seuilMinimum)*  
Algorithme :
1. Si pas d'historique SORTIE daté → max(0, seuil - stock) (combler le déficit minimum)
2. Si historique disponible → taux journalier = totalSorties / jours, consommation 30 jours extrapolée.  
   Suggestion = max(0, max(consommation30j, seuil) - stock)

*entreeStock(dto)* mis à jour :  
lot.setCoutUnitaire(dto.coutUnitaire())  
lot.setFournisseur(mp.getFournisseur())  
mouvement.setDateMouvement(dto.dateReception())

#### entree-stock.html
Le champ prix, anciennement readonly, est maintenant un <input type="number" name="coutUnitaire"> soumis dans le formulaire. Il est pré-rempli par JavaScript avec le PAMP de la fiche sélectionnée, mais reste éditable pour permettre la saisie du tarif réel de l'achat.

#### fiche.html
- Table des lots : colonnes *Fournisseur* et *Coût unitaire* ajoutées.
- Section *Suggestion de Réapprovisionnement* ajoutée en bas de page (visible uniquement si suggestion > 0).

### Tests mis à jour

MatierePremiereServiceTest.java — entreeStock_creeUnLotEtUnMouvement : constructeur EntreeStockDTO mis à jour avec le paramètre coutUnitaire.

---

## 10. Sprint 2.2 / Rattrapage 2 — Alertes de stock faible (report depuis le projet local)

Dernière mise à jour : 2026-07-03

### Contexte

Tâche du Sprint 2.2 (rattrapage, cf. `Sprint2.2.md`) : vérifier les seuils d'alerte des produits et des matières premières, et les afficher dans le Dashboard. Ce travail avait été fait et vérifié dans le projet local d'abord (voir `docs/rapports-techniques/Rary.md` du projet local, sections 1 à 11), puis reporté ici dans cette copie du projet partagé, qui n'avait ni le calcul de stock/statut côté produit, ni le branchement du Dashboard.

### État constaté dans cette copie avant le report

- `DashboardController.java` utilisait `@RequiredArgsConstructor` avec un champ `RecetteVenteService recetteVenteService` et une méthode `production(Model model)` (calcul de recette mensuelle) — absents du projet local. Route `/stock` sans `Model`, aucune donnée réelle affichée.
- `ProduitDTO.java` n'avait pas les champs `quantiteStock` ni `statut`.
- `ProduitService.java` ne dépendait pas de `LotProduitRepository` (pourtant déjà présent dans ce module avec sa méthode `sommeQuantiteRestante`), n'avait pas de `listerAlertes()`, et `versDTO()` ne calculait ni stock ni statut.
- Module `matierespremieres` (`MatierePremiereService`, `MatierePremiereListDTO`) : déjà conforme, identique au projet local, aucun changement nécessaire.
- `templates/dashboard/stock/index.html` : entièrement statique (données d'exemple codées en dur).

### Modifications appliquées

| Fichier | Modification |
| --- | --- |
| `controller/dashboard/DashboardController.java` | Ajout des champs `produitService` et `matierePremiereService` (constructeur régénéré par Lombok ; `production()` et `recetteVenteService` non touchés) ; `stock()` prend désormais un paramètre `Model` et ajoute les attributs `alertesMp` et `produits`. |
| `dto/produit/ProduitDTO.java` | Ajout des champs `quantiteStock` (`BigDecimal`) et `statut` (`String`). |
| `service/produit/ProduitService.java` | Ajout de la dépendance `LotProduitRepository` ; ajout de `listerAlertes()` ; `versDTO()` calcule désormais `quantiteStock` via `sommeQuantiteRestante` et `statut` via une méthode privée `statut(stock, seuilAlerte)` (`SEUIL ATTEINT` si seuil défini et stock ≤ seuil, sinon `Stock Correct`). |
| `templates/dashboard/stock/index.html` | Ajout de `xmlns:th`. Remplacement des blocs statiques "Seuils Critiques Minimums" et "État des Stocks Réels" par des boucles `th:each` sur `alertesMp` et `produits`, identiques au projet local. |

Aucun fichier du module `matierespremieres` modifié dans cette copie (déjà conforme). Aucune table, migration ni fichier SQL touché — uniquement du code Java/HTML.

### Vérification effectuée

`mvn compile` exécuté à la racine de cette copie (`docs/Vina-Akoho`) : compilation réussie sans erreur, `.class` générés pour `DashboardController`, `ProduitService` et les DTOs modifiés. Pas de vérification fonctionnelle navigateur/curl sur cette copie (pas de base de données connectée dans cet environnement) — seule la compilation a été vérifiée. Le détail complet des tests fonctionnels (exécutés sur le projet local) est dans `docs/rapports-techniques/Rary.md` du projet local, section 8.

---

## 11. Sprint 3 — Génération automatique de la dépense d'achat (Entrée.MP → Dépense, module F7)

Dernière mise à jour : 2026-07-11

### Contexte

Jusqu'ici, une entrée de stock de matière première (entreeStock) ne créait qu'un lot_mp et un mouvement_stock_mp : le coût d'achat (cout_unitaire × quantite) n'était reflété nulle part dans le module Dépenses (F7), qui restait alimenté uniquement par saisie manuelle via DepenseService. Décision : chaque entrée de stock doit désormais générer automatiquement la dépense d'achat correspondante, dans la même transaction que l'entrée de stock.

Constat préalable : aucune donnée n'est seedée pour les tables categorie_depense, phase et statut_depense (ni migration Flyway, ni data.sql, ni DataInitializer). Les libellés utilisés par le code doivent donc être créés automatiquement s'ils n'existent pas encore en base (get-or-create), pour ne pas dépendre d'une saisie manuelle préalable.

### Modifications apportées

| Fichier | Modification |
| --- | --- |
| repository/depense/CategorieDepenseRepository.java | Ajout de la méthode dérivée findByLibelle(String). |
| repository/depense/PhaseRepository.java | Ajout de la méthode dérivée findByLibelle(String). |
| repository/depense/StatutDepenseRepository.java | Ajout de la méthode dérivée findByLibelle(String). |
| service/matierespremieres/MatierePremiereService.java | Constructeur étendu : injection de CategorieDepenseRepository, PhaseRepository, StatutDepenseRepository et DepenseService. Ajout des constantes CATEGORIE_DEPENSE_ACHAT_MP ("Achat Matières Premières"), PHASE_ACHAT_MP ("Phase Initiale"), STATUT_DEPENSE_ACHAT_MP ("Payé"). Nouvelle méthode privée enregistrerDepenseAchat(mp, dto) appelée en fin de entreeStock(). Trois méthodes privées resolveCategorieDepenseAchatMp(), resolvePhaseAchatMp(), resolveStatutDepenseAchatMp() (get-or-create par libellé). |

### Logique métier

- enregistrerDepenseAchat construit un DepenseRequestDTO puis appelle depenseService.creer(...) :
  - montant = coutUnitaire × quantite (calculé, jamais ressaisi)
  - date = dateReception de l'entrée de stock
  - designation = "Achat matière première - {nom MP} - {quantité} {unité} - Fournisseur: {nom fournisseur}"
  - idCategorieDepense / idPhase / idStatutDepense = résolus par resolve*(), qui cherchent la ligne référentielle par libellé et la créent à la volée si absente
- L'appel est fait à l'intérieur du @Transactional existant de entreeStock() : si la création de la dépense échoue, le lot_mp et le mouvement_stock_mp ne sont pas persistés non plus (cohérence garantie entre stock et dépenses).

### Vérification effectuée

mvn compile exécuté à la racine du projet : compilation réussie sans erreur, .class régénérés pour MatierePremiereService et les 3 repositories modifiés. Pas de vérification fonctionnelle navigateur/base de données dans cet environnement — seule la compilation a été vérifiée.
