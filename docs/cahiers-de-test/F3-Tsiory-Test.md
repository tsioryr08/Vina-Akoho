# Cahier de test — F3 Sortie Matières Premières

---

Date : 30/06/2026
Testeur : Tsiory
Module : F3 — Sortie automatique MP (FIFO)
Registration : admin@vinaakoho.mg / admin123

---

## Test 1 — Authentification (pré-requis)

Résultat attendu : Cookie de session JSESSIONID généré
Résultat obtenu :
```
Set-Cookie: JSESSIONID=D8A392C3301FD6789CD9CEAEFCA8C141
→ Redirection 302 vers /admin
```
Statut : ☑ Succès

---

## Test 2 — Sortie MP sans session valide

Commande :
```bash
curl -X POST http://localhost:8081/api/stock-mp/sortie \
  -H "Content-Type: application/json" \
  -d '{"idCategorie": 1, "idEmploye": 1, "referenceDocument": "TEST-FIFO-001"}'
```
Résultat attendu : Rejet avec message d'authentification
Résultat obtenu :
```json
{"success": false, "message": "Authentification requise. Session expirée ou inexistante.", "data": null}
```
Statut : ☑ Succès (comportement attendu — filtre F0 actif)

---

## Test 3 — Sortie MP avec session valide + vérification FIFO

Données en base avant le test :
```sql
-- MP id=1 (Maïs jaune) : 2 lots à dates différentes
lot id=1 : quantite_restante=40, date_achat='2026-06-01'  ← plus ancien
lot id=2 : quantite_restante=20, date_achat='2026-06-25'
-- MP id=2 (Son de riz) : 1 lot
lot id=3 : quantite_restante=25, date_achat='2026-06-10'
```
Recette active pour idCategorie=1 :
- Maïs jaune (idMp=1) → 60 unités
- Son de riz  (idMp=2) → 25 unités

Commande :
```bash
curl -c cookies.txt -X POST http://localhost:8081/api/login \
  -d "email=admin@vinaakoho.mg&mdp=admin123"

curl -b cookies.txt -X POST http://localhost:8081/api/stock-mp/sortie \
  -H "Content-Type: application/json" \
  -d '{"idCategorie": 1, "idEmploye": 1, "referenceDocument": "TEST-FIFO-001"}'
```
Résultat attendu :
- Lot 1 (Maïs jaune, 2026-06-01) vidé en premier → 40 unités déduites
- Lot 2 (Maïs jaune, 2026-06-25) complète le reste → 20 unités déduites
- Lot 3 (Son de riz) → 25 unités déduites en une seule passe
- 3 mouvements de sortie créés en base

Résultat obtenu :
```json
{
  "success": true,
  "message": "Sortie de matières premières effectuée",
  "data": [
    {"id":1,"idLotMp":1,"idUnite":1,"nomMp":"Maïs jaune","quantite":40.00,"typeMouvement":"Sortie"},
    {"id":2,"idLotMp":2,"idUnite":1,"nomMp":"Maïs jaune","quantite":20.00,"typeMouvement":"Sortie"},
    {"id":3,"idLotMp":3,"idUnite":1,"nomMp":"Son de riz","quantite":25.00,"typeMouvement":"Sortie"}
  ]
}
```
Statut : ☑ Succès
Commentaire : FIFO validé — lot du 01/06 consommé entièrement avant le lot du 25/06.
Les 3 mouvements de sortie sont bien créés dans mouvement_stock_mp.

---

## Bugs identifiés & résolus

| Bug | Cause | Résolution |
|-----|-------|------------|
| `BeanDefinitionOverrideException` sur `lotMpRepository` | `LotMp.java` et `LotMpRepository.java` dupliqués dans `entity/stockmp/` et `entity/matierespremieres/` | Suppression des doublons dans `stockmp/`, conservation des classes de Rary |
| `package ... stockmp does not exist` | `MouvementStockMpRepository` importé depuis `stockmp` alors que seul `matierespremieres` existe | Import corrigé vers `repository.matierespremieres` |
| `cannot find symbol: LotMp` dans `entity/stockmp/MouvementStockMp.java` | `MouvementStockMp` de stockmp référençait l'ancien `LotMp` supprimé | Suppression de `entity/stockmp/MouvementStockMp.java`, utilisation de celui de Rary |
