# Cahier de Test — F3-Tsiory-Test.md
## Module : Recette Produit (`/api/recettes`)

---

## Pré-requis — Authentification

L'API étant protégée par session, chaque test nécessite une connexion préalable pour obtenir le cookie `JSESSIONID`.

```bash
curl -c cookies.txt -X POST http://localhost:8081/api/login \
  -d "email=admin@vinaakoho.mg&mdp=admin123"
```

Résultat : `HTTP 302` + `Set-Cookie: JSESSIONID=...` → cookie sauvegardé dans `cookies.txt`, réutilisé dans tous les tests suivants via `-b cookies.txt`.

---

## Test 1 — Création d'une recette (POST)

| Champ | Détail |
|---|---|
| **Date** | 30/06/2026 |
| **Testeur** | Tsiory |
| **Page** | `POST /api/recettes` |
| **Registration** | `admin@vinaakoho.mg` / `admin123` |
| **Résultat attendu** | Créer une recette pour la catégorie *Poussin* (id=1) avec 2 lignes de matières premières |
| **Résultat obtenu** | Recette créée avec succès, 2 lignes insérées en base avec `is_active = true` |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | — |

**Preuve :**
```bash
curl -b cookies.txt -X POST http://localhost:8081/api/recettes \
  -H "Content-Type: application/json" \
  -d '{"idCategorie":1,"idEmployeCreation":1,"lignes":[
        {"idMp":1,"quantiteMp":60,"idUnite":1},
        {"idMp":2,"quantiteMp":25,"idUnite":1}
      ]}'
```
```json
{"success":true,"message":"Recette créée avec succès",
 "data":[
   {"id":3,"idCategorie":1,"idMp":1,"nomMp":"Maïs jaune","quantiteMp":60,"isActive":true,"version":1},
   {"id":4,"idCategorie":1,"idMp":2,"nomMp":"Son de riz","quantiteMp":25,"isActive":true,"version":1}
 ]}
```

**Vérification en base :**
```
 id | id_categorie | version | id_mp | quantite_mp | is_active
----+--------------+---------+-------+-------------+-----------
  3 |            1 |       1 |     1 |       60.00 | t
  4 |            1 |       1 |     2 |       25.00 | t
```

---

## Test 2 — Lecture de la recette active (GET)

| Champ | Détail |
|---|---|
| **Date** | 30/06/2026 |
| **Testeur** | Tsiory |
| **Page** | `GET /api/recettes/categorie/{id}` |
| **Registration** | `admin@vinaakoho.mg` / `admin123` |
| **Résultat attendu** | Récupérer toutes les lignes actives de la recette de la catégorie 1 |
| **Résultat obtenu** | Les 2 lignes attendues sont retournées avec les bonnes données |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | — |

**Preuve :**
```bash
curl -b cookies.txt http://localhost:8081/api/recettes/categorie/1
```
```json
{"success":true,"message":"Recette trouvée",
 "data":[
   {"id":3,"idMp":1,"nomMp":"Maïs jaune","quantiteMp":60.00,"isActive":true},
   {"id":4,"idMp":2,"nomMp":"Son de riz","quantiteMp":25.00,"isActive":true}
 ]}
```

---

## Fonctions testées

| Fonction | Rôle |
|---|---|
| `RecetteProduitService.creerRecette()` | Crée/versionne une recette (désactive l'ancienne, insère la nouvelle) |
| `RecetteProduitService.getRecetteActive()` | Récupère les lignes actives d'une catégorie |

---

## Résumé

| Total tests | Succès | Échecs |
|---|---|---|
| 2 | 2 ✅ | 0 |
