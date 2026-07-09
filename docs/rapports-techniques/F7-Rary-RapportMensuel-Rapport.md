# RAPPORT TECHNIQUE - MODULE RAPPORT FINANCIER MENSUEL (Sprint 3)

**Version:** 2.1
**Responsable :** Rary (Responsable Financier — Sprint 3)
**Périmètre :** répond exactement aux 4 tâches assignées à Rary dans `Sprint3.md` (section "💰 Responsable Financier") : *Dépenses du mois, Recettes du mois, Bénéfice, Evolution mensuelle*. Aucune fonctionnalité hors de ce périmètre n'est incluse (voir `Sprint3_Rary.md` pour l'historique des ajustements de scope).

**Remarque** :
- Complète le module Bénéfices existant (F7 — voir `F7-Nekena-Benefice-Rapport.md`), sans le modifier : le nouveau rapport réutilise les mêmes requêtes (`VenteRepository`/`DepenseRepository`), sans en changer le comportement.
- Tout le code est isolé dans son propre package/dossier `finance` (`controller.finance`, `service.finance`, `dto.finance`, `templates/finance`) pour ne pas toucher aux fichiers du module Bénéfices existant (`controller.benefice`, propriété de Nekena) — évite les conflits si plusieurs personnes travaillent sur le module Finance en parallèle.
- URL : `/finances/rapport-mensuel` (hors préfixe `/api`, cohérent avec les autres pages Thymeleaf du site), reliée à la sidebar Finance (lien "Rapports financiers").

---

## 1. STRUCTURE DES FICHIERS

### 1.1 DTOs (dto/finance)

```
├── EvolutionMensuelleDTO.java       # Un mois : recettes, dépenses, bénéfice
├── RapportFinancierMensuelDTO.java  # Mois courant + liste EvolutionMensuelleDTO
```

### 1.2 SERVICES (service/finance)

```
├── RapportFinancierService.java     # calculerRapportFinancierMensuel(int nombreMois)
```

### 1.3 CONTROLLERS (controller/finance/)

```
├── RapportFinancierController.java  # GET /finances/rapport-mensuel
```

### 1.4 TEMPLATES (templates/finance/)

```
└── rapport-mensuel.html             # Cartes KPI + tableau d'évolution mensuelle, reliée à la sidebar Finance
```

---

## 2. FONCTIONS PRINCIPALES

### 2.1 SERVICE

```java
// Calcule Dépenses/Recettes/Bénéfice du mois courant + l'évolution sur les
// `nombreMois` derniers mois (mois courant inclus).
public RapportFinancierMensuelDTO calculerRapportFinancierMensuel(int nombreMois)
```

Logique interne, pour chaque mois de la fenêtre (du plus ancien au plus récent) :
1. Bornes du mois : `YearMonth.atDay(1)` → `YearMonth.atEndOfMonth()`.
2. Recettes = `venteRepository.sumRecettesEntreDeuxDates(...)` (même requête que le module Bénéfice existant).
3. Dépenses = `depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(..., categorieId = null)` → toutes catégories confondues, dépenses au statut « Payé » (id=1), même requête que le module existant.
4. Bénéfice = Recettes − Dépenses.
5. Les valeurs « du mois » exposées par le DTO sont celles du dernier élément de la liste (mois courant).

### 2.2 CONTROLLER

```java
// GET /finances/rapport-mensuel?nombreMois=6 (défaut 6)
public String afficherRapportMensuel(int nombreMois, Model model)
```

---

## 3. LOGIQUE MÉTIER — RÈGLES DE CALCUL

Identiques à celles déjà en place dans le module Bénéfice (F7 — Nekena), appliquées mois par mois :

* **Recettes du mois** : somme de `vente.montant_total` pour les ventes dont `date_vente` est dans le mois (aucun filtre de statut dans la requête réutilisée — comportement identique à l'existant `sumRecettesEntreDeuxDates`).
* **Dépenses du mois** : somme de `depense.montant` pour les dépenses dont `date` est dans le mois **et** `statut_depense.id = 1` (« Payé »), toutes catégories.
* **Bénéfice du mois** : `Recettes du mois − Dépenses du mois`.
* **Evolution mensuelle** : liste chronologique (du plus ancien au plus récent) des 3 valeurs ci-dessus pour chacun des `nombreMois` derniers mois, mois courant inclus.

---

## 4. ENDPOINTS API

| Méthode | Endpoint | Paramètres | Description |
| --- | --- | --- | --- |
| GET | `/finances/rapport-mensuel` | `nombreMois` (optionnel, défaut 6) | Affiche Dépenses/Recettes/Bénéfice du mois courant + tableau d'évolution mensuelle |

**Accès :** nécessite une session active (login via `/api/login`). Exposé dans la sidebar Finance (lien "Rapports financiers") de toutes les pages comptable.

---

## 5. VÉRIFICATION MANUELLE EFFECTUÉE

Compilation (`mvn compile`) : succès.
Application relancée (`mvn spring-boot:run`), connexion via `admin@vinaakoho.mg` / `admin123`, puis appel de `GET /finances/rapport-mensuel` : réponse HTTP 200, page rendue avec les 3 cartes KPI et le tableau d'évolution sur 6 mois (Février → Juillet 2026). Détail des résultats dans `F7-Rary-RapportMensuel-Test.md`.
