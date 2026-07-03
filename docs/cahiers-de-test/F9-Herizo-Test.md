# Cahier de Test — F9
## Module : Historique des achats, créances et solde client (`/clients`)

---

## Test 1 — Affichage de l'historique complet des achats et ventes d'un client

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /clients/{id}` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Afficher tous les achats et ventes du client, avec les articles, la facture et le statut |
| **Résultat obtenu** | Tous les achats et ventes du client sont listées dans 2 tableaux différents |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Le tableau du bas reprend chaque vente avec ses lignes, sa facture et son montant |

---

## Test 2 — Calcul automatique du montant restant à payer

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /clients/{id}` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Calculer automatiquement le solde restant à partir des ventes du client et des montants considérés comme réglés |
| **Résultat obtenu** | Le montant restant à payer s'affiche correctement dans le bloc de synthèse et dans le tableau d'historique |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Dans l'implémentation actuelle, le montant réglé est déduit des ventes validées, car aucune table de paiement séparée n'existe encore |

**Preuve :**
```bash
curl -b cookies.txt http://localhost:8081/clients/1
```

---

## Test 3 — Affichage du solde du client sur sa fiche

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Herizo |
| **Page** | `GET /clients/{id}` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Afficher le solde global du client avec le total des achats et le total réglé |
| **Résultat obtenu** | Le solde client est visible dans la zone de synthèse du profil |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Les totaux sont présentés dans les cartes de synthèse au-dessus de l'historique |

---

## Règles métier vérifiées

```text
Une vente appartient obligatoirement à un client.

Le montant des créances correspond à :

Somme des ventes
-
Somme des paiements

Le solde doit être recalculé automatiquement après chaque paiement.
```

---

## Fonctions testées

| Fonction | Rôle |
|---|---|
| `ClientViewController.detail()` | Charge la fiche client et l'historique des achats |
| `VenteService.obtenirHistoriqueClient()` | Calcule le total des achats, le montant réglé et le solde restant |
| `VenteRepository.findByClientIdOrderByDateVenteDesc()` | Récupère toutes les ventes du client |
| `VenteRepository.sommeAchatsClient()` | Calcule le total des achats du client |
| `VenteRepository.sommeReglementsClient()` | Calcule le montant considéré comme réglé |

---

## Résumé

| Total tests | Succès | Échecs |
|---|---|---|
| 3 | 3 | 0 |

## Remarque

Le rendu de la fiche client permet de consulter l'historique complet des achats directement depuis `/clients/{id}`. Les tests peuvent aussi être vérifiés visuellement depuis l'interface commerciale après connexion.