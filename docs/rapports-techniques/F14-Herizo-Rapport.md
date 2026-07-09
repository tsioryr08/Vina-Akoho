# RAPPORT TECHNIQUE — F14
## Module : Dashboard Production — Cartes KPI stock & lots (`/production`)

---

## 1. Objectif
Afficher 6 cartes de pilotage pour le Responsable Production :
1. Quantité actuelle des produits finis.
2. Quantité actuelle des matières premières.
3. Nombre de lots produits.
4. Nombre de lots expirant bientôt.
5. Nombre de produits sous le seuil d’alerte.
6. Nombre de matières premières sous le seuil minimum.

Règle métier : les cartes doivent être mises à jour automatiquement après :
- une production
- une vente
- un achat de matières premières

---

## 2. Structure des fichiers (couches)

### 2.1 Controller
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/dashboard/DashboardController.java`
  - Route `GET /production` : calcule les 6 KPIs et les injecte dans le modèle.

### 2.2 Service métier
- `src/main/java/mg/vinaAkoho/vina_akoho/service/produit/ProduitService.java`
  - `listerTous()` / `listerAlertes()` : charge les produits actifs et détermine leur statut d’alerte à partir du stock calculé par lot.
- `src/main/java/mg/vinaAkoho/vina_akoho/service/matierespremieres/MatierePremiereService.java`
  - `lister()` / `listerAlertes()` : charge les matières premières et détecte les niveaux sous seuil minimum.

### 2.3 Repository (accès BDD)
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/produit/LotProduitRepository.java`
  - `sommeQuantiteRestante(Long idProduit)` : somme `quantiteRestante` par produit.
  - `compterLotsProduitsActifs()` : compte les lots produits avec `quantiteRestante > 0`.
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/matierespremieres/LotMpRepository.java`
  - `sommeQuantiteRestante(Integer idMp)` : somme `quantiteRestante` par matière première.
  - `compterLotsExpirantBientot(LocalDate debut, LocalDate fin)` : compte les lots MP dont la `datePeremption` tombe dans la fenêtre glissante de 30 jours.

### 2.4 Templates
- `src/main/resources/templates/dashboard/production/index.html`
  - Grille de 6 cartes KPI avec gestion d’unité, formatage des décimales et sécurité `null`.
- `src/main/resources/templates/dashboard/production/entree-produit.html`
  - Formulaire de production ; redirection automatique vers `/production` après enregistrement.

---

## 3. Fonctions principales (signatures & rôle)

### 3.1 `DashboardController.production(Model model)`

```java
@GetMapping("/production")
public String production(Model model) {
    // ...
    model.addAttribute("quantiteProduitsFinis", ...);
    model.addAttribute("quantiteMatièresPremières", ...);
    model.addAttribute("lotsProduits", lotProduitRepository.compterLotsProduitsActifs());
    model.addAttribute("lotsExpirantBientot", lotMpRepository.compterLotsExpirantBientot(debut, fin));
    model.addAttribute("produitsSousSeuil", ...);
    model.addAttribute("mpSousSeuilMin", ...);
    model.addAttribute("uniteProduits", ...);
    model.addAttribute("uniteMps", ...);
    return "dashboard/production/index";
}
```

Rôle : agréger les stocks et lots, puis fournir les KPIs à la vue.

### 3.2 `ProduitService.listerTous()`

```java
public List<ProduitDTO> listerTous() {
    return produitRepository.findAllActifs().stream().map(this::versDTO).toList();
}
```

Rôle :
- ne récupère que les produits actifs ;
- calcule `quantiteStock` via `lotProduitRepository.sommeQuantiteRestante(id)` ;
- attribue le statut `"SEUIL ATTEINT"` ou `"Stock Correct"` en fonction de `seuilAlerte`.

### 3.3 `MatierePremiereService.lister()`

```java
public List<MatierePremiereListDTO> lister() {
    return matierePremiereRepository.findAll().stream().map(this::versListDTO).toList();
}
```

Rôle :
- récupère toutes les matières premières ;
- calcule `quantiteStock` via `lotMpRepository.sommeQuantiteRestante(id)` ;
- attribue le statut `"SEUIL ATTEINT"` ou `"Stock Correct"` en fonction de `seuilMinimum`.

### 3.4 Repository — comptages

#### a) `LotProduitRepository.compterLotsProduitsActifs()`

```java
@Query("SELECT COUNT(l) FROM LotProduit l WHERE l.quantiteRestante > 0")
long compterLotsProduitsActifs();
```

#### b) `LotMpRepository.compterLotsExpirantBientot(LocalDate debut, LocalDate fin)`

```java
@Query("SELECT COUNT(l) FROM LotMp l WHERE l.quantiteRestante > 0 AND l.datePeremption IS NOT NULL AND l.datePeremption BETWEEN :debut AND :fin")
long compterLotsExpirantBientot(LocalDate debut, LocalDate fin);
```

---

## 4. Logique métier expliquée

### 4.1 KPIs quantitatifs

- **Produits finis** : somme des `quantiteStock` de chaque `ProduitDTO` actif.
- **Matières premières** : somme des `quantiteStock` de chaque `MatierePremiereListDTO`.

### 4.2 KPIs d’alerte

- **Produits sous seuil** : count des produits dont le stock <= `seuilAlerte`.
- **MP sous seuil minimum** : count des matières premières dont le stock <= `seuilMinimum`.

### 4.3 KPIs de lots

- **Lots produits actifs** : count des `LotProduit` avec `quantiteRestante > 0`.
- **Lots expirant bientôt** : count des `LotMp` avec `quantiteRestante > 0` et `datePeremption` dans les 30 jours à venir.

### 4.4 Mise à jour automatique après événements

| Événement | Contrôleur | Redirection |
|---|---|---|
| Production | `EntreeProduitController` (AJAX) | `/production` après 3 s |
| Vente | `VenteController.validerVente()` | `redirect:/production` |
| Achat MP | `MatierePremiereViewController.entreeStock()` | `redirect:/production` |

---

## 5. Templates et rendu (UI)

### 5.1 `dashboard/production/index.html`

- Grille responsive `stats-grid stats-grid-6` :
  - `< 1120 px` : 2 colonnes
  - `1121 px - 1399 px` : 4 colonnes
  - `>= 1400 px` : 6 colonnes
- 6 cartes `stat-card` avec :
  - label, valeur formatée, tendance/badge.
  - fallback `0` si la donnée est `null`.
  - unité dynamique :
    - produits finis : `ProduitDTO.libelleUnite`
    - matières premières : `MatierePremiereListDTO.uniteLibelle`

---

## 6. Tests effectués (référence cahier)

Voir : `docs/cahiers-de-test/F14-Herizo-Test.md`

- Test 1 : 6 cartes KPI visibles et alignées sur `/production`
- Test 2 : Affichage de `0` quand la donnée est manquante
- Test 3 : Redirection automatique après production vers le dashboard
- Test 4 : Redirection automatique après vente vers le dashboard
- Test 5 : Redirection automatique après achat MP vers le dashboard
- Test 6 : Affichage des unités correctes sur les cartes de quantité

---

## 7. Remarques techniques

- Les KPI sont calculés à la volée dans le contrôleur (pas de cache).
- Toutes les valeurs numériques passent par des `COALESCE(..., 0)` côté repository ou sont sécurisées dans le modèle.
- Les unités proviennent des DTO et non des entités, pour respecter la séparation des couches.
- La fenêtre d’expiration est fixée à 30 jours glissants à partir du jour courant.
