# 🔵 Sprint 3 — Dashboard + Statistiques + Prévisions
# A rendre Mercredi a 21h 

## 🎯 Objectif

Exploiter les données générées lors des sprints précédents afin de fournir :

- des tableaux de bord
- des statistiques
- des indicateurs de performance (KPI)
- des prévisions de production

---

# 📌 Dépendances

```text
F2 Matières premières
        │
F3 Stocks
        │
F4 Ventes
        │
F6 Livraisons
        │
F7 Finances
        │
        ▼
Dashboard + Statistiques + Prévisions
```

---

# 📊 Historique des ventes

### Responsable

Ny Antema

### Tâches

- Créer une page listant toutes les ventes.
- Trier automatiquement les ventes de la plus récente à la plus ancienne.
- Afficher les informations principales :
  - Client
  - Date
  - Montant
  - Mode de paiement
  - Statut

### Règles métier

```
1. Pagination (10 éléments par défaut, configurable).

2. Recherche par :

- Client
- Produit
- Numéro de facture

3. Filtres :

- Date début / fin
- Mode de paiement
- Statut
- Montant

4. Tri :

- Date
- Montant
- Client
```

---

# 📈 DASHBOARD

---

## 👨 Administrateur

### Responsable : Tsiory

### Tâches

### 1) Gestion des utilisateurs

- Liste des utilisateurs actifs
- Liste des utilisateurs désactivés
- Recherche
- Filtre par rôle

### Tables utilisées

```
employe

role
```

---

### 2) Gestion des comptes

CRUD complet

- Création
- Modification
- Désactivation
- Réinitialisation mot de passe

---

## 👨‍💼 Commercial

### Responsable : Maude + Manohisoa

### Tâches

### 1) Dashboard Commercial : Maude

Supprimer les commandes.

Le tableau de bord commercial devient directement :

```
/ventes
```

Afficher :

- Nombre de ventes aujourd'hui
- Chiffre d'affaires du jour
- Factures générées
- Clients servis

---

### 2) Export PDF : Manohisoa

Permettre l'export :

- Historique des ventes
- Facture
- Rapport des ventes

---

## 🏭 Responsable Production

### Responsable : Herizo


### Tâches

Modifier les cartes du Dashboard.

Afficher :

1. Quantité actuelle des produits finis.

2. Quantité actuelle des matières premières.

3. Nombre de lots produits.

4. Nombre de lots expirant bientôt.

5. Nombre de produits sous le seuil d'alerte.

6. Nombre de matières premières sous le seuil minimum.

---

### Règles métier

Les cartes doivent être mises à jour automatiquement après :

- une production
- une vente
- un achat de MP

---

## 💰 Responsable Financier

### Responsable : Rary

...

### Tâches

Afficher :

- Dépenses du mois
- Recettes du mois
- Bénéfice
- Evolution mensuelle

---

# 📊 Statistiques et KPI

### Responsable : Mpiaro + Nekena


---

## 1) Produits les plus vendus  +  Catégories les plus vendues: 

Afficher :

- Top 10 produits
- Quantité vendue
- Pourcentage des ventes
- Nombre de ventes

Graphique :

- Barres

---

## 2) Evolution des ventes 

Afficher :

- Jour
- Semaine
- Mois

Graphique :

- Courbe

---

## 3) Livraisons       

Afficher :

- Nombre de livraisons
- Zones les plus desservies

---

## 4) Finances    

Afficher :

- Dépenses par catégorie
- Dépenses par phase
- Recettes mensuelles
- Evolution du bénéfice

---

# 🔮 Prévisions

### Responsable : ARMANDO


---

## 1) Prévision de production

Calculer :

```
Quantité moyenne vendue

+

Stock actuel

↓

Proposition de production
```

---

## 2) Prévision des matières premières

Calculer automatiquement 


---

# ✅ Critères de validation

Le Sprint est terminé si :

- [ ] Les dashboards affichent les bonnes données.
- [ ] Les statistiques sont calculées automatiquement.
- [ ] Les graphiques fonctionnent.
- [ ] Les exports PDF sont opérationnels.
- [ ] Les prévisions utilisent les données réelles.
- [ ] Les recherches et filtres fonctionnent.
- [ ] Les KPI sont mis à jour automatiquement.

---

# 📌 Préparation de la soutenance

- Vérification générale
- Correction des bugs
- Optimisation de l'interface
- Tests utilisateurs
- Préparation de la démonstration
- Nettoyage du code
- Documentation