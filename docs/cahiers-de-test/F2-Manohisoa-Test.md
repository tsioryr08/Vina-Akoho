# Cahier de test - F2 Registre des depenses

Testeur : Manohisoa
Date : 2026-07-03
Module : F2 - Gestion des depenses pour le comptable
Page principale : `/api/depenses/comptable-depenses`

---

## Donnees de reference a inserer avant les tests

```sql
-- =========================
-- CATEGORIE_DEPENSE
-- =========================
INSERT INTO categorie_depense (id, libelle, created_at) VALUES
(1, 'Charges fixes', '2026-07-03 19:13:05.709908'),
(2, 'Matieres premieres', '2026-07-03 19:13:05.709908'),
(3, 'Livraison', '2026-07-03 19:13:05.709908');

-- =========================
-- PHASE
-- =========================
INSERT INTO phase (id, libelle, description, created_at, updated_at) VALUES
(1, 'Aucune / Hors production', 'Depense non liee a une phase de production', '2026-07-03 18:37:19.4635', '2026-07-03 18:37:19.4635'),
(2, 'Phase 1 - Preparation', 'Preparation des matieres premieres et organisation avant production', '2026-07-03 18:37:19.4635', '2026-07-03 18:37:19.4635'),
(3, 'Phase 2 - Broyage/Melange', 'Broyage, melange et transformation des matieres premieres', '2026-07-03 18:37:19.4635', '2026-07-03 18:37:19.4635'),
(4, 'Phase 3 - Conditionnement', 'Conditionnement, emballage et preparation du produit fini', '2026-07-03 18:37:19.4635', '2026-07-03 18:37:19.4635');

-- =========================
-- STATUT_DEPENSE
-- =========================
INSERT INTO statut_depense (id, libelle, created_at) VALUES
(1, 'Regle', '2026-07-03 18:37:19.460693'),
(2, 'En attente', '2026-07-03 18:37:19.460693');
```

---

## T2.1 - Acces au registre depuis le menu comptable

Date : 2026-07-03
Testeur : Manohisoa
Page : `/comptabilite`, sidebar Comptable > Registre des Depenses
Registration : compte comptable
Resultat attendu : Le clic sur "Registre des Depenses" ouvre la page `/api/depenses/comptable-depenses`.
Resultat obtenu : La page "Registre des Depenses" s'affiche avec le header Comptable, les cartes de statistiques et le tableau des operations.
Statut :
☑ Succes
☐ Echec
Commentaire : Le lien est declare dans `layout/comptable.html`.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : `/api/depenses/comptable-depenses`
Registration : compte comptable
Resultat attendu : Les trois categories de depenses inserees en base sont utilisees dans les cartes de statistiques : Charges fixes, Matieres premieres, Livraison.
Resultat obtenu : Les cartes affichent les libelles des categories et les montants totaux correspondants.
Statut :
☑ Succes
☐ Echec
Commentaire : Les totaux sont calcules par `DepenseService.calculerMontantTotalParCategorie`.

---

## T2.2 - Affichage du formulaire de nouvelle depense

Date : 2026-07-03
Testeur : Manohisoa
Page : `/api/depenses/comptable-depenses-nouveau`
Registration : compte comptable
Resultat attendu : Le formulaire "Enregistrer une nouvelle depense" s'affiche.
Resultat obtenu : Les champs Designation, Categorie, Montant, Date, Phase de production, Statut du paiement et Notes complementaires sont visibles.
Statut :
☑ Succes
☐ Echec
Commentaire : La zone Notes complementaires est visuelle uniquement et n'est pas sauvegardee par le DTO actuel.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : `/api/depenses/comptable-depenses-nouveau`
Registration : compte comptable
Resultat attendu : Les listes deroulantes sont chargees depuis les tables `categorie_depense`, `phase` et `statut_depense`.
Resultat obtenu : La categorie propose Charges fixes, Matieres premieres, Livraison ; la phase propose Aucune / Hors production et les phases 1 a 3 ; le statut propose Regle et En attente.
Statut :
☑ Succes
☐ Echec
Commentaire : Les listes sont chargees via `chargerReferentiels(model)`.

---

## T2.3 - Creation d'une depense valide

Date : 2026-07-03
Testeur : Manohisoa
Page : POST `/api/depenses`
Registration : compte comptable
Resultat attendu : Creation d'une depense valide avec date=2026-07-03, designation="Facture Jirama", categorie=Charges fixes, phase=Aucune / Hors production, montant=250000, statut=Regle. Apres validation, l'utilisateur est redirige vers le registre.
Resultat obtenu : Redirection vers `/api/depenses/comptable-depenses`, message de succes "La depense a ete enregistree avec succes.", nouvelle ligne visible dans le tableau.
Statut :
☑ Succes
☐ Echec
Commentaire : La reference affichee suit le format `DEP-{id}`.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : `/api/depenses/comptable-depenses`
Registration : compte comptable
Resultat attendu : La depense creee apparait dans le tableau avec sa date, sa designation, sa categorie, son montant et son statut.
Resultat obtenu : La ligne "Facture Jirama" apparait avec la categorie "Charges fixes", le montant "250000 Ar" et le statut "Regle".
Statut :
☑ Succes
☐ Echec
Commentaire : Les libelles viennent du `DepenseDTO`, pas directement des entites.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : `/api/depenses/comptable-depenses`
Registration : compte comptable
Resultat attendu : Le total general et le total de la categorie Charges fixes augmentent du montant saisi.
Resultat obtenu : Les cartes de statistiques prennent en compte la nouvelle depense.
Statut :
☑ Succes
☐ Echec
Commentaire : Le calcul additionne les montants non nuls de toutes les depenses.

---

## T2.4 - Validation du formulaire

Date : 2026-07-03
Testeur : Manohisoa
Page : POST `/api/depenses`
Registration : compte comptable
Resultat attendu : Soumission sans designation refusee avec le message "La designation est obligatoire".
Resultat obtenu : Le formulaire reste affiche et la depense n'est pas enregistree.
Statut :
☑ Succes
☐ Echec
Commentaire : Validation definie par `@NotBlank` dans `DepenseRequestDTO`.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : POST `/api/depenses`
Registration : compte comptable
Resultat attendu : Soumission sans date refusee avec le message "La date est obligatoire".
Resultat obtenu : Le formulaire reste affiche et la depense n'est pas enregistree.
Statut :
☑ Succes
☐ Echec
Commentaire : Validation definie par `@NotNull` dans `DepenseRequestDTO`.

---

Date : 2026-07-03
Testeur : Manohisoa
Page : POST `/api/depenses`
Registration : compte comptable
Resultat attendu : Soumission avec un montant negatif refusee avec le message "Le montant doit etre positif ou nul".
Resultat obtenu : Le formulaire reste affiche et la depense n'est pas enregistree.
Statut :
☑ Succes
☐ Echec
Commentaire : Le champ HTML possede aussi `min="0"`, et le backend verifie avec `@DecimalMin`.

