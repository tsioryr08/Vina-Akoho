
# 🟡 Sprint 2 —  Stocks + Ventes

## Dépendances

```text
F1 Produits ──►      F3 Stocks
                            ↑
F5 Matieres-premieres ──────┘
```
---


| Fonctionnalité | Tâches                                      | Responsable |
| -------------- | -----------                                 | ------------|
| F3 Stocks      | Entrer.MP                                   | Rary        |
| F3 Stocks      | Sortie.MP                                   | Tsiory      |
| F3 Stocks      | Entrer.Produit                              | Mpiaro      |
| F4 Ventes      | Sortie Produit (FIFO) + Vente + Facturation | Ny Antema   |

Pour faire ce vente , utiliser des donnees inserer directement dans la base pour le test
### Bien Lire regles Metier en bas pour en savoir plus 



### REMARQUE:
        Quand on fait une entrer de produit , alors on fait une sortie de matiere_premieres et on insert dans lot_produit
        Quand on fait une sortie de produit ,on fait une mise a jour de lot_produit

FLUX:
```
F2 Matières premières
        │
        ▼
Stock MP disponible
        │
        ▼
F1 Produit
(Catalogue uniquement)
        │
        ▼
F3 Entrée Produit
(= Production)
        │
        ├── Consommer les matières premières
        ├── Créer un lot_produit
        ├── Créer un mouvementStockMP (Sortie)
        └── Créer un mouvementStockPR (Entrée)
                │
                ▼
Stock Produit disponible
                │
                ▼
F4 Vente
                │
                ▼
Sortie Produit (FIFO)
```


Table a utiliser:
### Stock:
* lot_produit
* lot_mp
* produit
* mouvement_stock_mp
* mouvement_stock_produit

### REGLE METIER:
Entree produit:
```
1. Vérifier que les matières premières sont suffisantes.

2. Lire la recette_produit.

3. Déduire automatiquement les matières premières.

4. Enregistrer un mouvementStockMP (Sortie).

5. Créer un lot_produit.

6. Enregistrer un mouvementStockPR (Entrée).

7. Mettre à jour le stock du produit.
```

Lors d'une vente:
```
1. Vérifier que le stock est suffisant.

2. Sélectionner automatiquement le lot_produit le plus ancien (FIFO).

3. Mettre à jour la quantité restante du lot.

4. Enregistrer un mouvementStockPR (Sortie).

5. Créer la vente et les lignes de vente.

6. Générer la facture.
```

### Taches en parallele: A finir Demain MArdi a 20 h 
#### Tache 1:
Responsable:
    Herizo + avec les memenbres qui ont fait les fonctionnalite qui sont deja code pour ne pas modifier des trucs qui pourrait ruiner le projet

Taches:
    Modifier et integrer les liens qui sont deja operationnel dans les dashboard qui va dans :
        * clients
        * produits 
        * matieres premieres

#### Tache 2:
Responsable:
Maude + Manou + avec les memenbres qui ont fait les fonctionnalite qui sont deja code pour ne pas modifier des trucs qui pourrait ruiner le projet 

Tache:
Adapter le css des pages qui ne respect pas la maquette.
Ne pas modifier les champs etc mais juste le css pour s'assurer que ca soit du meme forme que le template
exmple : il y a des pages qui ne possede pas de side bar comme dans le module produit
        
### Deadline livraison : Mercredi a 20 h 
---


## SE PREPARER A FAIRE LES MODULES SUIVANTES APRES CELA:
        * LIVRAISON
        * STATISTIQUE