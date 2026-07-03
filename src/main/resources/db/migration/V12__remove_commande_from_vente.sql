DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'vente'
          AND column_name = 'id_commande'
    ) THEN
        UPDATE vente v
        SET id_client = c.id_client
        FROM commande c
        WHERE v.id_commande = c.id
          AND v.id_client IS NULL;

        ALTER TABLE vente
        ALTER COLUMN id_client SET NOT NULL;

        ALTER TABLE vente
        DROP COLUMN id_commande;
    END IF;
END $$;

