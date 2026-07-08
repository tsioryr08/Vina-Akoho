-- Nettoyage ciblé avant réinjection des données de test statistiques
-- Objectif :
-- 1) supprimer les ventes de test déjà présentes
-- 2) supprimer leurs lignes, factures et mouvements de stock liés
-- 3) remettre à zéro les lots de produits de test
-- 4) laisser intacts les référentiels (clients, catégories, produits, modes de paiement, statuts)

BEGIN;

-- On cible uniquement les ventes liées aux produits de test connus
-- utilisés pour les statistiques du Sprint 3.
CREATE TEMP TABLE tmp_ventes_stats_a_supprimer AS
SELECT DISTINCT v.id
FROM vente v
JOIN ligne_vente lv ON lv.id_vente = v.id
JOIN produit p ON p.id = lv.id_produit
WHERE p.ref IN ('PRD-001', 'TST-REC-001', 'TST-REC-002');

-- Supprimer les éventuelles lignes de ventilation par lot
DELETE FROM ligne_vente_lot
WHERE id_ligne_vente IN (
    SELECT lv.id
    FROM ligne_vente lv
    WHERE lv.id_vente IN (SELECT id FROM tmp_ventes_stats_a_supprimer)
);

-- Supprimer les factures associées
DELETE FROM facture
WHERE id_vente IN (SELECT id FROM tmp_ventes_stats_a_supprimer);

-- Supprimer les lignes de vente
DELETE FROM ligne_vente
WHERE id_vente IN (SELECT id FROM tmp_ventes_stats_a_supprimer);

-- Supprimer les ventes de test
DELETE FROM vente
WHERE id IN (SELECT id FROM tmp_ventes_stats_a_supprimer);

-- Supprimer les mouvements de stock éventuellement générés par les tests de vente
DELETE FROM mouvement_stock_produit
WHERE reference_document LIKE 'VENTE-%'
   OR reference_document LIKE 'FACT-VALTEST-%'
   OR reference_document LIKE 'FACT-REC-%';

-- Réinitialiser les lots de produits de test.
-- Cela évite qu'une vente précédente réduise encore le stock visible
-- lors de futurs tests de vente ou de statistiques.
UPDATE lot_produit lp
SET quantite_restante = lp.quantite_initiale
WHERE lp.id_produit IN (
    SELECT id
    FROM produit
    WHERE ref IN ('PRD-001', 'TST-REC-001', 'TST-REC-002')
);

-- Remettre les séquences au bon niveau
SELECT setval(pg_get_serial_sequence('vente', 'id'), COALESCE((SELECT MAX(id) FROM vente), 1));
SELECT setval(pg_get_serial_sequence('ligne_vente', 'id'), COALESCE((SELECT MAX(id) FROM ligne_vente), 1));
SELECT setval(pg_get_serial_sequence('ligne_vente_lot', 'id'), COALESCE((SELECT MAX(id) FROM ligne_vente_lot), 1));
SELECT setval(pg_get_serial_sequence('facture', 'id'), COALESCE((SELECT MAX(id) FROM facture), 1));
SELECT setval(pg_get_serial_sequence('mouvement_stock_produit', 'id'), COALESCE((SELECT MAX(id) FROM mouvement_stock_produit), 1));

DROP TABLE tmp_ventes_stats_a_supprimer;

COMMIT;

SELECT 'Nettoyage des ventes de test terminé. Vous pouvez maintenant exécuter V_31_donnee_test_statistiques.sql.' AS message;
