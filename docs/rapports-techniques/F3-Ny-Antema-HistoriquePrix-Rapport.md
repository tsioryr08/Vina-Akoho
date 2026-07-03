# Rapport Technique - Historique des Prix des Produits

**Module :** F3 - Historique des prix des produits  
**Responsable :** Ny Antema  
**Date :** 03/07/2026  
**Sprint :** 2.2

---

## 1. Description du module

Ce module permet de suivre l'évolution des prix de vente des produits en enregistrant automatiquement chaque modification de prix et en affichant l'historique sur la fiche produit.

### Objectifs

- Enregistrer automatiquement chaque changement de prix de vente
- Afficher l'historique des prix sur la fiche produit
- Faciliter le suivi des variations de prix pour l'analyse commerciale

---

## 2. Structure des fichiers

### 2.1. Base de données

**Migration :** `src/main/resources/db/migration/V19__create_historique_prix_produit.sql`

```sql
CREATE TABLE IF NOT EXISTS historique_prix_produit (
    id SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    ancien_prix DECIMAL(10, 2) NOT NULL CHECK (ancien_prix >= 0),
    nouveau_prix DECIMAL(10, 2) NOT NULL CHECK (nouveau_prix >= 0),
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_employe INTEGER,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE CASCADE,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE SET NULL
);
```

### 2.2. Entité

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/entity/produit/HistoriquePrixProduit.java`

- Relations avec `Produit` et `Employe`
- Champs : id, produit, ancienPrix, nouveauPrix, dateModification, employe
- Lifecycle callback pour la date de modification automatique

### 2.3. DTO

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/dto/produit/HistoriquePrixProduitDTO.java`

- Champs : id, idProduit, nomProduit, ancienPrix, nouveauPrix, dateModification, nomEmploye
- Utilisation de Lombok pour les getters/setters/builders

### 2.4. Repository

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/repository/produit/HistoriquePrixProduitRepository.java`

- Méthodes :
  - `findByProduitIdOrderByDateModificationDesc(Long produitId)` : Récupère l'historique d'un produit par ordre chronologique décroissant
  - `findByProduitOrderByDateModificationDesc(Produit produit)` : Variante avec l'entité

### 2.5. Service

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/service/produit/ProduitService.java`

**Modifications apportées :**

- Ajout de `HistoriquePrixProduitRepository` comme dépendance
- Modification de la méthode `modifier(Long id, ProduitRequestDTO requete)` :
  - Détection des changements de prix
  - Création automatique d'une entrée dans l'historique si le prix a changé
- Ajout de la méthode `versHistoriquePrixDTO(HistoriquePrixProduit historique)` : Conversion entité → DTO
- Ajout de la méthode `listerHistoriquePrix(Long idProduit)` : Récupération de l'historique au format DTO

### 2.6. Controller

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/controller/produit/ProduitController.java`

**Modifications apportées :**

- Suppression de l'injection directe de `HistoriquePrixProduitRepository`
- Modification de la méthode `trouverParId(@PathVariable Long id, Model model)` :
  - Utilisation de `produitService.listerHistoriquePrix(id)` au lieu du repository direct
  - Passage du DTO au modèle Thymeleaf

### 2.7. Exception

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/exception/produit/HistoriquePrixNotFoundException.java`

- Exception personnalisée pour les cas où l'historique demandé n'existe pas

### 2.8. Template

**Fichier :** `src/main/resources/templates/produit/detail.html`

**Ajout d'une section "Historique des prix" :**

- Tableau avec colonnes : Date, Ancien prix, Nouveau prix, Modification
- Badges visuels (vert pour augmentation, rouge pour baisse)
- Message "Aucun historique de prix disponible" si pas d'historique

### 2.9. Tests unitaires

**Fichier :** `src/test/java/mg/vinaAkoho/vina_akoho/service/produit/ProduitServiceHistoriquePrixTest.java`

**Tests couverts :**

- `testModifier_CreeHistoriquePrix_QuandPrixChange` : Vérifie la création d'historique lors d'un changement de prix
- `testModifier_NeCreePasHistoriquePrix_QuandPrixNeChangePas` : Vérifie qu'aucun historique n'est créé si le prix ne change pas
- `testListerHistoriquePrix_RetourneListeDTO` : Vérifie la récupération de l'historique au format DTO
- `testListerHistoriquePrix_RetourneListeVide_QuandPasHistorique` : Vérifie le retour d'une liste vide quand pas d'historique

---

## 3. Règles métier implémentées

1. **Enregistrement automatique** : Toute modification du prix de vente crée automatiquement une ligne dans `historique_prix_produit`
2. **Comparaison de prix** : Le système compare l'ancien et le nouveau prix avant de créer un historique
3. **Ordre chronologique** : L'historique est affiché du plus récent au plus ancien
4. **Cascade delete** : Si un produit est supprimé, son historique de prix est automatiquement supprimé

---

## 4. Conformité aux règles backend

### ✅ Points respectés

- **Structure des dossiers** : Respect de la structure par module (`produit/`)
- **Conventions de nommage** : PascalCase pour les classes, snake_case pour la table et colonnes
- **DTOs obligatoires** : Création de `HistoriquePrixProduitDTO` pour exposer les données
- **Exceptions personnalisées** : Création de `HistoriquePrixNotFoundException`
- **Tests unitaires** : Création de tests JUnit pour le service
- **Migrations** : Migration V19 créée dans `db/migration/`

### ❌ Points non applicables

- **Format de réponse API** : Le module utilise Thymeleaf (vues HTML) et non des API JSON

---

## 5. Documentation

**Cahier de test :** `docs/cahiers-de-test/F3-Ny-Antema-HistoriquePrix-Test.md`

**Rapport technique :** `docs/rapports-techniques/F3-Ny-Antema-HistoriquePrix-Rapport.md`

---

## 6. Conclusion

Le module d'historique des prix des produits est entièrement fonctionnel et respecte les conventions de développement définies dans le document "Regles_Backend_VINA-AKOHO2(1).md". Les tests unitaires couvrent les principaux cas d'utilisation et la fonctionnalité est prête à être intégrée dans la branche principale.
