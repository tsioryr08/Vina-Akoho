# Cahier de Test - F4 Ventes

**Module :** F4 — Ventes  
**Responsable :** Ny Antema  
**Sprint :** 2.1  
**Date :** 03/07/2026

---

## Pré-requis

- Le serveur est démarré avec :
```bash
mvn spring-boot:run
```

- La base de données est accessible et contient des produits avec des lots (`lot_produit`) en stock.
- Les clients et les modes de paiement sont disponibles dans la base.

---

## Test 1 — Ajout d'un produit au panier

**Date :** 03/07/2026  
**Page :** `GET /ventes/nouvelle`  
**Résultat attendu :** Le produit apparaît dans le tableau du panier avec le montant total calculé correctement.  
**Résultat obtenu :** ✅ Produit ajouté avec succès au panier, affichage correct dans le tableau et total calculé.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Fonctionnalité opérationnelle.

---

## Test 2 — Suppression d'un produit du panier

**Date :** 03/07/2026  
**Page :** `GET /ventes/nouvelle`  
**Résultat attendu :** Le produit est retiré du panier et le panier se met à jour correctement.  
**Résultat obtenu :** ✅ Produit supprimé correctement, le panier affiche ensuite l'état vide.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Fonctionnalité opérationnelle.

---

## Test 3 — Validation d'une vente avec stock suffisant

**Date :** 03/07/2026  
**Page :** `GET /ventes/nouvelle`  
**Résultat attendu :** La vente est créée avec succès, le message de succès apparaît, une facture est enregistrée, et les lots sont alloués en FIFO.  
**Résultat obtenu :** ✅ Vente créée avec succès, redirection vers la liste des ventes, facture générée, et allocation FIFO effectuée.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'intégration de `SortieProduitService` permet l'allocation FIFO automatique.

---

## Test 4 — Blocage de la vente en cas de stock insuffisant

**Date :** 03/07/2026  
**Page :** `GET /ventes/nouvelle`  
**Résultat attendu :** La vente est bloquée, un message d'erreur signale le stock insuffisant, et aucune ligne de vente ni mouvement de stock n'est créé.  
**Résultat obtenu :** ✅ La validation a été bloquée avec l'erreur « Stock insuffisant », et aucune vente supplémentaire n'a été créée.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le système vérifie correctement le stock avant de créer la vente.

---

## Test 5 — Vérification de l'allocation FIFO des lots

**Date :** 03/07/2026  
**Page :** `GET /ventes/nouvelle`  
**Résultat attendu :** Les lots ayant la plus ancienne date de fabrication sont consommés en premier, `lot_produit.quantite_restante` est décrémenté correctement.  
**Résultat obtenu :** ✅ Allocation FIFO vérifiée : lot plus ancien épuisé en premier, lot suivant réduit ensuite.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La méthode `SortieProduitService.allouerLots()` utilise correctement l'ordre FIFO.

---

## Test 6 — Vérification des enregistrements de stock et de lot

**Date :** 03/07/2026  
**Résultat attendu :** Un enregistrement `mouvement_stock_produit` existe pour chaque lot utilisé avec la référence `VENTE-<idVente>`.  
**Résultat obtenu :** ✅ Les enregistrements `mouvement_stock_produit` ont été créés avec la bonne référence document.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La référence document est générée correctement dans `VenteService.creer()`.

---

## Test 7 — Tests unitaires JUnit

**Date :** 03/07/2026  
**Page :** Tests unitaires  
**Résultat attendu :** Les tests unitaires pour `VenteService` doivent tous passer.  
**Résultat obtenu :** ✅ Tous les tests unitaires passent avec succès.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Les tests couvrent les cas : panier vide, client introuvable, vente réussie avec FIFO, liste des ventes, vente par ID, validation de paiement.

---

## Résumé

**Tests effectués :** 7  
**Tests réussis :** 7  
**Tests échoués :** 0  
**Taux de réussite :** 100%

**Conclusion :** La fonctionnalité F4 Ventes est conforme aux règles métier du Sprint 2.1 et respecte les conventions du backend. L'intégration de `SortieProduitService` assure l'allocation FIFO automatique lors de la création d'une vente.

---

## Commandes utiles

**Démarrer l'application :**
```bash
mvn spring-boot:run
```

**Lancer les tests unitaires :**
```bash
mvn test -Dtest=VenteServiceTest
```

**Adresse web :**
```text
http://localhost:8081/ventes/nouvelle
```

**Vérification SQL :**
```sql
SELECT * FROM lot_produit WHERE id_produit = <idProduit>;
SELECT * FROM mouvement_stock_produit WHERE reference_document LIKE 'VENTE-%';
SELECT * FROM vente WHERE id = <idVente>;
```
