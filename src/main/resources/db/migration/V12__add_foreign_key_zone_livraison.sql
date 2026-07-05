-- Ajouter la contrainte de clé étrangère pour id_zone_livraison
-- Cette migration doit être exécutée après V12__create_zone_livraison.sql

-- Insérer la zone manquante si elle n'existe pas déjà
INSERT INTO zone_livraison (id, libelle) VALUES ('ZONE-VTE-01', 'Zone Vente 01') ON CONFLICT (id) DO NOTHING;

-- Mettre à NULL les références clients vers des zones qui n'existent pas encore
UPDATE client SET id_zone_livraison = NULL WHERE id_zone_livraison IS NOT NULL AND NOT EXISTS (SELECT 1 FROM zone_livraison WHERE id = client.id_zone_livraison);

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
