DROP INDEX IF EXISTS uq_recette_active_par_categorie;

CREATE UNIQUE INDEX IF NOT EXISTS uq_recette_active_par_categorie_mp
ON recette_produit (id_categorie, id_mp)
WHERE is_active = true;
