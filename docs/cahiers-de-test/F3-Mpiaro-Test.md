# Cahier de Test — F3-Mpiaro-Test.md
## Module : Entrée Produit (`/api/entrees-produit`)
Testeur : Mpiaro
Date : 2026-07-01
Module : F3 — Entree-Produit
Données de test : `20260630-insert-test.sql + 20260701-insert-test-entree-produit.sql`
---

## Pré-requis — Authentification

L'API étant protégée par session, chaque test nécessite une connexion préalable pour obtenir le cookie `JSESSIONID`.

```bash
curl -c cookies.txt -X POST http://localhost:8081/api/login \
  -d "email=admin@vinaakoho.mg&mdp=admin123"
```

---

## Pré-requis — Données de test (dans 20260630-insert-test.sql de Tsiory + 20260701-insert-test-entree-produit.sql)

| Table | Donnée |
|---|---|
| `produit` | id=1, "Aliment Poussin 10kg", catégorie Poussin (id=1) |
| `recette_produit` | catégorie 1 : 2 kg Maïs jaune (id_mp=1) + 1 kg Son de riz (id_mp=2) par unité |
| `lot_mp` (id_mp=1) | 290 kg répartis sur 4 lots (dates : 01-10, 03-05, 06-01, 06-25) |
| `lot_mp` (id_mp=2) | 100 kg répartis sur 2 lots (dates : 02-01, 06-10) |
| `employe` | id=3, Responsable Production |

---

## Test 1 — Production réussie avec déduction FIFO (POST)

| Champ | Détail |
|---|---|
| **Date** | 01/07/2026 |
| **Testeur** | Mpiaro |
| **Page** | `POST /api/entrees-produit` |
| **Registration** | `admin@vinaakoho.mg` / `admin123` |
| **Résultat attendu** | Produire 50 unités d'Aliment Poussin : déduire 100 kg de Maïs et 50 kg de Son de riz en respectant le FIFO (lot le plus ancien consommé en premier), créer un lot_produit, tracer la fabrication |
| **Résultat obtenu** | Lot produit créé (id=1, 50 unités). Maïs consommé sur le lot du 2026-01-10 (40 kg, épuisé) puis sur le lot du 2026-03-05 (60 kg, reste 40 kg). Son de riz consommé sur le lot du 2026-02-01 (50 kg, épuisé) |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | FIFO respecté : le lot du 2026-06-01 (plus récent) n'a pas été touché car les lots plus anciens suffisaient |

**Preuve :**
```bash
curl -b cookies.txt -X POST http://localhost:8081/api/entrees-produit \
  -H "Content-Type: application/json" \
  -d '{"idProduit": 1, "quantiteAProduire": 50, "datePeremption": null, "idEmploye": 3}'
```

```json
{"success":true,"message":"Production enregistrée avec succès",
"data":{
  "idLotProduit":1,
  "nomProduit":"Aliment Poussin 10kg",
  "quantiteProduite":50,
  "dateFabrication":"2026-07-01",
  "matieresPremieresConsommees":[
    {"idLotMp":1,"nomMp":"Maïs jaune","quantiteConsommee":40.00},
    {"idLotMp":2,"nomMp":"Maïs jaune","quantiteConsommee":60.00},
    {"idLotMp":3,"nomMp":"Son de riz","quantiteConsommee":50.00}
  ]
}}
```

**Vérification en base :**

```text
-- fabrication

id | quantite_produite | id_lot_produit | id_employe
----+--------------------+----------------+-----------
1 |              50.00 |              1 |          3
```

```text
-- fabrication_mp (traçabilité FIFO)

id | id_fabrication | id_lot_mp | quantite
----+----------------+-----------+---------
1 |              1 |         1 |    40.00
2 |              1 |         2 |    60.00
3 |              1 |         3 |    50.00
```

```text
-- mouvement_stock_mp (Sortie, id_type_mouvement=2)

id | id_type_mouvement | id_lot_mp | quantite
----+--------------------+-----------+---------
1 |                  2 |         1 |    40.00
2 |                  2 |         2 |    60.00
3 |                  2 |         3 |    50.00
```

```text
-- mouvement_stock_produit (Entrée, id_type_mouvement=1)

id | id_type_mouvement | id_lot_produit | quantite
----+--------------------+----------------+---------
1 |                  1 |              1 |    50.00
```

```text
-- stock lot_mp après consommation FIFO

id_mp | quantite_restante | date_achat
-------+--------------------+------------
1 |               0.00 | 2026-01-10  ← épuisé en premier
1 |              40.00 | 2026-03-05  ← partiellement consommé
1 |              50.00 | 2026-06-01  ← non touché
1 |             100.00 | 2026-06-25  ← non touché
2 |               0.00 | 2026-02-01  ← épuisé
2 |              50.00 | 2026-06-10  ← non touché
```
## Test 2 — Stock insuffisant (POST, cas d'échec)

| Champ | Détail |
|---|---|
| **Date** | 01/07/2026 |
| **Testeur** | Mpiaro |
| **Page** | `POST /api/entrees-produit` |
| **Registration** | `admin@vinaakoho.mg` / `admin123` |
| **Résultat attendu** | Rejeter la production de 200 unités (besoin: 400 kg de Maïs, disponible: 190 kg) sans insérer aucune donnée en base |
| **Résultat obtenu** | Erreur 400 retournée avec message explicite. Aucune ligne insérée dans `lot_produit`, `fabrication`, `fabrication_mp`, `mouvement_stock_mp`, `mouvement_stock_produit`. Stock `lot_mp` inchangé |
| **Statut** | ☑ Succès &nbsp;&nbsp;&nbsp; ☐ Échec |
| **Commentaire** | Confirme le bon fonctionnement du rollback `@Transactional` malgré la création de `lot_produit`/`fabrication` avant la boucle de vérification MP |

**Preuve :**
```bash
curl -b cookies.txt -X POST http://localhost:8081/api/entrees-produit \
  -H "Content-Type: application/json" \
  -d '{"idProduit": 1, "quantiteAProduire": 200, "datePeremption": null, "idEmploye": 3}'
```

```json
{"success":false,"message":"Stock insuffisant pour Maïs jaune (besoin: 400.00, disponible: 190.00)","data":null}
```

**Vérification en base (compteurs identiques avant/après, confirmant le rollback) :**
```Text
table                     | count
----------------------------+-------
lot_produit                |     1
fabrication                |     1
fabrication_mp             |     3
mouvement_stock_mp         |     3
mouvement_stock_produit    |     1

```
---

## Fonctions testées

| Fonction | Rôle |
|---|---|
| `EntreeProduitService.produire()` | Orchestration complète : vérification stock → lecture recette → déduction FIFO → création lot_produit → mouvements MP/Produit |
| `EntreeProduitService.getStockDisponible()` | Retourne le stock disponible d'un produit (somme des lots restants) |

---

## Résumé

| Total tests | Succès | Échecs |
|---|---|---|
| 2 | 2 ✅ | 0 |

## Commentaire
Il est à remarquer que des vérification directe via interface sont possible . 
Connexion avec un responsable de production , puis appuyez sur Entré Produit dans sidebar et vous pouvez faire des test direct aussi. Merci!