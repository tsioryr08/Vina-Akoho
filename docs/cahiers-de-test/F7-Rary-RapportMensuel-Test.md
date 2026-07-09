# CAHIER DE TESTS — RAPPORT FINANCIER MENSUEL (Sprint 3)

Testeur : Rary
Module : F7 — Rapport Financier Mensuel (Sprint 3, extension du module Bénéfices existant)
Page : `GET /finances/rapport-mensuel`
Registration : `admin@vinaakoho.mg` / `admin123`

Ce module répond au besoin Sprint 3 « Responsable Financier » : afficher Dépenses du mois, Recettes du mois, Bénéfice et Evolution mensuelle. La page est accessible depuis la sidebar Finance (lien "Rapports financiers") de toutes les pages comptable, ou directement par URL.

---

## 0. GUIDE DE TEST MANUEL (procédure pas à pas)

Pour rejouer les 4 vérifications ci-dessous depuis un navigateur, sans outil technique :

1. **Se connecter** : ouvrir l'application, se connecter avec `admin@vinaakoho.mg` / `admin123`.
2. **Accéder à la page** : cliquer sur "Rapports financiers" (code `F5`) dans la section "Finance" de la sidebar — ou aller directement sur `/finances/rapport-mensuel`.
3. **Dépenses du mois** : vérifier la carte « Dépenses — [mois courant] » (fond/texte rouge) et comparer le montant affiché à la somme des dépenses au statut "Payé" du mois en cours dans le registre des Dépenses (`F2`).
4. **Recettes du mois** : vérifier la carte « Recettes — [mois courant] » (vert) et comparer au total des ventes du mois en cours (page Historique des ventes).
5. **Bénéfice** : vérifier que la carte « Bénéfice » = Recettes − Dépenses affichées juste au-dessus (calcul visible à l'œil).
6. **Evolution mensuelle** : vérifier que le tableau sous les 3 cartes affiche une ligne par mois, du plus ancien au plus récent, mois courant inclus.
   - Par défaut : 6 lignes (6 derniers mois).
   - Ajouter `?nombreMois=3` à l'URL → le tableau doit se réduire à 3 lignes.
   - Ajouter `?nombreMois=1` à l'URL → une seule ligne (le mois courant), qui doit correspondre exactement aux 3 cartes KPI.
7. **Sans être connecté** : se déconnecter puis retenter `/finances/rapport-mensuel` → doit être refusé (redirection login ou erreur d'authentification), jamais afficher les données.

Les résultats détaillés de cette procédure (dates, valeurs obtenues) sont consignés dans les sections 1 à 3 ci-dessous.

---

## 1. ACCÈS ET SÉCURITÉ

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel` (sans session)
Résultat attendu : Accès refusé, HTTP 401.
Résultat obtenu : `{"success": false, "message": "Authentification requise. Session expirée ou inexistante.", "data": null}` — HTTP 401.
Statut :
☑ Succès
☐ Échec
Commentaire : Le filtre `SessionFilter` protège bien la nouvelle route comme le reste de l'application.

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel` (après login admin)
Résultat attendu : HTTP 200, page rendue avec les 3 cartes KPI et le tableau d'évolution.
Résultat obtenu : HTTP 200, page complète reçue (sidebar, cartes stats, tableau).
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

## 2. EXACTITUDE DES CALCULS (mois courant = Juillet 2026)

*Données réelles de la base au moment du test (aucun jeu de données de test injecté spécifiquement pour ce module).*

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel`
Résultat attendu : Recettes du mois = somme de `vente.montant_total` sur juillet 2026.
Résultat obtenu : `+ 979 000.00 Ar` affiché en vert dans la carte « Recettes — Juillet 2026 ».
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel`
Résultat attendu : Dépenses du mois = somme de `depense.montant` au statut « Payé » sur juillet 2026.
Résultat obtenu : `- 0.00 Ar` affiché en rouge — aucune dépense au statut « Payé » enregistrée sur juillet 2026 en base.
Statut :
☑ Succès
☐ Échec
Commentaire : Comportement cohérent avec la règle du module Bénéfice existant (F7-Nekena) : seules les dépenses « Payé » comptent. À revérifier avec un jeu de données de dépenses de test si le résultat 0 Ar surprend en démonstration.

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel`
Résultat attendu : Bénéfice du mois = Recettes − Dépenses = 979 000 − 0.
Résultat obtenu : `979 000.00 Ar`, affiché en vert (positif).
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

## 3. EVOLUTION MENSUELLE ET PARAMÈTRE `nombreMois`

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel` (sans paramètre, défaut)
Résultat attendu : Tableau affiche 6 lignes, de Février 2026 à Juillet 2026 (mois courant inclus), triées du plus ancien au plus récent.
Résultat obtenu : 6 lignes exactement dans cet ordre : Février, Mars, Avril, Mai, Juin, Juillet 2026.
Statut :
☑ Succès
☐ Échec
Commentaire : Les 5 mois antérieurs à juillet affichent tous 0,00 Ar (recettes et dépenses) car la base ne contient pas de mouvements sur cette période.

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel?nombreMois=3`
Résultat attendu : Tableau réduit à 3 lignes (Mai, Juin, Juillet 2026).
Résultat obtenu : 3 lignes reçues : Mai 2026, Juin 2026, Juillet 2026.
Statut :
☑ Succès
☐ Échec
Commentaire : —

---

Date : 2026-07-07
Testeur : Rary
Page : `GET /finances/rapport-mensuel?nombreMois=1`
Résultat attendu : Tableau réduit à 1 ligne (mois courant uniquement).
Résultat obtenu : 1 ligne reçue : Juillet 2026.
Statut :
☑ Succès
☐ Échec
Commentaire : Confirme que le mois courant est toujours le dernier élément de la liste, utilisé pour les 3 cartes KPI.

---

## Bilan global

| Catégorie | Tests rédigés | Succès | Échecs |
| --- | --- | --- | --- |
| Accès et sécurité | 2 | 2 | 0 |
| Exactitude des calculs | 3 | 3 | 0 |
| Evolution mensuelle / paramètre `nombreMois` | 3 | 3 | 0 |
| **Total** | **8** | **8** | **0** |

## Bugs identifiés
Aucun.

## Points à surveiller
- Les dépenses du mois affichent 0 Ar faute de dépenses « Payé » sur juillet 2026 en base réelle. Si un jeu de données de démonstration est nécessaire, s'inspirer du script SQL en annexe de `F7-Nekena-Benefice-Test.md`.
