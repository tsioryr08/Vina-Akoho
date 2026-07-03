ALTER TABLE livraison
    RENAME COLUMN id_commande TO id_vente;

ALTER INDEX idx_livraison_commande
    RENAME TO idx_livraison_vente;