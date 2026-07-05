DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'livraison'
          AND column_name = 'id_commande'
    ) THEN
        ALTER TABLE livraison
            RENAME COLUMN id_commande TO id_vente;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM pg_indexes
        WHERE schemaname = 'public'
          AND tablename = 'livraison'
          AND indexname = 'idx_livraison_commande'
    ) THEN
        ALTER INDEX idx_livraison_commande
            RENAME TO idx_livraison_vente;
    END IF;
END $$;
