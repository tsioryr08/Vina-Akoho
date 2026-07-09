# 🟠 Sprint 2.2 — Rattrapages + Livraisons + Finances

## 🎯 Objectif

Finaliser les fonctionnalités indispensables oubliées lors des premiers sprints et préparer les modules d'analyse (Dashboard, Statistiques et Prévisions).

---

# 📌 Dépendances

```text
F4 Ventes
      │
      ├────────► F6 Livraisons
      │
      ├────────► F7 Finances
      │
      └────────► Dashboard
```

---

# 🔴 RATTRAPAGES

## 1) Historique des achats + Créances / Dettes

### Responsable

Herizo

### Tâches

- Afficher l'historique complet des achats d'un client.
- Afficher toutes les ventes du client.
- Calculer automatiquement le montant restant à payer.
- Afficher le solde du client sur sa fiche.

### Tables utilisées

- client
- vente
- ligne_vente
- facture

### Règles métier

```
Une vente appartient obligatoirement à un client.

Le montant des créances correspond à :

Somme des ventes
-
Somme des paiements

Le solde doit être recalculé automatiquement après chaque paiement.
```

---

## 2) Alertes de stock faible

### Responsable

Rary

### Tâches

- Vérifier les seuils d'alerte des produits.
- Vérifier les seuils des matières premières.
- Afficher les alertes dans le Dashboard.

### Tables utilisées

- produit
- matierePremiere

### Règles métier

```
Si

Quantité actuelle <= seuil_alerte

Alors

Créer une notification.

Une notification ne doit apparaître qu'une seule fois tant que le stock n'est pas réapprovisionné.
```

---

## 3) Historique des prix des produits

### Responsable

Ny Antema

### Tâches

- Enregistrer automatiquement chaque changement de prix.
- Afficher l'historique des prix sur la fiche produit.

### Tables utilisées

- produit
- historique_prix_produit

### Règles métier

```
Toute modification du prix de vente

↓

Créer automatiquement une ligne dans historique_prix_produit.
```

---

# 🚚 F6 — Gestion des Livraisons

### Responsable

Maude

---

## Tâches

- Créer une livraison.
- Associer une livraison à une vente.
- Associer automatiquement le client.
- Choisir un livreur.
- Modifier le statut.
- Consulter l'historique.

---

### Tables utilisées

- livraison
- livreur
- historiqueChangement
- statutLivraison

---

### Règles métier

```
Une livraison est associée à une commande.

Une vents appartient à un client.

Une livraison possède un statut :

En préparation

↓

En cours

↓

Livrée

ou

Annulée
```

### Remarque , dans la table livraison:

```
supprimer la colonne id_commande et remplacer par id_ventes
Car un livraison est associer a une vente pas a une commande 
```

---

### Règles de gestion

```
Impossible de créer une livraison
si la vente n'existe pas.

Chaque changement de statut
doit être enregistré dans historiqueChangement.
```

---

# 💰 F7 — Gestion Financière

---

## 1) Dépenses

### Responsable

Manohisoa

### Tâches

- CRUD Dépenses.
- Catégories.
- Phases.
- Statuts.

### Tables utilisées

- depense
- categorieDepense
- phase
- statutDepense

### Règles métier

```
Chaque dépense possède :

une catégorie

une phase

un montant

une date

un statut
```

---

### Règles de gestion

```
Le montant doit être supérieur à zéro.
```

---

## 2) Recettes

### Responsable

Armando

### Tâches

- Calcul automatique des recettes.
- Affichage des recettes par période.

### Tables utilisées

- vente
- produit

### Règles métier

```
Chaque vente validée

↓

Génère automatiquement une recette.( priz de vente d'un produit dans la table produits)
```

---

### Règles de gestion

```
Les ventes annulées
ne sont pas comptabilisées.
```

---

## 3) Bénéfices

### Responsable

Nekena

### TâchesM

- Calcul des bénéfices.
- Affichage par période.

### Tables utilisées

- vente
- depense

### Règles métier

```
Bénéfice

=

Recettes

-

Dépenses
```

---

### Règles de gestion

```
Les bénéfices doivent être recalculés automatiquement
après chaque nouvelle vente ou dépense.
```

---

# 📌 Préparation du Sprint suivant

Le Sprint suivant utilisera directement :

- Dashboard
- Statistiques
- Prévisions

Toutes les données produites dans ce sprint devront être exploitables.

---

# ✅ Critères de validation

Le Sprint est terminé si :

- [ ] L'historique des achats fonctionne.
- [ ] Les créances sont correctement calculées.
- [ ] Les alertes de stock apparaissent.
- [ ] Les livraisons peuvent être créées.
- [ ] Les statuts des livraisons sont enregistrés.
- [ ] Les dépenses peuvent être saisies.
- [ ] Les recettes sont calculées automatiquement.
- [ ] Les bénéfices sont calculés.
- [ ] Toutes les données sont visibles dans les interfaces.

---

# 📌 Remarques

## 1)

Le tableau de bord du commercial devra correspondre à la page **/ventes** afin de lui permettre d'accéder rapidement à toutes les ventes, commandes et factures.

---


