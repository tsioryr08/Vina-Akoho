# Cahier de Test - F4 Historique des Ventes

**Module :** F4 — Ventes (Historique)  
**Responsable :** Ny Antema  
**Sprint :** 3  
**Date :** 06/07/2026

---

## Pré-requis

- Le serveur est démarré avec :
```bash
mvn spring-boot:run
```

- La base de données contient des ventes avec différents statuts (Validée, En attente de paiement, Annulée)
- Les clients, produits et factures sont disponibles dans la base
- Les livraisons sont configurées pour certaines ventes

---

## Test 1 — Affichage de la liste des ventes

**Date :** 06/07/2026  
**Page :** GET /ventes  
**Résultat attendu :** La page affiche la liste de toutes les ventes avec les informations principales (Client, Date, Montant, Mode de paiement, Statut).  
**Résultat obtenu :**  Liste des ventes affichée correctement avec toutes les informations requises.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Fonctionnalité opérationnelle.

---

## Test 2 — Tri automatique par date décroissante

**Date :** 06/07/2026  
**Page :** GET /ventes  
**Résultat attendu :** Les ventes sont triées automatiquement de la plus récente à la plus ancienne.  
**Résultat obtenu :**  Tri par date décroissante appliqué par défaut.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le tri automatique fonctionne correctement.

---

## Test 3 — Pagination (10 éléments par défaut)

**Date :** 06/07/2026  
**Page :** GET /ventes  
**Résultat attendu :** La page affiche 10 ventes par défaut avec des contrôles de pagination.  
**Résultat obtenu :**  Pagination fonctionnelle avec 10 éléments par défaut, configurable (5, 10, 20, 50).  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La pagination est configurable et fonctionne correctement.

---

## Test 4 — Recherche par client

**Date :** 06/07/2026  
**Page :** GET /ventes?recherche=<nom_client>  
**Résultat attendu :** Les résultats affichent uniquement les ventes du client recherché.  
**Résultat obtenu :**  Recherche par client fonctionne correctement (recherche textuelle approximative).  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La recherche est insensible à la casse et cherche dans le nom du client.

---

## Test 5 — Recherche par produit

**Date :** 06/07/2026  
**Page :** GET /ventes?recherche=<nom_produit>  
**Résultat attendu :** Les résultats affichent uniquement les ventes contenant le produit recherché.  
**Résultat obtenu :**  Recherche par produit fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La recherche parcourt les lignes de vente pour trouver le produit.

---

## Test 6 — Recherche par numéro de facture

**Date :** 06/07/2026  
**Page :** GET /ventes?recherche=<numero_facture>  
**Résultat attendu :** Les résultats affichent uniquement la vente correspondant au numéro de facture.  
**Résultat obtenu :**  Recherche par numéro de facture fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** La recherche trouve les ventes par numéro de facture exact ou partiel.

---

## Test 7 — Filtre par date début/fin

**Date :** 06/07/2026  
**Page :** GET /ventes?dateDebut=<date>&dateFin=<date>  
**Résultat attendu :** Les résultats affichent uniquement les ventes dans la période spécifiée.  
**Résultat obtenu :**  Filtre par période fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Les dates sont inclusives et le format est correctement géré.

---

## Test 8 — Filtre par mode de paiement

**Date :** 06/07/2026  
**Page :** GET /ventes?modePaiement=<mode>  
**Résultat attendu :** Les résultats affichent uniquement les ventes avec le mode de paiement spécifié.  
**Résultat obtenu :**  Filtre par mode de paiement fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le filtre utilise une correspondance exacte avec le libellé du mode de paiement.

---

## Test 9 — Filtre par statut

**Date :** 06/07/2026  
**Page :** GET /ventes?statut=<statut>  
**Résultat attendu :** Les résultats affichent uniquement les ventes avec le statut spécifié (Validée, En attente de paiement, Annulée).  
**Résultat obtenu :**  Filtre par statut fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Les trois statuts principaux sont filtrables.

---

## Test 10 — Filtre par livraison (avec/sans)

**Date :** 06/07/2026  
**Page :** GET /ventes?avecLivraison=true ou GET /ventes?avecLivraison=false  
**Résultat attendu :** Les résultats affichent uniquement les ventes avec ou sans livraison selon le filtre.  
**Résultat obtenu :**  Filtre par livraison fonctionne correctement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le filtre vérifie la présence ou l'absence de livraison associée.

---

## Test 11 — Tri par date

**Date :** 06/07/2026  
**Page :** GET /ventes?triPar=dateVente&ordreTri=asc ou desc  
**Résultat attendu :** Les ventes sont triées par date dans l'ordre spécifié.  
**Résultat obtenu :**  Tri par date fonctionne correctement (croissant et décroissant).  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le tri par date respecte l'ordre spécifié.

---

## Test 12 — Tri par montant

**Date :** 06/07/2026  
**Page :** GET /ventes?triPar=montantTotal&ordreTri=asc ou desc  
**Résultat attendu :** Les ventes sont triées par montant dans l'ordre spécifié.  
**Résultat obtenu :**  Tri par montant fonctionne correctement (croissant et décroissant).  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le tri par montant respecte l'ordre spécifié.

---

## Test 13 — Tri par client

**Date :** 06/07/2026  
**Page :** GET /ventes?triPar=clientNom&ordreTri=asc ou desc  
**Résultat attendu :** Les ventes sont triées par nom de client dans l'ordre spécifié.  
**Résultat obtenu :**  Tri par client fonctionne correctement (croissant et décroissant).  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le tri par client est insensible à la casse.

---

## Test 14 — Indépendance Recherche/Filtres/Tri

**Date :** 06/07/2026  
**Page :** GET /ventes  
**Résultat attendu :** La recherche, les filtres et le tri fonctionnent de manière indépendante. Chaque section peut être utilisée séparément.  
**Résultat obtenu :**  Les trois fonctionnalités sont indépendantes. Les paramètres sont préservés entre les sections.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'interface est structurée en 3 sections distinctes avec champs cachés pour préserver les paramètres.

---

## Test 15 — Affichage des détails de vente

**Date :** 06/07/2026  
**Page :** GET /ventes/{id}  
**Résultat attendu :** La page affiche les détails complets de la vente (client, facture, lignes de vente, livraison si applicable).  
**Résultat obtenu :**  Détails de vente affichés correctement avec toutes les informations.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Les informations de livraison sont affichées si elles existent, avec gestion des erreurs.

---

## Test 16 — Affichage des unités de produits

**Date :** 06/07/2026  
**Page :** GET /ventes/{id}  
**Résultat attendu :** Les lignes de vente affichent l'unité réelle du produit (kg, sac, carton, litre, pièce, etc.).  
**Résultat obtenu :**  Unités de produits affichées correctement sans valeur par défaut incorrecte.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'unité est affichée uniquement si elle n'est pas vide.

---

## Test 17 — Annulation de commande

**Date :** 06/07/2026  
**Page :** POST /ventes/{id}/annuler  
**Résultat attendu :** La commande est annulée uniquement si son statut est "En attente de paiement". Le statut passe à "Annulée".  
**Résultat obtenu :**  Annulation fonctionnelle uniquement pour les commandes en attente de paiement.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le bouton d'annulation n'apparaît que pour les commandes annulables.

---

## Test 18 — Validation de paiement

**Date :** 06/07/2026  
**Page :** POST /ventes/{id}/valider-paiement  
**Résultat attendu :** Le statut de la commande passe à "Validée" ou "En livraison" si une livraison est requise.  
**Résultat obtenu :**  Validation de paiement fonctionnelle avec changement de statut approprié.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** Le statut de livraison est synchronisé si applicable.

---

## Test 19 — Gestion des erreurs de livraison

**Date :** 06/07/2026  
**Page :** GET /ventes/{id} (vente avec livraison)  
**Résultat attendu :** En cas d'erreur lors de la récupération des informations de livraison, la page s'affiche quand même sans les informations de livraison.  
**Résultat obtenu :**  Gestion des erreurs robuste avec try-catch. Création de DTO minimal en cas d'échec.  
**Statut :**  
☑ Succès  
☐ Échec  
**Commentaire :** L'erreur ne provoque pas de crash de l'application.

---

## Résumé

**Tests effectués :** 19  
**Tests réussis :** 19  
**Tests échoués :** 0  
**Taux de réussite :** 100%

**Conclusion :** La fonctionnalité F4 Historique des Ventes est conforme aux règles métier du Sprint 3. Toutes les fonctionnalités de recherche, filtrage, tri, pagination sont opérationnelles. La gestion de la livraison, l'annulation de commande et la validation de paiement fonctionnent correctement. L'architecture est robuste avec une gestion des erreurs appropriée.

---

## Commandes utiles

**Démarrer l'application :**
```bash
mvn spring-boot:run
```

**Adresse web :**
```text
http://localhost:8081/ventes
```

**Tests de recherche :**
```text
http://localhost:8081/ventes?recherche=Dupont
http://localhost:8081/ventes?recherche=Poulet
http://localhost:8081/ventes?recherche=FACT-123456
```

**Tests de filtres :**
```text
http://localhost:8081/ventes?modePaiement=Espèces
http://localhost:8081/ventes?statut=Validée
http://localhost:8081/ventes?avecLivraison=true
http://localhost:8081/ventes?dateDebut=2026-01-01&dateFin=2026-12-31
```

**Tests de tri :**
```text
http://localhost:8081/ventes?triPar=dateVente&ordreTri=desc
http://localhost:8081/ventes?triPar=montantTotal&ordreTri=asc
http://localhost:8081/ventes?triPar=clientNom&ordreTri=asc
```

**Vérification SQL :**
```sql
SELECT * FROM vente ORDER BY date_vente DESC;
SELECT * FROM vente WHERE id_client IN (SELECT id FROM client WHERE nom LIKE '%Dupont%');
SELECT * FROM livraison WHERE id_vente = <idVente>;
```
