ALTER TABLE lot_mp
ADD COLUMN IF NOT EXISTS id_fournisseur INTEGER;

ALTER TABLE lot_mp
ADD COLUMN IF NOT EXISTS cout_unitaire DECIMAL(10, 2);

ALTER TABLE lot_mp
ADD CONSTRAINT fk_lot_mp_fournisseur
FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_lot_mp_fournisseur ON lot_mp(id_fournisseur);

ALTER TABLE mode_paiement
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE mouvement_stock_mp
ALTER COLUMN date_mouvement TYPE DATE USING date_mouvement::date,
ALTER COLUMN date_mouvement SET DEFAULT CURRENT_DATE;

ALTER TABLE mouvement_stock_produit
ALTER COLUMN date_mouvement TYPE DATE USING date_mouvement::date,
ALTER COLUMN date_mouvement SET DEFAULT CURRENT_DATE;
