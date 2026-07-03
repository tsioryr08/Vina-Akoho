DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'vente'
          AND column_name = 'id_commande'
    ) THEN
        ALTER TABLE vente
        ADD COLUMN IF NOT EXISTS id_client INTEGER;

        UPDATE vente v
        SET id_client = c.id_client
        FROM commande c
        WHERE v.id_commande = c.id
          AND v.id_client IS NULL;

        ALTER TABLE vente
        ALTER COLUMN id_client SET NOT NULL;

        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'fk_vente_client'
              AND conrelid = 'public.vente'::regclass
        ) THEN
            ALTER TABLE vente
            ADD CONSTRAINT fk_vente_client
            FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE RESTRICT;
        END IF;

        ALTER TABLE vente
        DROP COLUMN id_commande;
    END IF;
END $$;

