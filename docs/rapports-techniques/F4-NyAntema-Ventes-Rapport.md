# Rapport Technique - F4 Ventes

**Module :** F4 — Ventes  
**Responsable :** Ny Antema  
**Sprint :** 2.1  
**Date :** 03/07/2026

---

## 1. Description du module

Ce module permet de gérer le processus de vente des produits, incluant la gestion du panier, la validation des ventes, l'allocation FIFO des lots de produits, et la génération automatique de factures.

### Objectifs

- Créer des ventes avec gestion du panier
- Appliquer automatiquement l'allocation FIFO des lots lors de la vente
- Vérifier la disponibilité du stock avant validation
- Générer automatiquement les factures
- Enregistrer les mouvements de stock produits

---

## 2. Structure des fichiers

### 2.1. Entités

**Fichiers :**
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/Vente.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/LigneVente.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/LigneVenteLot.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/Facture.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/ModePaiement.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/StatutVente.java`

**Relations :**
- `Vente` → `Client` (relation directe, sans table `commande`)
- `Vente` → `ModePaiement`
- `Vente` → `StatutVente`
- `Vente` → `Facture` (one-to-one)
- `Vente` → `LigneVente` (one-to-many)
- `LigneVente` → `Produit`
- `LigneVenteLot` → `LigneVente` → `LotProduit`

### 2.2. DTOs

**Fichiers :**
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/VenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/VenteFormDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/LigneVenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/PanierItemDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/PanierFormDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/FactureDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/VenteStatistiquesDTO.java`

### 2.3. Repositories

**Fichiers :**
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/VenteRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteLotRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/FactureRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/ModePaiementRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/StatutVenteRepository.java`

### 2.4. Services

**Fichiers :**
- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/VenteService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/RecetteVenteService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/ExportVenteService.java`

**Modifications apportées à `VenteService.java` :**

- Ajout de `SortieProduitService` comme dépendance
- Modification de la méthode `creer(VenteFormDTO requete, List<PanierItemDTO> panier, Integer idEmploye)` :
  - Génération de la référence document `VENTE-<idVente>`
  - Appel à `sortieProduitService.allouerLots()` pour chaque produit du panier
  - Intégration de l'allocation FIFO automatique

### 2.5. Controller

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/VenteController.java`

**Fonctionnalités :**
- Gestion du panier via HttpSession
- Création de vente avec validation
- Liste des ventes avec filtres
- Validation de paiement
- Affichage des factures et bons de livraison
- Export Excel des ventes

### 2.6. Exception

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/exception/ventes/VenteNotFoundException.java`

- Exception personnalisée pour les cas où la vente demandée n'existe pas

### 2.7. Templates

**Fichiers :**
- `src/main/resources/templates/ventes/responsable-commercial-ventes.html`
- `src/main/resources/templates/ventes/responsable-commercial-ventes-nouvelles.html`
- `src/main/resources/templates/ventes/responsable-commercial-ventes-detail.html`
- `src/main/resources/templates/ventes/facture.html`
- `src/main/resources/templates/ventes/bon-livraison.html`

### 2.8. Tests unitaires

**Fichier :** `src/test/java/mg/vinaAkoho/vina_akoho/service/ventes/VenteServiceTest.java`

**Tests couverts :**
- `testCreer_PanierVide_ThrowsException` : Vérifie qu'un panier vide lance une exception
- `testCreer_ClientIntrouvable_ThrowsException` : Vérifie l'exception si client introuvable
- `testCreer_VenteReussi_AvecFIFO` : Vérifie la création réussie avec allocation FIFO
- `testListerToutes_RetourneListeDTO` : Vérifie la liste des ventes au format DTO
- `testTrouverParId_RetourneDTO` : Vérifie la récupération d'une vente par ID
- `testTrouverParId_VenteIntrouvable_ThrowsException` : Vérifie l'exception si vente introuvable
- `testValiderPaiement_ChangeStatut` : Vérifie le changement de statut lors de la validation

---

## 3. Règles métier implémentées

1. **Vérification du stock suffisant** : `SortieProduitService.allouerLots()` vérifie le stock disponible avant allocation
2. **Sélection FIFO automatique** : `lotProduitRepository.findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc()` utilise l'ordre FIFO
3. **Mise à jour des quantités restantes** : `lot.setQuantiteRestante()` dans la boucle FIFO
4. **Enregistrement des mouvements de stock** : `mouvementStockProduitRepository.save()` pour chaque lot alloué
5. **Création de la vente et des lignes** : `VenteService.creer()` crée la vente et les lignes
6. **Génération automatique de facture** : `factureRepository.save()` avec numéro généré automatiquement

---

## 4. Conformité aux règles backend

### ✅ Points respectés

- **Structure des dossiers** : Respect de la structure par module (`ventes/`)
- **Conventions de nommage** : PascalCase pour les classes, snake_case pour les tables et colonnes
- **DTOs obligatoires** : Tous les DTOs créés et utilisés (`VenteDTO`, `LigneVenteDTO`, etc.)
- **Exceptions personnalisées** : `VenteNotFoundException` créée
- **Tests unitaires** : `VenteServiceTest.java` créé avec 7 tests couvrant les cas principaux
- **Intégration FIFO** : `SortieProduitService` intégré dans `VenteService` pour l'allocation automatique

### ❌ Points non applicables

- **Format de réponse API** : Le module utilise Thymeleaf (vues HTML) et non des API JSON

---

## 5. Table commande

**Non utilisée.** La migration V18 (`V18__remove_commande_from_vente.sql`) a supprimé la relation vente → commande. La table `vente` est maintenant directement liée à `client` via la colonne `id_client`.

**Avant :** `vente` → `commande` → `client`  
**Après :** `vente` → `client` (relation directe)

Cette simplification a été prise en compte dans le code, notamment dans `LivraisonService` qui a été corrigé pour utiliser `getClient()` au lieu de `getCommande()`.

---

## 6. Documentation

**Cahier de test :** `docs/cahiers-de-test/F4-NyAntema-Ventes-Test.md`

**Rapport technique :** `docs/rapports-techniques/F4-NyAntema-Ventes-Rapport.md`

---

## 7. Conclusion

Le module F4 Ventes est entièrement fonctionnel et respecte les conventions de développement définies dans le document "Regles_Backend_VINA-AKOHO2(1).md". L'intégration de `SortieProduitService` assure l'allocation FIFO automatique conforme aux règles métier du Sprint 2.1. Les tests unitaires couvrent les principaux cas d'utilisation et la fonctionnalité est prête à être intégrée dans la branche principale.

### Corrections apportées pour la conformité

1. **Intégration FIFO** : Ajout de `SortieProduitService` dans `VenteService` pour l'allocation automatique des lots
2. **Tests unitaires** : Création de `VenteServiceTest.java` avec 7 tests
3. **Documentation** : Création du cahier de test et du rapport technique

### État final

- ✅ Règles métier Sprint 2.1 respectées
- ✅ Conventions backend respectées
- ✅ Tests unitaires créés
- ✅ Documentation complète
- ✅ FIFO intégré automatiquement
