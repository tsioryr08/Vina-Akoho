-- V20 a renommé livraison.id_commande -> id_vente mais n'a pas recrée
-- la contrainte de clé étrangère, qui pointait toujours vers commande(id).
-- Résultat : insertion d'un id_vente valide rejetée car absent de "commande".

DELETE FROM livraison
WHERE id_vente IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM vente WHERE vente.id = livraison.id_vente);

-- V20 a renommé livraison.id_commande -> id_vente mais n'a pas recréé
-- la contrainte de clé étrangère, qui pointait toujours vers commande(id).
-- Résultat : insertion d'un id_vente valide rejetée car absent de "commande".

ALTER TABLE livraison
    DROP CONSTRAINT IF EXISTS livraison_id_commande_fkey;

ALTER TABLE livraison
    ADD CONSTRAINT livraison_id_vente_fkey
    FOREIGN KEY (id_vente) REFERENCES vente(id) ON DELETE RESTRICT;
