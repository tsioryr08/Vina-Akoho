# Cahier de test — F4 Ventes

**Module :** F4 — Ventes
**Testeur :** NyAntema
**Branche :** feature/f4-ventes

---

## Pré-requis

- Le serveur est démarré avec :

```bash
mvn spring-boot:run
```

- La base de données est accessible et contient des produits avec des lots (`lot_produit`) en stock.
- Les clients et les modes de paiement sont disponibles dans la base.

## Données de test à insérer

Avant de lancer les tests, injecter les données de test F4 avec le script suivant :

```bash
psql -h localhost -p 5432 -U vinakoho -d vinakoho -f docs/sql/insert_f4_test_data.sql
```

Données créées par le script :
- un client de test `Client Test`
- une catégorie `Test Ventes`
- un produit `Produit Test F4`
- deux lots FIFO pour ce produit
- des services et types de clients nécessaires à la table `client`

Effectuer les tests un par un dans l'ordre indiqué.

## Instructions de reproduction

1. Démarrer l’application depuis le répertoire du projet :

```bash
mvn spring-boot:run
```

2. Vérifier que l’application est accessible :

```text
http://localhost:8082/
```

3. Se connecter avec le compte commercial :

- Email : `commercial@vinaakoho.mg`
- Mot de passe : `commercial123`

4. Ouvrir la page de vente :

```text
http://localhost:8082/ventes/nouvelle
```

5. Vérifier les identifiants de test utiles :

```sql
SELECT id FROM produit WHERE ref='TEST-PROD-001';
SELECT id FROM client WHERE nom='Client' AND prenom='Test';
SELECT id, libelle FROM mode_paiement ORDER BY id;
```

6. Réinitialiser l’inventaire du produit de test avant le test 5 :

```sql
UPDATE lot_produit
SET quantite_restante = quantite_initiale
WHERE id_produit = (SELECT id FROM produit WHERE ref='TEST-PROD-001');
```

7. Vérifier les résultats en base après chaque vente :

```sql
SELECT * FROM lot_produit WHERE id_produit = (SELECT id FROM produit WHERE ref='TEST-PROD-001');
SELECT * FROM mouvement_stock_produit WHERE reference_document LIKE 'VENTE-%';
SELECT * FROM ligne_vente_lot WHERE id_ligne_vente IN (SELECT id FROM ligne_vente WHERE id_vente = <idVente>);
```

## Test 1 — Ajout d'un produit au panier

Date : 02/07/2026

Page : `GET /ventes/nouvelle`

Étapes :
1. Ouvrir la page de nouvelle vente.
2. Choisir un produit existant.
3. Saisir une quantité valide (> 0).
4. Cliquer sur `Ajouter au panier`.

Résultat attendu :
- Le produit apparaît dans le tableau du panier.
- Le montant total est calculé correctement.

Statut :
☑ Succès
☐ Échec

Commentaire :
Produit ajouté avec succès au panier, affichage correct dans le tableau et total calculé à 15 000 Ar pour 1 kg.

## Test 2 — Suppression d'un produit du panier

Date : 02/07/2026

Page : `GET /ventes/nouvelle`

Étapes :
1. Ajouter un produit au panier.
2. Cliquer sur le bouton `X` de la ligne du produit.

Résultat attendu :
- Le produit est retiré du panier.
- Le panier se met à jour correctement.

Statut :
☑ Succès
☐ Échec

Commentaire :
Produit supprimé correctement, le panier affiche ensuite l’état vide.

## Test 3 — Validation d'une vente avec stock suffisant

Date : 02/07/2026

Page : `GET /ventes/nouvelle`

Étapes :
1. Ajouter un produit ayant suffisamment de stock.
2. Choisir un client.
3. Choisir un mode de paiement.
4. Cliquer sur `Enregistrer la vente`.

Résultat attendu :
- La vente est créée avec succès.
- Le message de succès apparaît.
- Une facture est enregistrée.

Statut :
☑ Succès
☐ Échec

Commentaire :
Vente créée avec succès, redirection vers la liste des ventes et facture générée (`FACT-1782942146227`).

## Test 4 — Blocage de la vente en cas de stock insuffisant

Date : 02/07/2026

Page : `GET /ventes/nouvelle`

Étapes :
1. Ajouter un produit dont la quantité demandée dépasse le stock disponible.
2. Choisir un client.
3. Choisir un mode de paiement.
4. Cliquer sur `Enregistrer la vente`.

Résultat attendu :
- La vente est bloquée.
- Un message d'erreur signale le stock insuffisant.
- Aucune ligne de vente ni mouvement de stock n'est créé.

Statut :
☑ Succès
☐ Échec

Commentaire :
La validation a été bloquée avec l’erreur « Impossible de créer la vente : Stock insuffisant ... », et aucune vente supplémentaire n’a été créée.

## Test 5 — Vérification de l'allocation FIFO des lots

Date : 02/07/2026

Page : `GET /ventes/nouvelle`

Étapes :
1. Prendre un produit avec plusieurs lots en stock.
2. Choisir une quantité inférieure ou égale au stock total.
3. Valider la vente.
4. Vérifier les lots consommés en base.

Résultat attendu :
- Les lots ayant la plus ancienne date de fabrication sont consommés en premier.
- `lot_produit.quantite_restante` est décrémenté correctement.
- Les lots les plus récents ne sont pas utilisés tant que les anciens ne sont pas épuisés.

Statut :
☑ Succès
☐ Échec

Commentaire :
Allocation FIFO vérifiée : lot plus ancien épuisé en premier, lot suivant réduit ensuite.

## Test 6 — Vérification des enregistrements de stock et de lot

Date : 02/07/2026

Étapes :
1. Après une vente réussie, vérifier en base :
   - `mouvement_stock_produit`
   - `ligne_vente_lot`

Résultat attendu :
- Un enregistrement `mouvement_stock_produit` existe pour chaque lot utilisé.
- Un enregistrement `ligne_vente_lot` existe pour chaque lot lié à la ligne de vente.
- La référence du document doit contenir `VENTE-<idVente>`.

Statut :
☑ Succès
☐ Échec

Commentaire :
Deux enregistrements `ligne_vente_lot` et deux `mouvement_stock_produit` ont été créés pour la vente `VENTE-26`.

## Commandes utiles

```bash
mvn spring-boot:run
psql -h localhost -p 5432 -U vinakoho -d vinakoho -f docs/sql/insert_f4_test_data.sql
```

Adresse web :

```text
http://localhost:8082/ventes/nouvelle
```

Connexion test :

- `commercial@vinaakoho.mg`
- `commercial123`

Vérification SQL :

```sql
SELECT id FROM produit WHERE ref='TEST-PROD-001';
SELECT id FROM client WHERE nom='Client' AND prenom='Test';
SELECT * FROM lot_produit WHERE id_produit = (SELECT id FROM produit WHERE ref='TEST-PROD-001');
SELECT * FROM mouvement_stock_produit WHERE reference_document LIKE 'VENTE-%';
SELECT * FROM ligne_vente_lot WHERE id_ligne_vente = <id>;
```

Réinitialiser les lots avant le test 5 :

```sql
UPDATE lot_produit
SET quantite_restante = quantite_initiale
WHERE id_produit = (SELECT id FROM produit WHERE ref='TEST-PROD-001');
```
