-- Script de données de test pour les modules de Ny Antema
-- F0 Login, F4 Ventes, F3 Historique Prix
-- Exécution : psql -h localhost -p 5432 -U vinakoho -d vinakoho -f docs/sql/donnees_test_nyantema.sql

-- ============================================
-- F0 LOGIN - Données d'authentification
-- ============================================

-- Réinitialiser les employés de test
DELETE FROM employe WHERE email IN ('admin@vinaakoho.mg', 'commercial@vinaakoho.mg');

-- Insérer l'administrateur
INSERT INTO employe (nom, prenom, email, mdp, id_role, est_actif) 
VALUES ('Admin', 'System', 'admin@vinaakoho.mg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, true);

-- Insérer un commercial pour tester F4 Ventes
INSERT INTO employe (nom, prenom, email, mdp, id_role, est_actif) 
VALUES ('Commercial', 'Test', 'commercial@vinaakoho.mg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 2, true);

-- ============================================
-- F4 VENTES - Données de test
-- ============================================

-- Réinitialiser les données de test
DELETE FROM ligne_vente_lot;
DELETE FROM ligne_vente;
DELETE FROM facture;
DELETE FROM vente;
DELETE FROM lot_produit WHERE id_produit IN (SELECT id FROM produit WHERE ref IN ('TEST-PROD-001', 'PROD-POULET-001'));
DELETE FROM mouvement_stock_produit WHERE reference_document LIKE 'VENTE-%';
DELETE FROM client WHERE nom = 'Client' AND prenom = 'Test';
DELETE FROM produit WHERE ref IN ('TEST-PROD-001', 'PROD-POULET-001');
DELETE FROM categorie WHERE libelle = 'Test Ventes';
DELETE FROM mode_paiement WHERE libelle IN ('Espèces', 'Carte bancaire', 'Mobile Money');

-- Insérer une catégorie de test
INSERT INTO categorie (libelle, marge_pourcentage) 
VALUES ('Test Ventes', 25.0);

-- Insérer des modes de paiement
INSERT INTO mode_paiement (libelle) VALUES ('Espèces'), ('Carte bancaire'), ('Mobile Money');

-- Insérer un produit de test pour F4 Ventes
INSERT INTO produit (ref, nom, id_categorie, prix_vente, seuil_alerte, description, actif) 
VALUES ('TEST-PROD-001', 'Produit Test F4', 
        (SELECT id FROM categorie WHERE libelle = 'Test Ventes'), 
        15000, 50, 'Produit de test pour les ventes', true);

-- Insérer un produit pour F3 Historique Prix
INSERT INTO produit (ref, nom, id_categorie, prix_vente, seuil_alerte, description, actif) 
VALUES ('PROD-POULET-001', 'Poulet entier', 
        (SELECT id FROM categorie WHERE libelle = 'Test Ventes'), 
        45000, 50, 'Poulet entier pour test historique prix', true);

-- Insérer des lots FIFO pour le produit de test F4 Ventes
-- Lot 1 : Plus ancien (date de fabrication il y a 10 jours)
INSERT INTO lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_expiration) 
VALUES (
    (SELECT id FROM produit WHERE ref = 'TEST-PROD-001'),
    100, 100, 
    CURRENT_DATE - INTERVAL '10 days', 
    CURRENT_DATE + INTERVAL '20 days'
);

-- Lot 2 : Plus récent (date de fabrication il y a 5 jours)
INSERT INTO lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_expiration) 
VALUES (
    (SELECT id FROM produit WHERE ref = 'TEST-PROD-001'),
    150, 150, 
    CURRENT_DATE - INTERVAL '5 days', 
    CURRENT_DATE + INTERVAL '25 days'
);

-- Insérer un client de test
INSERT INTO client (nom, prenom, numero_telephone, adresse, id_zone_livraison, est_supprimer) 
VALUES ('Client', 'Test', '0341234567', 'Antananarivo', 1, false);

-- ============================================
-- F3 HISTORIQUE PRIX - Données de test
-- ============================================

-- Réinitialiser l'historique de prix
DELETE FROM historique_prix_produit WHERE id_produit IN (SELECT id FROM produit WHERE ref = 'PROD-POULET-001');

-- Insérer un historique de prix pour le produit
-- Historique 1 : Prix initial 40000 → 45000
INSERT INTO historique_prix_produit (id_produit, ancien_prix, nouveau_prix, date_modification, id_employe) 
VALUES (
    (SELECT id FROM produit WHERE ref = 'PROD-POULET-001'),
    40000, 45000, 
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg')
);

-- Historique 2 : Prix 45000 → 48000
INSERT INTO historique_prix_produit (id_produit, ancien_prix, nouveau_prix, date_modification, id_employe) 
VALUES (
    (SELECT id FROM produit WHERE ref = 'PROD-POULET-001'),
    45000, 48000, 
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg')
);

-- Historique 3 : Prix 48000 → 45000 (baisse)
INSERT INTO historique_prix_produit (id_produit, ancien_prix, nouveau_prix, date_modification, id_employe) 
VALUES (
    (SELECT id FROM produit WHERE ref = 'PROD-POULET-001'),
    48000, 45000, 
    CURRENT_TIMESTAMP,
    (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg')
);

-- ============================================
-- RÉSUMÉ DES DONNÉES INSÉRÉES
-- ============================================

-- Employés pour F0 Login
-- - admin@vinaakoho.mg / admin123
-- - commercial@vinaakoho.mg / commercial123

-- Données pour F4 Ventes
-- - Produit : TEST-PROD-001 (Prix: 15000 Ar)
-- - Client : Client Test (ID: à vérifier)
-- - Modes de paiement : Espèces, Carte bancaire, Mobile Money
-- - Lots FIFO : 2 lots (100 + 150 unités)

-- Données pour F3 Historique Prix
-- - Produit : PROD-POULET-001 (Prix actuel: 45000 Ar)
-- - Historique : 3 entrées (40000→45000→48000→45000)

-- ============================================
-- COMMANDES DE VÉRIFICATION
-- ============================================

-- Vérifier les employés
-- SELECT id, nom, prenom, email, role FROM employe WHERE email IN ('admin@vinaakoho.mg', 'commercial@vinaakoho.mg');

-- Vérifier le produit F4 Ventes
-- SELECT id, ref, nom, prix_vente FROM produit WHERE ref = 'TEST-PROD-001';

-- Vérifier les lots FIFO
-- SELECT id, quantite_restante, date_fabrication FROM lot_produit WHERE id_produit = (SELECT id FROM produit WHERE ref = 'TEST-PROD-001') ORDER BY date_fabrication ASC;

-- Vérifier le client
-- SELECT id, nom, prenom, numero_telephone FROM client WHERE nom = 'Client' AND prenom = 'Test';

-- Vérifier les modes de paiement
-- SELECT id, libelle FROM mode_paiement ORDER BY id;

-- Vérifier l'historique des prix
-- SELECT id, ancien_prix, nouveau_prix, date_modification FROM historique_prix_produit WHERE id_produit = (SELECT id FROM produit WHERE ref = 'PROD-POULET-001') ORDER BY date_modification DESC;
