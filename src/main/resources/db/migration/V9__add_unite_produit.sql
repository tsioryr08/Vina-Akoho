-- Ajouter la colonne id_unite à la table produit
ALTER TABLE produit
ADD COLUMN IF NOT EXISTS id_unite INTEGER;

ALTER TABLE produit
ADD CONSTRAINT fk_produit_unite
FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_produit_unite ON produit(id_unite);
