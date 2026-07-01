# Rapport Technique — F3-Mpiaro-Rapport.md
## Module : Entrée Produit (`= Production`)

---

## Structure des fichiers
```Text
entity/entreeproduit/LotProduit.java
entity/entreeproduit/MouvementStockProduit.java
entity/entreeproduit/Fabrication.java
entity/entreeproduit/FabricationMp.java
dto/entreeproduit/EntreeProduitRequestDTO.java
dto/entreeproduit/EntreeProduitResponseDTO.java
dto/entreeproduit/DetailConsommationMpDTO.java
repository/entreeproduit/LotProduitRepository.java
repository/entreeproduit/MouvementStockProduitRepository.java
repository/entreeproduit/FabricationRepository.java
repository/entreeproduit/FabricationMpRepository.java
service/entreeproduit/EntreeProduitService.java
controller/entreeproduit/EntreeProduitController.java
controller/entreeproduit/EntreeProduitViewController.java
exception/entreeproduit/ProduitNotFoundException.java
exception/entreeproduit/RecetteInexistanteException.java
templates/dashboard/production/entree-produit.html
templates/layout/responsableProduction.html (fragment sidebar, modifié)
```
---

## Fonctions principales

### `produire(EntreeProduitRequestDTO dto) → EntreeProduitResponseDTO`

Orchestre l'intégralité du processus de production (7 étapes de la règle métier) :

1. Charge le produit et l'employé responsable
2. Lit la recette active de la catégorie du produit (`RecetteProduitRepository.findByIdCategorieAndIsActiveTrue`)
3. Vérifie que le stock de chaque matière première est suffisant **avant** toute modification (calcul : `quantite_mp_recette × quantiteAProduire`)
4. Crée le `lot_produit` et l'enregistrement `fabrication` (traçabilité)
5. Déduit les matières premières en **FIFO** (lot le plus ancien en premier), lot par lot, jusqu'à couvrir le besoin — enregistre un `mouvement_stock_mp` (Sortie) et une ligne `fabrication_mp` par lot consommé
6. Enregistre le `mouvement_stock_produit` (Entrée) pour le lot produit créé
7. Le stock produit disponible est calculé à la volée (pas de champ dénormalisé sur `produit`), via `SUM(lot_produit.quantite_restante)`

Toute la méthode est `@Transactional` : en cas de stock insuffisant détecté en cours de boucle (sécurité redondante avec l'étape 3), tout est annulé (rollback), y compris le `lot_produit` et la `fabrication` déjà persistés — vérifié par test (voir cahier de test, Test 2).

### `getStockDisponible(Long idProduit) → BigDecimal`

Retourne le stock total disponible d'un produit (somme des `quantite_restante` sur tous ses `lot_produit`).

---

## Logique métier

### Déduction FIFO des matières premières

Pour chaque ligne de recette, les lots de matière première sont récupérés triés par `date_achat` croissante (méthode déjà existante `LotMpRepository.findByMatierePremiereIdAndQuantiteRestanteGreaterThanOrderByDateAchatAsc`). La consommation se fait lot par lot : on prend le minimum entre la quantité restante du lot et le besoin restant, jusqu'à couvrir intégralement le besoin. Chaque prélèvement génère une ligne `mouvement_stock_mp` et une ligne `fabrication_mp` (traçabilité fine du lot consommé).

### Traçabilité de la production

Les tables `fabrication` et `fabrication_mp` (déjà présentes dans le schéma) sont utilisées pour tracer précisément quelle production a consommé quels lots de matières premières, en quelle quantité — utile pour un futur calcul de coût de revient ou un audit de traçabilité alimentaire.

---

## Correctif appliqué en cours de développement

**Problème :** l'ancienne contrainte `uq_recette_active_par_categorie` (UNIQUE sur `id_categorie` seul) n'avait pas été supprimée après la migration de Tsiory vers `uq_recette_active_par_categorie_mp`, provoquant une erreur de contrainte dupliquée lors de l'insertion de plusieurs matières premières actives pour une même catégorie.

**Solution appliquée :**
```sql
DROP INDEX IF EXISTS uq_recette_active_par_categorie;
```
(La contrainte `uq_recette_active_par_categorie_mp` créée par Tsiory reste seule active et suffit à garantir l'intégrité.)

**⚠️ Point de vigilance transversal :** ce correctif doit être appliqué sur **toutes** les bases de l'équipe (chaque développeur en local) sinon l'erreur réapparaîtra pour quiconque tente d'insérer une recette multi-MP.

---

## Point ouvert — Unité du produit fini

Le schéma ne définit pas de colonne `id_unite` sur la table `produit`. Pour renseigner l'unité du `mouvement_stock_produit` (Entrée), j'ai temporairement réutilisé l'unité de la première ligne de recette active du produit. **Ce contournement doit être validé ou remplacé par les Architectes Backend** — proposition : ajouter une colonne `id_unite` sur `produit`, ou définir une unité "pièce/sac" par défaut pour tous les produits finis.

---

## Dépendances avec les autres modules

| Sens | Module | Détail |
|---|---|---|
| ⬅️ Dépend de | F1 Produits (Nekena) | Entité `Produit`, `ProduitRepository` |
| ⬅️ Dépend de | F2 Matières premières (Rary) | Entités `MatierePremiere`, `LotMp`, `Unite`, `MouvementStockMp`, `TypeMouvement` |
| ⬅️ Dépend de | Recette Produit (Tsiory) | Lecture de la recette active (`RecetteProduitRepository.findByIdCategorieAndIsActiveTrue`) pour calculer les besoins en MP |
| ➡️ Sera utilisé par | F4 Ventes (Ny Antema) | Le `lot_produit` créé ici alimente le stock disponible consommé en FIFO lors d'une vente |

---

## Endpoints API

| Méthode | Route | Description |
|---|---|---|
| `POST` | `/api/entrees-produit` | Déclenche une production : déduit les MP selon la recette, crée un lot produit |
| `GET` | `/api/entrees-produit/stock/{idProduit}` | Retourne le stock disponible d'un produit |
| `GET` | `/production/entree-produit` | Page Thymeleaf du formulaire de production |

> Authentification requise (session) pour toutes les routes API.