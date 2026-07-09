# Incohérences — Relève unifiée

## ADMIN — `http://localhost:8081/admin`

| #   | Incohérence                                                                                                          | Fichier / Ligne                                        | Constat                       |
| --- | -------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------ | ----------------------------- |
| 1   | **Finances → Rapports & Trésorerie** pointe vers le rôle Comptable (`/comptabilite`) au lieu de rester dans l'Admin. | `layout/admin.html:26`                                 | `th:href="@{/comptabilite}"`  |
| 2   | **Statistiques → Vue globale & Analytics** a exactement le même comportement/cible que le Dashboard Principal.       | `layout/admin.html:31-32` vs `layout/admin.html:16-17` | Les deux mènent à `@{/admin}` |
| 3   | **Statistiques → Production** sort de la responsabilité Admin et pointe vers le Responsable Production.              | `layout/admin.html:34`                                 | `th:href="@{/production}"`    |
| 4   | **Statistiques → Ventes** sort de la responsabilité Admin et pointe vers le Responsable Commercial.                  | `layout/admin.html:37`                                 | `th:href="@{/ventes}"`        |
| 5   | **Statistiques → Géographie** sort de la responsabilité Admin et pointe vers le Responsable Commercial.              | `layout/admin.html:40`                                 | `th:href="@{/commercial}"`    |
| 6   | **Configuration → Paramètres système** a exactement le même comportement/cible que le Dashboard Principal.           | `layout/admin.html:45` vs `layout/admin.html:16-17`    | Les deux mènent à `@{/admin}` |
| 7   | **Prévisions → Prévisions** sort de la responsabilité Admin et pointe vers le Responsable Production.                | `layout/admin.html:50`                                 | `th:href="@{/production}"`    |

---

## GESTIONNAIRE DE STOCK — `http://localhost:8081/stock`

| #   | Incohérence                                                                              | Fichier / Ligne                                   | Constat                                                                                                                                                                                                 |
| --- | ---------------------------------------------------------------------------------------- | ------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **Tous les liens du sidebar ne fonctionnent pas** ; ils sont en `href="#"`.              | `dashboard/stock/index.html:32,37,40,45,48,53,56` | Confirmé                                                                                                                                                                                                |
| 2   | **Rôle inutile** — le rôle « Gestionnaire de Stock » (RO03) doit être supprimé du login. | `manou.txt`                                       | Présent dans les templates (`dashboard/stock/index.html`, `layout/gestionnaireStock.html`) ; pas de trace de suppression dans le code Java (`RO03` absent des annotations `@PreAuthorize` / `hasRole`). |

---

## RESPONSABLE PRODUCTION — `http://localhost:8081/production`

| #   | Incohérence                                                                                                                                                                                 | Fichier / Ligne                                                                     | Constat                                                       |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| 1   | **Boutons du dashboard non fonctionnels** : « Générer plan d'achat », « Simuler rentabilité », « Ajuster simulation ».                                                                      | `dashboard/production/index.html:84,85,110`                                         | `href="#"`                                                    |
| 2   | **Matières Premières → Ajouter Matières** ne mène nulle part (`#`).                                                                                                                         | `layout/responsableProduction.html:21`                                              | `href="#"`                                                    |
| 3   | **Matières Premières → Suggestions** ne mène nulle part (`#`).                                                                                                                              | `layout/responsableProduction.html:30`                                              | `href="#"`                                                    |
| 4   | **Matières Premières → Prévisions** ne mène nulle part (`#`).                                                                                                                               | `layout/responsableProduction.html:33`                                              | `href="#"`                                                    |
| 5   | **Stocks → Suivi Produits Finis** ne mène nulle part (`#`).                                                                                                                                 | `layout/responsableProduction.html:41`                                              | `href="#"`                                                    |
| 6   | **Surveillance → Inventaire Périodique** ne mène nulle part (`#`).                                                                                                                          | `layout/responsableProduction.html:52`                                              | `href="#"`                                                    |
| 7   | **Redondance sidebar** — « Entrée matière première en stock » (Surveillance) devrait être déplacé/regroupé sous « Matières Premières → Ajouter Matières », car ils couvrent la même action. | `layout/responsableProduction.html:21` vs `layout/responsableProduction.html:49-50` | `href="#"` vs `th:href="@{/matieres-premieres/entree-stock}"` |

---

## RESPONSABLE COMMERCIAL — `http://localhost:8081/commercial`

| #   | Incohérence                                                                                                                       | Fichier / Ligne                                           | Constat    |
| --- | --------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------- | ---------- |
| 1   | **Filtres rechargent la page** (formulaires `GET` classiques avec `<button type="submit">`) au lieu de filtrer sans rechargement. | `ventes/responsable-commercial-ventes.html:58-66, 72-115` | Confirmé   |
| 2   | **Livraisons → Zones à forte demande** ne marche pas (`#`).                                                                       | `layout/responsableCommercial.html:46`                    | `href="#"` |
| 3   | **Livraisons → Liste livraisons** ne marche pas (`#`).                                                                            | `layout/responsableCommercial.html:49`                    | `href="#"` |
| 4   | **Livraisons → Nouvelle livraison** ne marche pas (`#`).                                                                          | `layout/responsableCommercial.html:52`                    | `href="#"` |

> **Note** : L'export Excel est implémenté côté code (`VenteController.java` endpoints `/ventes/liste/export/excel` et `/ventes/historique/export/excel` présents) ; l'incohérence « export Excel ne marche pas encore » n'est donc **plus présente** au niveau du code.

---

## RESPONSABLE ACHAT — `http://localhost:8081/achats`

| #   | Incohérence                                                                                                                                                                          | Fichier / Ligne                                       | Constat  |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------- | -------- |
| 1   | **Tous les liens du sidebar ne fonctionnent pas** ; ils sont en `href="#"`.                                                                                                          | `dashboard/achats/index.html:32,37,42,45,50,53,54,55` | Confirmé |
| 2   | **Contenu du dashboard en dur** — `dashboard/achats/index.html` utilise des données statiques/hardcodées (commandes CMD-MP-098/099, chiffres figés) sans modèle Thymeleaf dynamique. | `dashboard/achats/index.html`                         | Confirmé |

> **Note** : Le layout `layout/responsableAchat.html` utilise des chemins relatifs vers des templates qui n'existent pas (`responsable-achat-dashboard.html`, etc.), mais la route `/achats` pointe vers `dashboard/achats/index.html`. Les chemins morts dans `responsableAchat.html` ne sont donc pas la cause visible du problème, mais constituent une incohérence de maintenance.

---

## COMPTABLE — `http://localhost:8081/comptabilite`

| #   | Incohérence                                                                                                                                                                                                                      | Fichier / Ligne                                 | Constat                                                                                                                                                        |
| --- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **Tous les boutons du dashboard sont inopérants** (`href="#"`).                                                                                                                                                                  | `dashboard/comptabilite/index.html:50,51,72,73` | Confirmé                                                                                                                                                       |
| 2   | **Sidebar pointe vers du HTML dur** — les cibles `comptable-finances.html`, `comptable-cout-de-revient.html`, `comptable-rapports-financiers.html`, `clients.html` n'existent pas comme vues Thymeleaf gérées par un contrôleur. | `layout/comptable.html:21,27,30,33`             | Confirmé (chemins relatifs morts)                                                                                                                              |
| 3   | **Registre des Dépenses inaccessible** — l'appel à `/api/depenses/comptable-depenses` retourne une erreur JSON (`success: false, message: "Une erreur interne est survenue"`).                                                   | `manou.txt`                                     | Non vérifiable sans exécution, mais le contrôleur `DepenseController.java` retourne bien la vue `depense/comptable-depenses` ; le problème reste donc signalé. |

---

## GLOBAL

| #   | Incohérence                                                                                                                  | Source        | Constat                                                                                                                                         |
| --- | ---------------------------------------------------------------------------------------------------------------------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **Manque d'homogénéité CSS / styles** — il faut revoir la cohérence et l'uniformité des styles utilisés dans tout le projet. | `refactor.md` | Confirmé (styles inline, classes incohérentes entre sidebars, absence de design system visible).                                                |
| 2   | **Sidebars inutiles ou à supprimer** — plusieurs sidebars n'ont pas de contenu ou doivent être fusionnées/supprimées.        | `refactor.md` | Confirmé (ex. `layout/gestionnaireStock.html` et `layout/responsableAchat.html` non utilisés par les routes principales, sidebars redondantes). |

---

\_
