-- Ajout de la colonne manquante
ALTER TABLE livraison ADD COLUMN IF NOT EXISTS id_zone_livraison VARCHAR(50);

-- Ajout de la contrainte FK
ALTER TABLE livraison
    ADD CONSTRAINT fk_livraison_zone_livraison
        FOREIGN KEY (id_zone_livraison) REFERENCES zone_livraison(id)
            ON DELETE SET NULL;