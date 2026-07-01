# Rapport Technique — F3-Tsiory-Rapport.md
## Module : Recette Produit (`recette_produit`)

---

## Structure des fichiers

```
entity/recetteproduit/RecetteProduit.java
dto/recetteproduit/RecetteProduitDTO.java
dto/recetteproduit/CreateRecetteProduitDTO.java
dto/recetteproduit/LigneRecetteProduitDTO.java
repository/recetteproduit/RecetteProduitRepository.java
service/recetteproduit/RecetteProduitService.java
controller/recetteproduit/RecetteProduitController.java
exception/recetteproduit/RecetteProduitException.java
exception/recetteproduit/RecetteProduitNotFoundException.java
```

---

## Fonctions principales

### `creerRecette(CreateRecetteProduitDTO dto) → List<RecetteProduitDTO>`
Crée une nouvelle version de recette pour une catégorie donnée. Désactive automatiquement l'ancienne recette active (si elle existe) et incrémente le numéro de version. Une recette regroupe **plusieurs lignes** (une par matière première utilisée).

### `getRecetteActive(Integer idCategorie) → List<RecetteProduitDTO>`
Retourne toutes les lignes actives (`is_active = true`) de la recette d'une catégorie donnée. Lève `RecetteProduitNotFoundException` si aucune recette active n'existe.

---

## Logique métier

Chaque recette est **versionnée par catégorie** (`id_categorie` + `version`). Lors de la création d'une nouvelle recette pour une catégorie déjà existante :
1. Les anciennes lignes sont désactivées (`is_active = false`, `date_fin` renseignée)
2. Le numéro de version est incrémenté
3. Les nouvelles lignes sont insérées avec `is_active = true`

Ça garantit un **historique complet** des recettes successives, sans jamais perdre la trace des anciennes formules.

---

## Correctif base de données

**Problème :** la contrainte `uq_recette_active_par_categorie` (UNIQUE sur `id_categorie` seul) empêchait d'avoir plusieurs lignes actives pour une même catégorie — alors qu'une recette nécessite plusieurs matières premières simultanément.

**Solution appliquée :**
```sql
DROP INDEX uq_recette_active_par_categorie;

CREATE UNIQUE INDEX uq_recette_active_par_categorie_mp
ON recette_produit (id_categorie, id_mp)
WHERE is_active = true;
```

Cette nouvelle contrainte garantit qu'une même matière première n'apparaît qu'**une seule fois** en version active par catégorie, tout en autorisant plusieurs matières premières actives en parallèle.

---

## Dépendances avec les autres modules

| Sens | Module | Détail |
|---|---|---|
| ⬅️ Dépend de | F2 Matières premières | Entité `MatierePremiere`, `MatierePremiereRepository` |
| ➡️ Sera utilisé par | Sortie MP (Tsiory) | La recette détermine les quantités de MP à déduire |
| ➡️ Sera utilisé par | Entrée Produit (Mpiaro) | Lecture de la recette active pour calculer la production |

---

## Endpoints API

| Méthode | Route | Description |
|---|---|---|
| `POST` | `/api/recettes` | Créer / mettre à jour (nouvelle version) une recette |
| `GET` | `/api/recettes/categorie/{id}` | Lire la recette active d'une catégorie |

> Authentification requise (session) pour toutes les routes.
