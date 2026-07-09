
---

# RAPPORT TECHNIQUE UNIFIÉ - DASHBOARD DE PILOTAGE FINANCE ET LIVRAISON



---

## 1. ARCHITECTURE DES SERVICES

### 1.1 Couche API (REST Controllers)

* **`FinanceStatController`** : Exposition des données agrégées (Dépenses, Recettes, Bénéfice).
* **`LivraisonStatistiqueController`** : Exposition des métriques opérationnelles (Volume, Zones).

### 1.2 Couche Service

* **`BeneficeService`** : Logique métier de calcul financier et évolution temporelle.
* **`LivraisonService`** : Agrégation des données géographiques et comptage des flux.

---

## 2. SYNTHÈSE DES ENDPOINTS API

| Domaine | Méthode | Endpoint | Description |
| --- | --- | --- | --- |
| **Finance** | GET | `/api/finances-statistique/depenses-categorie` | Répartition des dépenses par catégorie |
| **Finance** | GET | `/api/finances-statistique/depenses-phase` | Répartition des dépenses par phase |
| **Finance** | GET | `/api/finances-statistique/recettes-mensuelles` | Recettes totales par mois |
| **Finance** | GET | `/api/finances-statistique/evolution-benefice` | Tendance du bénéfice net (mensuel) |
| **Livraison** | GET | `/api/statistiques/livraisons/total` | Nombre total de livraisons |
| **Livraison** | GET | `/api/statistiques/livraisons/zones` | Répartition des livraisons par zone |

---

## 3. LOGIQUE MÉTIER ET PERFORMANCE

### 3.1 Agrégation & Rendu

* **Données Financières** : Utilisation de `TreeMap` pour garantir la chronologie des séries temporelles (Bénéfices).
* **Données Opérationnelles** : Utilisation de `Map<String, Long>` pour un typage précis des volumes par zone.
* **Performance Frontend** : Utilisation de `Promise.all()` en JavaScript pour charger les 6 sources de données en parallèle, assurant un affichage fluide du dashboard.

---



---