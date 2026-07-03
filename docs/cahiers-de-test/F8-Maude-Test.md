# Cahier de Test — F8-Maude-Test.md
## Module : Livraison (`/api/livraisons`)

---

## Pré-requis — Authentification

L'API étant protégée par session, chaque test nécessite une connexion préalable pour obtenir le cookie `JSESSIONID`.

```bash
curl -c cookies.txt -X POST http://localhost:8081/api/login \
  -d "email=commercial@vinaakoho.mg&mdp=commercial123"
```

Résultat : `HTTP 302` + `Set-Cookie: JSESSIONID=...` → cookie sauvegardé dans `cookies.txt`, réutilisé dans tous les tests suivants via `-b cookies.txt`.

---

## Test 1 — Liste des livraisons (GET)

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Maude |
| **Page** | `GET /api/livraisons` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Afficher la liste des livraisons avec la vente, le client, le livreur, le statut et la date |
| **Résultat obtenu** | La liste des livraisons s'affiche correctement |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Vérification de l'écran principal du module livraison |

**Preuve :**
```bash
curl -b cookies.txt http://localhost:8081/api/livraisons
```

---

## Test 2 — Création d'une livraison (POST)

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Maude |
| **Page** | `POST /api/livraisons/creer` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Créer une livraison à partir d'une vente existante avec un statut initial |
| **Résultat obtenu** | Livraison créée avec succès, associée à la vente et au statut sélectionnés |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | La création vérifie l'existence de la vente avant insertion |

**Preuve :**
```bash
curl -b cookies.txt -X POST http://localhost:8081/api/livraisons/creer \
  -d "idVente=1&idLivreur=1&idStatutLivraison=1&lieuExact=Avenue Test&contact=0340000002&dateLivraison=2026-07-04&commentaire=Livraison de test"
```

---

## Test 3 — Détail d'une livraison (GET)

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Maude |
| **Page** | `GET /api/livraisons/{id}` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Afficher les informations complètes de la livraison et le formulaire de changement de statut |
| **Résultat obtenu** | Les informations générales, le statut courant et le formulaire de mise à jour s'affichent |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | La fiche détail permet aussi d'accéder à l'historique de statut |

---

## Test 4 — Modification du statut (POST)

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Maude |
| **Page** | `POST /api/livraisons/{id}/statut` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Mettre à jour le statut de la livraison et enregistrer l'historique |
| **Résultat obtenu** | Le statut est mis à jour et une entrée est ajoutée dans `historique_statut_livraison` |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Le changement de statut est bien tracé |

**Preuve :**
```bash
curl -b cookies.txt -X POST http://localhost:8081/api/livraisons/1/statut \
  -d "nouveauStatut=En cours"
```

---

## Test 5 — Historique des livraisons (GET)

| Champ | Détail |
|---|---|
| **Date** | 03/07/2026 |
| **Testeur** | Maude |
| **Page** | `GET /api/livraisons/historique` |
| **Registration** | `commercial@vinaakoho.mg` / `commercial123` |
| **Résultat attendu** | Afficher les changements de statut avec ancien statut, nouveau statut et date |
| **Résultat obtenu** | L'historique des changements s'affiche correctement |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | La page historique reflète les données de `historique_statut_livraison` |

---

## Fonctions testées

| Fonction | Rôle |
|---|---|
| `LivraisonService.creer()` | Crée une livraison liée à une vente et enregistre le statut initial |
| `LivraisonService.modifierStatut()` | Met à jour le statut d'une livraison et trace l'historique |
| `LivraisonService.listerToutes()` | Récupère la liste des livraisons pour l'affichage |
| `LivraisonService.listerHistorique()` | Récupère l'historique global des statuts |
| `LivraisonService.listerHistoriquePourLivraison()` | Récupère l'historique d'une livraison donnée |

---

## Résumé

| Total tests | Succès | Échecs |
|---|---|---|
| 5 | 3 | 2 |