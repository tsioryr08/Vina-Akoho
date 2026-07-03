-- Créer la table historique_prix_produit pour suivre les changements de prix de vente
CREATE TABLE IF NOT EXISTS historique_prix_produit (
    id SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    ancien_prix DECIMAL(10, 2) NOT NULL CHECK (ancien_prix >= 0),
    nouveau_prix DECIMAL(10, 2) NOT NULL CHECK (nouveau_prix >= 0),
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_employe INTEGER,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE CASCADE,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_historique_prix_produit_produit ON historique_prix_produit(id_produit);
CREATE INDEX IF NOT EXISTS idx_historique_prix_produit_date ON historique_prix_produit(date_modification);
