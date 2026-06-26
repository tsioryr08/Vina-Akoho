# 🟢 Sprint 1 (Samedi - Mercredi) — Modules fondamentaux
## Bien lire les instructions et les remarques a la fin (pour tout le monde)

Ces modules sont indépendants.

| Fonctionnalité        | Tâches                 | Responsable        | Dépend de   |
| --------------------- | ---------------------- | ------------------ | ----------- |
| F0 Login              | Auth + filtre user     | Ny Antema          |             |
| F1 Produits           | T1.1, T1.2, T1.3, T1.4 | Nekena             |             |
| F2 Matières premières | T2.1, T2.2, T2.3, T2.4 | Rary               |             |
| F5 Clients            | T5.1, T5.2, T5.3, T5.4 | Armando            |             |

## ARCHITECTE BACKEND:
        * Tsiory
        * Mpiaro 
```
L'architecte impose des règles à toute l'équipe et surveille :
        ✅ DTO obligatoire
        ✅ Validation côté backend (Jakarta Validation)
        ✅ Gestion des exceptions (BusinessException, NotFoundException)
        ✅ Tests unitaires obligatoires
        ✅ Aide l'equipe en cas de blockage 
        etc ...
```
        

### Sequence du travaille :

```text
F0 Login              █████████

F2 Matières premières          █████████

F1 Produits                    █████████

F5 Clients                     █████████
```

Vous pouvez creer les fonctions necessaire a F1 ,F2 , F5 en attendant que F0 soit terminer puis juste integration

### Travail en parallèle a part  :
```text
        Manou + Maude : Le reste des HTML + css
        Herizo : Voir ce qui ne va pas encore sur google Sheet a propos de notre budget
```
### Table a utiliser:
        * matierePremiere
        * catgorieProduit
        * Produit
        * Recette_produit
        * Client
        * MouvementStockMP
        * Depenses
        * employe
        * role

### REMARQUE:
```
À chaque fin d'une tâche ou d'un module avec tests, chaque développeur doit fournir :
        
        📋 Cahier de test → docs/cahiers-de-test/FX-Nom-Test.md

                Liste des tests effectués

                Résultats (succès/échec)

                Bugs identifiés

        📄 Rapport technique → docs/rapports-techniques/FX-Nom-Rapport.md

                Structure des fichiers

                Fonctions principales (avec signatures Java)

                Logique métier expliquée

                Dépendances avec les autres modules

                Schémas/Diagrammes (si besoin)
```
### Deadline livraison : Mercredi a 20 h 
---
