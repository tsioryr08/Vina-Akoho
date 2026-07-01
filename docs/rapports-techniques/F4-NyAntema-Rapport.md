# Rapport technique — F4 Ventes

**Module :** F4 — Ventes
**Développeur :** NyAntema
**Branche :** feature/f4-ventes

---

## 1. Objectif du module

Permettre la création d'une vente à partir d'un panier, en vérifiant le stock produit disponible.
Le module doit allouer automatiquement les lots produits les plus anciens (FIFO), mettre à jour la quantité restante des lots et enregistrer les mouvements de sortie en base.

## 2. Fonctionnalités implémentées

- Ajout de ligne produit dans un panier de vente.
- Validation de la vente avec un client et un mode de paiement.
- Vérification du stock disponible sur les lots produits avant validation.
- Allocation FIFO des lots via `LotProduit.dateFabrication`.
- Décrément de `LotProduit.quantiteRestante` pour chaque lot utilisé.
- Enregistrement d'un `MouvementStockProduit` pour chaque sortie de lot.
- Enregistrement de `LigneVenteLot` pour tracer chaque lot utilisé par ligne de vente.
- Génération d'une facture associée à la vente.

## 3. Architecture et flux métier

### Principaux composants

- `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/VenteController.java`
  - Gère l'affichage du formulaire de vente.
  - Reçoit les actions d'ajout au panier et de validation de la vente.

- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/VenteService.java`
  - Orchestration de la création de commande, vente, lignes de commande, lignes de vente, facturation.
  - Délègue l'allocation FIFO des lots produits au service de stock.

- `src/main/java/mg/vinaAkoho/vina_akoho/service/stockproduit/SortieProduitService.java`
  - Vérifie l'état du stock produit.
  - Alloue automatiquement les lots par ordre chronologique (FIFO).
  - Met à jour `quantiteRestante` et crée les mouvements de stock.

### Entités clés

- `LigneVente` : ligne de vente associée à un produit et à une vente.
- `LigneVenteLot` : relation entre une ligne de vente et le(s) lot(s) produit(s) utilisés.
- `LotProduit` : lot produit contenant la quantité restante.
- `MouvementStockProduit` : enregistrement du mouvement de sortie de stock.
- `Facture` : facture générée pour la vente.

## 4. Routes et templates

### Templates

- `src/main/resources/templates/ventes/responsable-commercial-ventes-nouvelles.html`
  - Formulaire de saisie et affichage du panier.
  - Soumission vers `POST /ventes/panier/ajouter` et `POST /ventes/valider`.

### Routes

- `POST /ventes/panier/ajouter` → ajoute un produit au panier.
- `POST /ventes/panier/{idProduit}/supprimer` → supprime un produit du panier.
- `POST /ventes/valider` → crée la vente et déclenche l'allocation FIFO.

## 5. Exécution du module

1. Lancer l'application :

```bash
mvn spring-boot:run
```

2. Ouvrir le navigateur sur :

```text
http://localhost:8080/ventes/nouvelle
```

3. Remplir le panier et valider la vente.

## 6. Test et vérification

Les vérifications à réaliser après exécution :

- Le panier est bien affiché et les produits ajoutés apparaissent.
- La vente est créée lorsque le stock est suffisant.
- La table `lot_produit` voit ses quantités restantes décrémentées.
- La table `mouvement_stock_produit` contient les mouvements de sortie correspondants.
- La table `ligne_vente_lot` contient les allocations lot/ligne de vente.
- Si le stock est insuffisant, la vente doit être bloquée avec une erreur.

## 7. Résultats attendus

- Vente enregistrée avec succès.
- Facture générée.
- FIFO appliqué : les lots les plus anciens sont consommés en premier.
- Traçabilité de stock assurée par `mouvement_stock_produit`.
- Traçabilité de lot par `ligne_vente_lot`.

---

## 8. Observations

- Le module utilise le template de vente déjà présent dans le projet.
- Aucune modification du template n'est nécessaire pour le fonctionnement basique.
- Une amélioration future possible : afficher le stock disponible par produit directement dans le formulaire.
