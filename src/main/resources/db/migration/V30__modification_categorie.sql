-- Nettoyage anciennement prévu pour réordonner des catégories de test.
-- Sur une base fraîche, les ids 4 et 5 n'existent pas, donc la migration
-- doit rester sans effet pour éviter de casser le démarrage Flyway.

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM categorie WHERE id = 4)
     AND EXISTS (SELECT 1 FROM categorie WHERE id = 5)
     AND EXISTS (SELECT 1 FROM categorie WHERE id = 3)
  THEN
    -- ============================================================
    -- ÉTAPE 1 : libérer les ids 1 et 2 (catégories de test)
    -- ============================================================

    UPDATE produit SET id_categorie = 4 WHERE id_categorie = 1;
    UPDATE produit SET id_categorie = 5 WHERE id_categorie = 2;

    DELETE FROM categorie WHERE id IN (1, 2);

    -- ============================================================
    -- ÉTAPE 2 : renumérotation 3 -> 1, 4 -> 2, 5 -> 3
    -- ============================================================

    UPDATE categorie SET libelle = libelle || ' (tmp)' WHERE id = 3;

    INSERT INTO categorie (id, actif, created_at, description, libelle, marge_pourcentage,
                            pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at)
    SELECT 1, actif, created_at, description, replace(libelle, ' (tmp)', ''), marge_pourcentage,
           pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at
    FROM categorie WHERE id = 3;

    UPDATE produit SET id_categorie = 1 WHERE id_categorie = 3;
    DELETE FROM categorie WHERE id = 3;

    UPDATE categorie SET libelle = libelle || ' (tmp)' WHERE id = 4;

    INSERT INTO categorie (id, actif, created_at, description, libelle, marge_pourcentage,
                            pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at)
    SELECT 2, actif, created_at, description, replace(libelle, ' (tmp)', ''), marge_pourcentage,
           pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at
    FROM categorie WHERE id = 4;

    UPDATE produit SET id_categorie = 2 WHERE id_categorie = 4;
    DELETE FROM categorie WHERE id = 4;

    UPDATE categorie SET libelle = libelle || ' (tmp)' WHERE id = 5;

    INSERT INTO categorie (id, actif, created_at, description, libelle, marge_pourcentage,
                            pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at)
    SELECT 3, actif, created_at, description, replace(libelle, ' (tmp)', ''), marge_pourcentage,
           pourcentage_humidite_max, pourcentage_matiere_grasses, pourcentage_proteine, updated_at
    FROM categorie WHERE id = 5;

    UPDATE produit SET id_categorie = 3 WHERE id_categorie = 5;
    DELETE FROM categorie WHERE id = 5;

    PERFORM setval(
      pg_get_serial_sequence('categorie', 'id'),
      (SELECT MAX(id) FROM categorie)
    );
  END IF;
END $$;
