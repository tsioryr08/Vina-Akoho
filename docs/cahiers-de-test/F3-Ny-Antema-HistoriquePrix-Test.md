# Cahier de Test - Historique des Prix des Produits

**Module :** F3 - Historique des prix des produits  
**Responsable :** Ny Antema  
**Date :** 03/07/2026

---

## Test 1 : Création automatique d'historique lors de la modification de prix

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** http://localhost:8081/api/produits/{id}/modifier  
**Résultat attendu :** Lors de la modification du prix d'un produit, une entrée est automatiquement créée dans la table `historique_prix_produit` avec l'ancien prix, le nouveau prix et la date de modification.  
**Résultat obtenu :** ✅ L'historique est créé automatiquement dans la base de données lors de la modification du prix.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le système détecte correctement les changements de prix et enregistre l'historique.

---

## Test 2 : Affichage de l'historique des prix sur la fiche produit

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** http://localhost:8081/api/produits/{id}  
**Résultat attendu :** La fiche produit affiche une section "Historique des prix" avec un tableau contenant la date, l'ancien prix, le nouveau prix et la modification (avec badge visuel).  
**Résultat obtenu :** ✅ L'historique des prix est affiché correctement avec les badges verts pour les augmentations et rouges pour les baisses.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'affichage est clair et les badges visuels facilitent la lecture des variations de prix.

---

## Test 3 : Aucun historique créé si le prix ne change pas

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** http://localhost:8081/api/produits/{id}/modifier  
**Résultat attendu :** Si le prix reste inchangé lors de la modification d'un produit, aucun nouvel historique ne doit être créé.  
**Résultat obtenu :** ✅ Aucun historique n'est créé lorsque le prix ne change pas.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le système compare correctement les prix et ne crée pas d'historique inutile.

---

## Test 4 : Affichage du message "Aucun historique disponible"

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** http://localhost:8081/api/produits/{id}  
**Résultat attendu :** Pour un produit sans historique de prix, le message "Aucun historique de prix disponible" doit s'afficher.  
**Résultat obtenu :** ✅ Le message s'affiche correctement pour les produits sans historique.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'interface gère correctement le cas où aucun historique n'existe.

---

## Test 5 : Ordre chronologique de l'historique

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** http://localhost:8081/api/produits/{id}  
**Résultat attendu :** L'historique des prix doit être affiché par ordre chronologique décroissant (du plus récent au plus ancien).  
**Résultat obtenu :** ✅ L'historique est affiché dans le bon ordre chronologique.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La requête SQL utilise correctement `ORDER BY date_modification DESC`.

---

## Test 6 : Tests unitaires

**Date :** 03/07/2026  
**Testeur :** Ny Antema  
**Page :** Tests unitaires JUnit  
**Résultat attendu :** Les tests unitaires pour le service d'historique des prix doivent tous passer.  
**Résultat obtenu :** ✅ Tous les tests unitaires passent avec succès.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Les tests couvrent les cas : création d'historique, non-création si prix inchangé, liste d'historique, liste vide.

---

## Résumé

**Tests effectués :** 6  
**Tests réussis :** 6  
**Tests échoués :** 0  
**Taux de réussite :** 100%

**Conclusion :** La fonctionnalité d'historique des prix des produits fonctionne correctement et respecte les règles métier définies dans le Sprint 2.2.
