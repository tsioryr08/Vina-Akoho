# Améliorations proposées pour le module Vente / Commande

## 1. Gestion de la livraison

Lors de la création d'une commande, le responsable commercial doit pouvoir indiquer si le client souhaite une livraison.

### Fonctionnement proposé

- Ajouter un champ **"Livraison requise ?"** (Oui / Non).
- Si **Oui** :
  - afficher les champs nécessaires à la livraison ;
  - enregistrer les informations de livraison avec la commande.
- Si **Non** :
  - aucune information supplémentaire n'est demandée.

### Informations de livraison

- Adresse de livraison
- Zone de livraison
- Date de livraison souhaitée (optionnelle)
- Commentaire ou instructions de livraison (optionnel)

---

## 2. Annulation d'une commande

Une commande doit pouvoir être annulée tant qu'elle n'a pas été finalisée.

### Actions possibles

- Valider le paiement
- Annuler la commande

### Statuts proposés

- En attente de paiement
- Validée
- Annulée

Une commande annulée ne doit plus être considérée comme une vente réalisée.

---

## 3. Affichage de l'unité des produits

Dans les lignes de vente, l'unité affichée doit correspondre à l'unité du produit.

### Exemples

- kg
- sac
- carton
- litre
- pièce

L'affichage actuel devra être remplacé par l'unité réelle enregistrée pour chaque produit.
surtout dans detail de vente
---

## 4. Calcul du chiffre d'affaires

Le chiffre d'affaires doit uniquement prendre en compte les ventes effectivement réalisées.

Les commandes ayant les statuts suivants ne doivent **pas** être comptabilisées :

- En attente de paiement
- Annulée

Les statistiques suivantes doivent être calculées uniquement à partir des commandes **Validées** :

- Chiffre d'affaires
- Produits les plus vendus
- Quantités vendues
- Pourcentage des ventes
- Tableaux de bord
- Graphiques
- Rapports commerciaux

---