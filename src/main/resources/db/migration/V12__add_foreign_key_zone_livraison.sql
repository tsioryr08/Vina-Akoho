-- Ajouter la contrainte de clé étrangère pour id_zone_livraison
-- Cette migration doit être exécutée après V12__create_zone_livraison.sql

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_client_zone_livraison'
          AND conrelid = 'public.client'::regclass
    ) THEN
        ALTER TABLE client
        ADD CONSTRAINT fk_client_zone_livraison
        FOREIGN KEY (id_zone_livraison) REFERENCES zone_livraison(id) ON DELETE SET NULL;
    END IF;
END $$;
