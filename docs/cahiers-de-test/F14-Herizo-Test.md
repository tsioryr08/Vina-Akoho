# Cahier de Test — F14
## Module : Dashboard Production — Cartes KPI stock & lots (`/production`)

---

## Test 1 — Affichage des 6 cartes KPI sur le dashboard production

| Champ | Détail |
|---|---|
| **Date** | 07/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /production` |
| **Registration** | RO02 - Responsable de production |
| **Résultat attendu** | Afficher 6 cartes : quantité produits finis, quantité matières premières, lots produits, lots expirant bientôt, produits sous seuil, MP sous seuil minimum |
| **Résultat obtenu** | Les 6 cartes sont présentes et alignées en ligne sur grand écran |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Grille `stats-grid-6` fonctionne en responsive : 2/4/6 colonnes selon la largeur |

---

## Test 2 — Affichage de 0 quand la donnée est manquante

| Champ | Détail |
|---|---|
| **Date** | 07/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /production` |
| **Registration** | RO02 - Responsable de production |
| **Résultat attendu** | Si un KPI est `null`, la carte affiche `0` au lieu d’une erreur ou d’une case vide |
| **Résultat obtenu** | Toutes les cartes affichent `0` quand la donnée est absente |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Sécurisé côté contrôleur ET côté vue (`th:xxx != null ? ... : 0`) |

**Preuve :**
```bash
curl -b cookies.txt http://localhost:8081/production
```

---

## Test 3 — Redirection automatique après production

| Champ | Détail |
|---|---|
| **Date** | 08/07/2026 |
| **Testeur** | Herizo |
| **Page** | `POST /api/entrees-produit` depuis `/production/entree-produit` |
| **Registration** | RO02 - Responsable de production |
| **Résultat attendu** | Après validation de la production, redirection automatique vers `/production` après 3 secondes |
| **Résultat obtenu** | Redirection automatique vers le dashboard avec carte mise à jour |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Timer `setTimeout` côté JS sur `afficherResultat()` |

---

## Test 4 — Redirection automatique après vente

| Champ | Détail |
|---|---|
| **Date** | 08/07/2026 |
| **Testeur** | Herizo |
| **Page** | `POST /ventes/valider` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Après validation de la vente, le commercial est redirigé vers `/production` pour voir les stocks mis à jour |
| **Résultat obtenu** | Redirect vers `/production` confirmé |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | `VenteController.validerVente()` retourne `redirect:/production` |

---

## Test 5 — Redirection automatique après achat MP

| Champ | Détail |
|---|---|
| **Date** | 09/07/2026 |
| **Testeur** | Herizo |
| **Page** | `POST /matieres-premieres/entree-stock` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Après enregistrement d’une entrée de stock, redirection vers `/production` |
| **Résultat obtenu** | Redirect vers `/production` en succès comme en erreur |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | `MatierePremiereViewController.entreeStock()` retourne `redirect:/production` |

---

## Test 6 — Affichage des unités sur les cartes de quantité

| Champ | Détail |
|---|---|
| **Date** | 09/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /production` |
| **Registration** | RO02 - Responsable de production |
| **Résultat attendu** | Les cartes “Quantité actuelle produits finis” et “Quantité actuelle matières premières” affichent l’unité réelle (ex : `kg`) et non un générique “Unités en stock” |
| **Résultat obtenu** | L’unité provient de `ProduitDTO.libelleUnite` et `MatierePremiereListDTO.uniteLibelle` ; fallback sur “Unité” si absent |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Unités dynamiques passées depuis le contrôleur via `uniteProduits` et `uniteMps` |

---

## Règles métier vérifiées

```text
Les cartes du dashboard production doivent montrer :

1. Quantité actuelle des produits finis.
2. Quantité actuelle des matières premières.
3. Nombre de lots produits.
4. Nombre de lots expirant bientôt.
5. Nombre de produits sous le seuil d'alerte.
6. Nombre de matières premières sous le seuil minimum.

Elles doivent être mises à jour automatiquement après :
- une production
- une vente
- un achat de MP
```

---

## Fonctions testées

| Fonction | Rôle |
|---|---|
| `DashboardController.production()` | Calcule et expose les 6 KPIs |
| `ProduitService.listerTous()` | Liste les produits actifs avec stock calculé |
| `ProduitService.listerAlertes()` | Liste les produits sous seuil d’alerte |
| `MatierePremiereService.lister()` | Liste les MP avec stock calculé |
| `MatierePremiereService.listerAlertes()` | Liste les MP sous seuil minimum |
| `LotProduitRepository.compterLotsProduitsActifs()` | Compte les lots produits actifs |
| `LotMpRepository.compterLotsExpirantBientot()` | Compte les lots MP expirant sous 30 jours |

---

## Résumé

| Total tests | Succès | Échecs |
|---|---|---|
| 6 | 6 | 0 |

## Remarque

Les redirections vers `/production` peuvent être vérifiées visuellement depuis le navigateur après chaque action (production, vente, entrée de stock MP).
