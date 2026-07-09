# Cahier de test - F4 Export des ventes, produits vendus et facture

Testeur : Manohisoa
Date : 2026-07-08
Module : F4 - Export ventes pour le responsable commercial
Pages principales :
- `/ventes`
- `/ventes/historique`
- `/ventes/{id}`

---

## Donnees de reference avant les tests

Preconditions :

- disposer d'au moins une vente realisee avec statut different de `En attente de paiement` et `Annulee` ;
- disposer d'au moins une vente avec plusieurs lignes produits ;
- disposer d'au moins une vente avec facture generee ;
- disposer d'au moins une vente en attente ou annulee pour verifier l'exclusion des statistiques realisees ;
- ouvrir la session avec un profil autorise a consulter les pages du responsable commercial.

Exemple de donnees fonctionnelles attendues :

| Vente | Client | Statut | Produits | Facture |
| ----- | ------ | ------ | -------- | ------- |
| Vente 1 | Client test A | Validee | Produit A, Produit B | Oui |
| Vente 2 | Client test B | En attente de paiement | Produit A | Oui ou non |
| Vente 3 | Client test C | Annulee | Produit C | Oui ou non |

---

## T4.1 - Export Excel de la liste des ventes

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes`
Action : Cliquer sur `Exporter Excel`.
Resultat attendu : Le navigateur telecharge un fichier nomme `ventes_liste_2026-07-08.xlsx`.
Resultat obtenu : Le fichier Excel est telecharge.
Statut :
☑ Succes
☐ Echec
Commentaire : Le bouton appelle `/ventes/liste/export/excel`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/liste/export/excel`
Action : Ouvrir le fichier Excel telecharge.
Resultat attendu : Le fichier contient les colonnes `ID`, `Client`, `Date`, `Produit(s)`, `Mode paiement`, `Total (Ar)` et `Statut`.
Resultat obtenu : Les colonnes sont presentes dans la premiere ligne du fichier.
Statut :
☑ Succes
☐ Echec
Commentaire : Les donnees sont construites avec `VenteListeExportDTO`.

---

## T4.2 - Export PDF de la liste des ventes

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes`
Action : Cliquer sur `Export PDF`.
Resultat attendu : Le navigateur telecharge un fichier nomme `ventes_liste_2026-07-08.pdf`.
Resultat obtenu : Le fichier PDF est telecharge.
Statut :
☑ Succes
☐ Echec
Commentaire : Le bouton appelle `/ventes/liste/export/pdf`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/liste/export/pdf`
Action : Ouvrir le PDF telecharge.
Resultat attendu : Le document affiche le titre `Liste des ventes` et un tableau avec les ventes.
Resultat obtenu : Le titre et le tableau sont visibles.
Statut :
☑ Succes
☐ Echec
Commentaire : Le PDF est genere par `ExportVenteService.exporterVentesListePdf`.

---

## T4.3 - Conservation des filtres dans l'export de liste

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes`
Action : Saisir une recherche client ou produit, puis cliquer sur `Exporter Excel`.
Resultat attendu : Le fichier Excel contient seulement les ventes correspondant a la recherche.
Resultat obtenu : Les lignes exportees respectent le filtre applique dans la page.
Statut :
☑ Succes
☐ Echec
Commentaire : Les parametres de formulaire sont ajoutes a l'URL par `getFilters()`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes`
Action : Appliquer un filtre de date ou de statut, puis cliquer sur `Export PDF`.
Resultat attendu : Le fichier PDF contient seulement les ventes correspondant aux filtres.
Resultat obtenu : Les lignes exportees respectent les filtres.
Statut :
☑ Succes
☐ Echec
Commentaire : Les routes d'export reutilisent la logique de recherche, filtre et tri.

---

## T4.4 - Export Excel des produits les plus vendus

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/historique`
Action : Cliquer sur `Export Excel`.
Resultat attendu : Le navigateur telecharge un fichier nomme `produits_vendus_2026-07-08.xlsx`.
Resultat obtenu : Le fichier Excel est telecharge.
Statut :
☑ Succes
☐ Echec
Commentaire : Le bouton appelle `/ventes/historique/export/excel`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/historique/export/excel`
Action : Ouvrir le fichier Excel telecharge.
Resultat attendu : Le fichier contient les colonnes `Produit`, `Quantite`, `Chiffre d'affaires (Ar)` et `Part du CA`.
Resultat obtenu : Les colonnes sont presentes et les produits sont tries par chiffre d'affaires decroissant.
Statut :
☑ Succes
☐ Echec
Commentaire : Le calcul regroupe les lignes de vente par nom de produit.

---

## T4.5 - Export PDF des produits les plus vendus

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/historique`
Action : Cliquer sur `Export PDF`.
Resultat attendu : Le navigateur telecharge un fichier nomme `produits_vendus_2026-07-08.pdf`.
Resultat obtenu : Le fichier PDF est telecharge.
Statut :
☑ Succes
☐ Echec
Commentaire : Le bouton appelle `/ventes/historique/export/pdf`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/historique/export/pdf`
Action : Ouvrir le PDF telecharge.
Resultat attendu : Le document affiche le titre `Produits les plus vendus` et le tableau des produits.
Resultat obtenu : Le titre et le tableau sont visibles.
Statut :
☑ Succes
☐ Echec
Commentaire : Les parts de chiffre d'affaires sont calculees cote backend avec un arrondi explicite.

---

## T4.6 - Exclusion des ventes non realisees

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/historique`
Action : Exporter les produits vendus avec une base contenant une vente en attente et une vente annulee.
Resultat attendu : Les produits des ventes `En attente de paiement` et `Annulee` ne sont pas additionnes dans les exports de produits vendus.
Resultat obtenu : Seules les ventes realisees sont prises en compte.
Statut :
☑ Succes
☐ Echec
Commentaire : La methode `estVenteRealisee` exclut ces deux statuts.

---

## T4.7 - Export PDF d'une facture

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/{id}`
Action : Cliquer sur le bouton `PDF Facture`.
Resultat attendu : Le navigateur telecharge un fichier nomme `facture_vente_{id}.pdf`.
Resultat obtenu : Le fichier PDF est telecharge.
Statut :
☑ Succes
☐ Echec
Commentaire : Le lien pointe vers `/ventes/{id}/facture/pdf`.

---

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes/{id}/facture/pdf`
Action : Ouvrir le PDF de facture.
Resultat attendu : Le document affiche les informations de vente, le numero de facture, la date d'emission, les articles vendus et le total TTC.
Resultat obtenu : Toutes les informations principales de la facture sont visibles.
Statut :
☑ Succes
☐ Echec
Commentaire : Le PDF est genere par `ExportVenteService.exporterFactureVentePdf`.

---

## T4.8 - Verification des types de fichiers

Date : 2026-07-08
Testeur : Manohisoa
Page : routes d'export
Action : Telecharger un export Excel et un export PDF.
Resultat attendu : Les fichiers Excel s'ouvrent avec un tableur et les fichiers PDF s'ouvrent avec un lecteur PDF.
Resultat obtenu : Les formats sont reconnus correctement.
Statut :
☑ Succes
☐ Echec
Commentaire : Les types MIME envoyes sont `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` et `application/pdf`.

---

## T4.9 - Cas sans donnees

Date : 2026-07-08
Testeur : Manohisoa
Page : `/ventes` ou `/ventes/historique`
Action : Appliquer des filtres ne retournant aucune vente, puis exporter.
Resultat attendu : Le fichier est genere avec les en-tetes, sans erreur serveur.
Resultat obtenu : Le fichier vide est genere correctement.
Statut :
☑ Succes
☐ Echec
Commentaire : Les services d'export acceptent une liste vide.
