-- V20 a renommé livraison.id_commande -> id_vente mais n'a pas recréé
-- la contrainte de clé étrangère, qui pointait toujours vers commande(id).
-- Résultat : insertion d'un id_vente valide rejetée car absent de "commande".

ALTER TABLE livraison
    DROP CONSTRAINT IF EXISTS livraison_id_commande_fkey;

ALTER TABLE livraison
    ADD CONSTRAINT livraison_id_vente_fkey
    FOREIGN KEY (id_vente) REFERENCES vente(id) ON DELETE RESTRICT;
