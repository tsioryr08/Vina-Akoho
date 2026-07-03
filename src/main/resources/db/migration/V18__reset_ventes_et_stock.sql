-- Réinitialiser les ventes et le stock
-- ATTENTION: Cette migration efface toutes les ventes et réinitialise le stock

-- Supprimer les lignes de vente
DELETE FROM ligne_vente_lot;
DELETE FROM ligne_vente;

-- Supprimer les factures (avant les ventes à cause de la FK)
DELETE FROM facture;

-- Supprimer les ventes
DELETE FROM vente;

-- Réinitialiser les stocks des lots de produits
UPDATE lot_produit 
SET quantite_restante = quantite_initiale;

-- Supprimer les mouvements de stock de sortie liés aux ventes
DELETE FROM mouvement_stock_produit 
WHERE id_type_mouvement IN (SELECT id FROM type_mouvement WHERE libelle ILIKE '%sortie%');
