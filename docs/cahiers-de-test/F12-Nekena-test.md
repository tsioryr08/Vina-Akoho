
---

## 📋 Cahier de Tests - Vina Akoho
 Nekena , 8/04/26 19h55
### 1. Module Finance (Tâche 4)

| Test | Action | Résultat Attendu |
| --- | --- | --- |
| **FIN-01** | Charger le Dashboard | Les 4 graphiques et 3 KPIs s'affichent sans erreur. |
| **FIN-02** | Calcul Bénéfice | (Total Recettes - Total Dépenses) correspond au Bénéfice Net affiché. |
| **FIN-03** | Données API | Les endpoints `/api/finances-statistique/...` renvoient bien du JSON. |

---

### 2. Module Livraisons (Tâche 3)

| Test | Action | Résultat Attendu |
| --- | --- | --- |
| **LIV-01** | Compteur Livraison | Le nombre total de livraisons affiché correspond à la base. |
| **LIV-02** | Zone desservie | La liste des zones affiche tous les quartiers/villes uniques. |

---

### 3. Procédure de vérification rapide

Pour chaque module, vérifie manuellement ces deux points :

1. **Données :** Les chiffres affichés correspondent-ils aux données insérées dans `psql` ? 
oui
2. **Interface :** Les graphiques sont-ils interactifs (hover sur les barres/parts) et sans erreur JS dans la console du navigateur 
oui

