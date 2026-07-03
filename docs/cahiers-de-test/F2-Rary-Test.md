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

## Sprint 2 — Conformité RG03 (fournisseur, coût, date par lot d'achat)

---

Date : 2026-07-03
Testeur : Rary
Page : POST /matieres-premieres/entree-stock
Registration : `test@vinakoho.mg` / test123
Résultat attendu : Entrée de 300 kg de "Tourteau de Soja" avec coût unitaire saisi à 1600 Ar (différent du tarif fiche 1550 Ar, négociation ponctuelle) → le lot créé enregistre coutUnitaire=1600 et le fournisseur de la fiche.
Résultat obtenu : Nouveau lot visible dans la fiche avec "Coût unitaire" = 1600 Ar et "Fournisseur" = Agro-HautesTerres (fournisseur de la fiche).
Statut :
☑ Succès
☐ Échec
Commentaire : RG03 — le prix réel de l'achat est conservé par lot, indépendamment du coutUnitaire par défaut de la fiche.

---

Date : 2026-07-03
Testeur : Rary
Page : POST /api/matieres-premieres/entree-stock (sans coutUnitaire)
Registration : —
Résultat attendu : Coût unitaire absent → HTTP 400, message "Le coût unitaire est obligatoire".
Résultat obtenu : {"success":false,"message":"Le coût unitaire est obligatoire","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : Validation @NotNull sur EntreeStockDTO.coutUnitaire.

---

Date : 2026-07-03
Testeur : Rary
Page : POST /api/matieres-premieres/entree-stock (coutUnitaire=0)
Registration : —
Résultat attendu : Coût unitaire = 0 → HTTP 400, message "Le coût unitaire doit être supérieur à 0".
Résultat obtenu : {"success":false,"message":"Le coût unitaire doit être supérieur à 0","data":null}
Statut :
☑ Succès
☐ Échec
Commentaire : @DecimalMin(value="0", inclusive=false) sur coutUnitaire, même règle que pour la quantité.

---

Date : 2026-07-03
Testeur : Rary
Page : GET /api/matieres-premieres/{id} (Maïs Concassé, 2 lots à coûts différents : 1150 Ar × 150 kg et 1250 Ar × 200 kg)
Registration : —
Résultat attendu : PAMP recalculé = pondéré par lot = (1150×150 + 1250×200) / (150+200) = 1207,14 Ar/kg, et non plus le coutUnitaire figé de la fiche.
Résultat obtenu : {"pamp":1207.14,...}
Statut :
☑ Succès
☐ Échec
Commentaire : Évolution Sprint 2 — remplace l'ancien comportement "PAMP verrouillé à la création" testé dans T2.2 ; MatierePremiereService.calculerPamp() applique désormais Σ(coutLot×qtéInitiale)/Σ(qtéInitiale).

---

Date : 2026-07-03
Testeur : Rary
Page : GET /matieres-premieres/{id} (fiche détail)
Registration : —
Résultat attendu : Chaque ligne de la table des lots affiche les colonnes "Fournisseur" et "Coût unitaire" propres à ce lot (et non plus les valeurs génériques de la fiche).
Résultat obtenu : Table des lots affiche fournisseur + coût par ligne, conforme aux lots créés lors des entrées de stock successives.
Statut :
☑ Succès
☐ Échec
Commentaire : Pour les lots créés avant Sprint 2 (id_fournisseur/cout_unitaire NULL), la fiche retombe sur les valeurs par défaut de la fiche (fallback de compatibilité).

---

Date : 2026-07-03
Testeur : Rary
Page : GET /matieres-premieres/{id} ("Farine de Poisson Premium", seuil=100, stock=60, aucune sortie enregistrée)
Registration : —
Résultat attendu : Section "Suggestion de Réapprovisionnement" affiche 40 (= seuil 100 − stock 60), car aucun historique de sortie n'existe.
Résultat obtenu : Suggestion affichée = "40 kg".
Statut :
☑ Succès
☐ Échec
Commentaire : Cas sans historique (RG03) — calculerSuggestion() comble simplement le déficit par rapport au seuil.

---

Date : 2026-07-03
Testeur : Rary
Page : GET /matieres-premieres/{id} (MP avec historique de sorties : 300 kg sorties sur 15 jours, seuil=200, stock=100)
Registration : —
Résultat attendu : Consommation extrapolée sur 30 jours = 300/15×30 = 600 kg (arrondi supérieur) ; cible = max(600, seuil 200) = 600 ; suggestion = 600 − 100 = 500 kg.
Résultat obtenu : Suggestion affichée = "500 kg".
Statut :
☑ Succès
☐ Échec
Commentaire : Cas avec historique — le taux journalier moyen (totalSorties/jours) est extrapolé sur 30 jours, arrondi CEILING.

---

Date : 2026-07-03
Testeur : Rary
Page : GET /matieres-premieres/{id} (MP dont stock ≥ suggestion calculée)
Registration : —
Résultat attendu : Suggestion calculée ≤ 0 → section "Suggestion de Réapprovisionnement" masquée dans fiche.html (affichée uniquement si > 0).
Résultat obtenu : Section absente de la page, aucune valeur négative affichée.
Statut :
☑ Succès
☐ Échec
Commentaire : calculerSuggestion() retourne max(0, ...) — jamais de suggestion négative.

---

## Sprint 2.2 — Alertes de stock faible dans le Dashboard (`/stock`)

---

Date : 2026-07-03
Testeur : Rary
Page : GET /stock
Registration : `test@vinakoho.mg` / test123
Résultat attendu : La carte "Seuils Critiques Minimums" liste uniquement les matières premières dont le stock ≤ seuil (Maïs Concassé + Farine de Poisson Premium), avec fournisseur, quantité restante et seuil.
Résultat obtenu : 2 lignes affichées, correspondant à MatierePremiereService.listerAlertes().
Statut :
☑ Succès
☐ Échec
Commentaire : DashboardController.stock() ajoute l'attribut "alertesMp" au modèle.

---

Date : 2026-07-03
Testeur : Rary
Page : GET /stock
Registration : `test@vinakoho.mg` / test123
Résultat attendu : La table "État des Stocks Réels" liste tous les produits actifs avec leur quantité en stock réelle (calculée via LotProduitRepository.sommeQuantiteRestante) et un badge de statut.
Résultat obtenu : Tous les produits actifs affichés, quantités correctes, badges cohérents avec le seuil de chaque produit.
Statut :
☑ Succès
☐ Échec
Commentaire : Avant ce Sprint, la page était entièrement statique (données d'exemple codées en dur).

---

Date : 2026-07-03
Testeur : Rary
Page : GET /stock (produit "Vina Croissance", stock=40, seuilAlerte=50)
Registration : —
Résultat attendu : Badge rouge "Seuil Atteint" affiché pour ce produit (stock ≤ seuilAlerte).
Résultat obtenu : Badge "Seuil Atteint" (badge-light-red) visible sur la ligne du produit.
Statut :
☑ Succès
☐ Échec
Commentaire : ProduitService.statut() — même règle RG.2.3.1 que pour les matières premières, appliquée cette fois au seuilAlerte du produit.

---

Date : 2026-07-03
Testeur : Rary
Page : GET /stock (produit sans seuilAlerte défini, stock=0)
Registration : —
Résultat attendu : Badge vert "Normal" malgré un stock à 0, car seuilAlerte est NULL (pas de seuil défini = pas d'alerte).
Résultat obtenu : Badge "Normal" (badge-light-green) affiché.
Statut :
☑ Succès
☐ Échec
Commentaire : Cohérent avec la règle déjà appliquée aux matières premières (T2.3).

---

Date : 2026-07-03
Testeur : Rary
Page : GET /stock (base sans aucune matière première sous seuil)
Registration : —
Résultat attendu : Message "✓ Aucune matière première sous le seuil d'alerte." affiché à la place de la liste.
Résultat obtenu : Message conforme affiché, aucune ligne d'alerte.
Statut :
☑ Succès
☐ Échec
Commentaire : th:if="${#lists.isEmpty(alertesMp)}" dans dashboard/stock/index.html.

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
| Sprint 2 — RG03 (fournisseur/coût/date par lot) | 8 | 8 | 0 |
| Sprint 2.2 — Alertes Dashboard `/stock` | 5 | 5 | 0 |
| Tests unitaires auto | 5 | 5 | 0 |
| **Total** | **38** | **38** | **0** |

## Bugs identifiés
Aucun.
