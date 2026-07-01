ALTER TABLE recette_produit DROP CONSTRAINT uq_recette_active_par_categorie;

CREATE UNIQUE INDEX uq_recette_active_par_categorie_mp 
ON recette_produit (id_categorie, id_mp) 
WHERE is_active = true;

\d recette_produit;

DROP INDEX IF EXISTS uq_recette_active_par_categorie;