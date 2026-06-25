# 📋 Cahier de test — F2 Matières premières

Testeur : Rary
Date : 2026-06-23
Module : F2 — Gestion des matières premières (T2.1 à T2.4)
Données de test : `donnees-test.sql`

---

## T2.1 — CRUD Matières premières

---

Date : 2026-06-23
Testeur : Rary
Page : `/matieres-premieres/nouveau`
Registration : `test@vinakoho.mg` / test123
Résultat attendu : Le formulaire de création s'affiche avec les listes déroulantes fournisseur et unité correctement chargées.
Résultat obtenu : Formulaire affiché, 3 fournisseurs et 3 unités dans les menus.
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-06-23
Testeur : Rary
Page : `/matieres-premieres/nouveau`
Registration : `test@vinakoho.mg` / test123
Résultat attendu : Création d'une MP valide (nom="Tourteau de Colza", fournisseur=Agro-HautesTerres, coût=2100, unité=kg, seuil=200) → redirection vers /matieres-premieres, nouvelle ligne visible.
Résultat obtenu : Redirection OK, MP "Tourteau de Colza" apparaît dans la liste avec code MP-TOURTEAU-02 auto-généré.
Statut :
☑ Succès
☐ Échec
Commentaire : Code généré en format MP-TOURTEAU-02 car MP-TOURTEAU-01 existe déjà.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /api/matieres-premieres (via curl/Postman)
Registration : —
Résultat attendu : Création sans nom → réponse HTTP 400 avec message "Le nom est obligatoire".
Résultat obtenu : {"success":false,"message":"Le nom est obligatoire","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : Jakarta Validation fonctionne correctement.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /api/matieres-premieres (via curl/Postman)
Registration : —
Résultat attendu : Création avec coût unitaire négatif (-500) → HTTP 400, message "Le coût unitaire ne peut pas être négatif".
Résultat obtenu : {"success":false,"message":"Le coût unitaire ne peut pas être négatif","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-06-23
Testeur : Rary
Page : POST /api/matieres-premieres (via curl/Postman)
Registration : —
Résultat attendu : Création avec fournisseur inexistant (id=9999) → HTTP 404, message "Fournisseur introuvable : 9999".
Résultat obtenu : {"success":false,"message":"Fournisseur introuvable : 9999","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : FournisseurNotFoundException bien interceptée par GlobalExceptionHandler.

---

Date : 2026-06-23
Testeur : Rary
Page : PUT /api/matieres-premieres/{id}
Registration : —
Résultat attendu : Modification du seuil minimum de MP "Son de Riz" (300 → 400) → fiche retournée avec seuilMinimum=400.
Résultat obtenu : {"success":true,"message":"Matière première modifiée","data":{..."seuilMinimum":400...}}
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-06-23
Testeur : Rary
Page : PUT /api/matieres-premieres/9999
Registration : —
Résultat attendu : Modification d'une MP inexistante → HTTP 404, message "Matière première introuvable : 9999".
Résultat obtenu : {"success":false,"message":"Matière première introuvable : 9999","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-06-23
Testeur : Rary
Page : DELETE /api/matieres-premieres/{id}
Registration : —
Résultat attendu : Suppression de "Tourteau de Colza" (créée au test précédent) → HTTP 200, "Matière première supprimée", disparaît de la liste.
Résultat obtenu : {"success":true,"message":"Matière première supprimée","data":null} — plus visible en GET /api/matieres-premieres.
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

## T2.2 — Suivi des quantités en temps réel

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres
Registration : —
Résultat attendu : "Maïs Concassé" affiche quantiteStock=350 (150+200, issus des 2 lots du donnees-test.sql).
Résultat obtenu : Colonne "Quantité en Stock" = 350 kg pour Maïs Concassé.
Statut :
☑ Succès
☐ Échec
Commentaire : Calcul = somme des quantite_restante de tous les lots actifs.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /matieres-premieres/entree-stock
Registration : —
Résultat attendu : Entrée de 500 kg de "Maïs Concassé" → stock passe de 350 à 850 kg. La page fiche affiche le nouveau lot en bas de la file FIFO.
Résultat obtenu : Stock = 850 kg après soumission. Nouveau lot visible avec statut "EN ATTENTE".
Statut :
☑ Succès
☐ Échec
Commentaire : Mise à jour immédiate, pas de rechargement manuel nécessaire.

---

Date : 2026-06-23
Testeur : Rary
Page : GET /api/matieres-premieres/{id}
Registration : —
Résultat attendu : PAMP de "Maïs Concassé" = 1200 Ar/kg (coût unitaire verrouillé à la création, pas recalculé par lot).
Résultat obtenu : {"pamp":1200.00,...}
Statut :
☑ Succès
☐ Échec
Commentaire : Règle de gestion : le prix d'achat est bloqué à la fiche d'origine, le PAMP = coutUnitaire.

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres/entree-stock
Registration : —
Résultat attendu : Quand on sélectionne "Tourteau de Soja" dans le menu déroulant, le champ "Prix unitaire" se remplit automatiquement avec 1550 Ar (désactivé).
Résultat obtenu : Champ prix = "1550 Ar" après sélection, non modifiable.
Statut :
☑ Succès
☐ Échec
Commentaire : JavaScript lit l'attribut data-prix de l'option sélectionnée.

---

## T2.3 — Seuils d'alerte de réapprovisionnement

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres
Registration : —
Résultat attendu : "Maïs Concassé" (350 kg ≤ seuil 500) → ligne rouge, badge "⚠ SEUIL ATTEINT". "Tourteau de Soja" (1200 kg > seuil 400) → badge vert "Stock Correct".
Résultat obtenu : Conforme aux couleurs attendues.
Statut :
☑ Succès
☐ Échec
Commentaire : RG.2.3.1 appliquée. Ligne entière en rouge pour le Maïs.

---

Date : 2026-06-23
Testeur : Rary
Page : GET /api/matieres-premieres/alertes
Registration : —
Résultat attendu : Seules les MP dont stock ≤ seuil sont retournées (Maïs Concassé + Farine de Poisson Premium).
Résultat obtenu : Liste de 2 éléments avec statut "SEUIL ATTEINT".
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres
Registration : —
Résultat attendu : MP sans seuil_minimum défini (NULL) → pas d'alerte, statut "Stock Correct" même si stock = 0.
Résultat obtenu : Statut "Stock Correct", aucun badge rouge.
Statut :
☑ Succès
☐ Échec
Commentaire : seuilMinimum NULL = pas de seuil défini, aucune alerte déclenchée.

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres (après entrée de 200 kg sur Maïs Concassé)
Registration : —
Résultat attendu : Maïs Concassé stock = 550 kg > seuil 500 → badge passe de "SEUIL ATTEINT" à "Stock Correct", ligne n'est plus rouge.
Résultat obtenu : Badge vert, ligne normale.
Statut :
☑ Succès
☐ Échec
Commentaire : L'alerte disparaît automatiquement dès que le stock repasse au-dessus du seuil.

---

## T2.4 — Historique des approvisionnements / Lots FIFO

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres/{id} (Maïs Concassé)
Registration : —
Résultat attendu : 2 lots affichés dans l'ordre FIFO : LOT du 12/05/2026 en "EN TÊTE DE PILE" (150 kg), LOT du 02/06/2026 en "EN ATTENTE" (200 kg). Stock global = 350 kg.
Résultat obtenu : Conforme — ordre, statuts et stock corrects.
Statut :
☑ Succès
☐ Échec
Commentaire : RG.2.2.3 — lot le plus ancien toujours en tête de pile.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /matieres-premieres/entree-stock (ajout d'un 3e lot)
Registration : —
Résultat attendu : Après entrée de 300 kg de Maïs le 2026-06-23 → fiche affiche 3 lots : lot du 12/05 (EN TÊTE DE PILE), lot du 02/06 (EN ATTENTE), lot du 23/06 (EN ATTENTE). Stock = 650 kg.
Résultat obtenu : 3 lots dans l'ordre chronologique, statuts corrects, stock = 650 kg.
Statut :
☑ Succès
☐ Échec
Commentaire : L'ordre FIFO est maintenu à chaque nouvelle entrée.

---

Date : 2026-06-23
Testeur : Rary
Page : GET /matieres-premieres/{id}
Registration : —
Résultat attendu : Un lot dont quantite_restante = 0 affiche le statut "ÉPUISÉ" (grisé, ne bloque pas la file).
Résultat obtenu : Badge gris "ÉPUISÉ" sur le lot à 0 kg, les lots suivants gardent leurs statuts.
Statut :
☑ Succès
☐ Échec
Commentaire : Un lot épuisé n'est pas en tête de pile ; c'est le prochain lot non-vide qui prend ce rôle.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /api/matieres-premieres/entree-stock (quantite=0)
Registration : —
Résultat attendu : Quantité = 0 → HTTP 400, message "La quantité doit être supérieure à 0".
Résultat obtenu : {"success":false,"message":"La quantité doit être supérieure à 0","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : Validation @DecimalMin(value="0", inclusive=false) fonctionne.

---

Date : 2026-06-23
Testeur : Rary
Page : POST /api/matieres-premieres/entree-stock (sans date)
Registration : —
Résultat attendu : Date absente → HTTP 400, message "La date de réception est obligatoire".
Résultat obtenu : {"success":false,"message":"La date de réception est obligatoire","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

## Tests unitaires automatisés (`MatierePremiereServiceTest`)

Commande : `mvn -Dtest=MatierePremiereServiceTest test`

| # | Méthode de test | Résultat attendu | Statut |
| --- | --- | --- | --- |
| 1 | `creer_genereLeCodeAuFormatAttendu` | code=MP-MAIS-01, pamp=1200 | ✅ |
| 2 | `detail_quandIntrouvable_leveException` | MatierePremiereNotFoundException | ✅ |
| 3 | `lister_stockSousSeuil_donneStatutAlerte` | stock 350 ≤ seuil 500 → "SEUIL ATTEINT" | ✅ |
| 4 | `detail_appliqueFifoSurLesLots` | lot 1 = EN TÊTE, lot 2 = EN ATTENTE, stock 350 | ✅ |
| 5 | `entreeStock_creeUnLotEtUnMouvement` | 1 lot_mp + 1 mouvement_stock_mp sauvegardés | ✅ |

Résultat : **5/5 — 0 échec**

---

## Bilan global

| Tâche | Tests rédigés | Succès | Échecs |
| --- | --- | --- | --- |
| T2.1 CRUD | 7 | 7 | 0 |
| T2.2 Quantités temps réel | 4 | 4 | 0 |
| T2.3 Seuils d'alerte | 4 | 4 | 0 |
| T2.4 Historique / FIFO | 5 | 5 | 0 |
| Tests unitaires auto | 5 | 5 | 0 |
| **Total** | **25** | **25** | **0** |

## Bugs identifiés
Aucun.
