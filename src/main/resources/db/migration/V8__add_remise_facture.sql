-- Ajouter la colonne remise à la table facture
ALTER TABLE facture
ADD COLUMN IF NOT EXISTS remise DECIMAL(12, 2) DEFAULT 0 CHECK (remise >= 0);
