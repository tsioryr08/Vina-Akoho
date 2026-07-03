-- Renommer colonne et index dans la table livraison
ALTER TABLE livraison
    RENAME COLUMN id_commande TO id_vente;

ALTER INDEX idx_livraison_commande
    RENAME TO idx_livraison_vente;

-- Ajouter id_client à la table vente
ALTER TABLE vente
    ADD COLUMN id_client INTEGER NOT NULL;

ALTER TABLE vente
    ADD CONSTRAINT fk_vente_client FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE RESTRICT;

CREATE INDEX idx_vente_client ON vente(id_client);