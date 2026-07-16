--
-- SCRIPT DE PURGE + CORRECTION DES SÉQUENCES - Vinaakoho
-- Supprime toutes les données et réinitialise les séquences
-- À exécuter AVANT le script d'insertion des données de test
--

-- ============================================
-- 1. DÉSACTIVER TEMPORAIREMENT LES CONTRAINTES
-- ============================================

-- Désactiver les triggers de vérification des clés étrangères
SET session_replication_role = 'replica';

-- ============================================
-- 2. SUPPRESSION DES DONNÉES (ordre inverse des dépendances)
-- ============================================

-- Supprimer d'abord les données enfants (les plus dépendantes)
TRUNCATE TABLE public.ligne_vente_lot CASCADE;
TRUNCATE TABLE public.ligne_vente CASCADE;
TRUNCATE TABLE public.vente CASCADE;
TRUNCATE TABLE public.facture CASCADE;
TRUNCATE TABLE public.livraison CASCADE;
TRUNCATE TABLE public.historique_statut_livraison CASCADE;
TRUNCATE TABLE public.historique_prix_produit CASCADE;
TRUNCATE TABLE public.historique_prix CASCADE;
TRUNCATE TABLE public.ligne_commande CASCADE;
TRUNCATE TABLE public.commande CASCADE;
TRUNCATE TABLE public.depense_lot CASCADE;
TRUNCATE TABLE public.depense CASCADE;
TRUNCATE TABLE public.fabrication_mp CASCADE;
TRUNCATE TABLE public.fabrication CASCADE;
TRUNCATE TABLE public.mouvement_stock_produit CASCADE;
TRUNCATE TABLE public.mouvement_stock_mp CASCADE;
TRUNCATE TABLE public.lot_produit CASCADE;
TRUNCATE TABLE public.lot_mp CASCADE;
TRUNCATE TABLE public.recette_produit CASCADE;
TRUNCATE TABLE public.produit CASCADE;
TRUNCATE TABLE public.matiere_premiere CASCADE;
TRUNCATE TABLE public.client CASCADE;
TRUNCATE TABLE public.fournisseur CASCADE;
TRUNCATE TABLE public.livreur CASCADE;
TRUNCATE TABLE public.employe CASCADE;

-- Supprimer les tables de référence (les moins dépendantes)
TRUNCATE TABLE public.role CASCADE;
TRUNCATE TABLE public.unite CASCADE;
TRUNCATE TABLE public.service CASCADE;
TRUNCATE TABLE public.type_client CASCADE;
TRUNCATE TABLE public.mode_paiement CASCADE;
TRUNCATE TABLE public.phase CASCADE;
TRUNCATE TABLE public.statut_commande CASCADE;
TRUNCATE TABLE public.statut_vente CASCADE;
TRUNCATE TABLE public.statut_livraison CASCADE;
TRUNCATE TABLE public.statut_depense CASCADE;
TRUNCATE TABLE public.categorie_depense CASCADE;
TRUNCATE TABLE public.type_mouvement CASCADE;
TRUNCATE TABLE public.type_prix CASCADE;
TRUNCATE TABLE public.zone_livraison CASCADE;
TRUNCATE TABLE public.categorie CASCADE;

-- ============================================
-- 3. RÉACTIVER LES CONTRAINTES
-- ============================================

-- Réactiver les triggers de vérification des clés étrangères
SET session_replication_role = 'origin';

-- ============================================
-- 4. RÉINITIALISER LES SÉQUENCES
-- ============================================

-- Réinitialisation automatique de toutes les séquences
DO $$
DECLARE
    seq_record RECORD;
    max_id bigint;
    seq_name text;
    table_name text;
    has_data boolean;
BEGIN
    RAISE NOTICE '=== DÉBUT DE LA RÉINITIALISATION DES SÉQUENCES ===';
    
    FOR seq_record IN 
        SELECT 
            pg_class.relname as seq_name,
            pg_namespace.nspname as schema_name
        FROM pg_class 
        JOIN pg_namespace ON pg_namespace.oid = pg_class.relnamespace
        WHERE pg_class.relkind = 'S' 
        AND pg_namespace.nspname = 'public'
        ORDER BY pg_class.relname
    LOOP
        seq_name := seq_record.seq_name;
        
        -- Essayer de déterminer la table associée
        IF seq_name LIKE '%_id_seq' THEN
            table_name := replace(seq_name, '_id_seq', '');
            
            -- Vérifier si la table existe
            PERFORM 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = table_name;
            IF FOUND THEN
                -- Vérifier si la table a des données
                EXECUTE format('SELECT EXISTS (SELECT 1 FROM public.%I LIMIT 1)', table_name) INTO has_data;
                
                IF has_data THEN
                    -- Récupérer le max ID
                    EXECUTE format('SELECT COALESCE(MAX(id), 0) FROM public.%I', table_name) INTO max_id;
                    -- Mettre à jour la séquence avec la valeur max + 1
                    EXECUTE format('SELECT setval(%L, %s, true)', seq_name, max_id + 1);
                    RAISE NOTICE '✅ Séquence % mise à jour à % (max_id: %)', seq_name, max_id + 1, max_id;
                ELSE
                    -- Table vide, réinitialiser à 1
                    EXECUTE format('SELECT setval(%L, 1, false)', seq_name);
                    RAISE NOTICE 'ℹ️ Séquence % réinitialisée à 1 (table vide)', seq_name;
                END IF;
            ELSE
                -- Table non trouvée, réinitialiser à 1 par défaut
                EXECUTE format('SELECT setval(%L, 1, false)', seq_name);
                RAISE NOTICE '⚠️ Séquence % réinitialisée à 1 (table % non trouvée)', seq_name, table_name;
            END IF;
        END IF;
    END LOOP;
    
    RAISE NOTICE '=== FIN DE LA RÉINITIALISATION DES SÉQUENCES ===';
END $$;

-- ============================================
-- 5. RÉINITIALISATION MANUELLE DES SÉQUENCES PRINCIPALES
-- ============================================

-- Réinitialiser à 1 (après purge, toutes les tables sont vides)
SELECT setval('public.vente_id_seq', 1, false);
SELECT setval('public.client_id_seq', 1, false);
SELECT setval('public.commande_id_seq', 1, false);
SELECT setval('public.produit_id_seq', 1, false);
SELECT setval('public.facture_id_seq', 1, false);
SELECT setval('public.livraison_id_seq', 1, false);
SELECT setval('public.depense_id_seq', 1, false);
SELECT setval('public.fournisseur_id_seq', 1, false);
SELECT setval('public.livreur_id_seq', 1, false);
SELECT setval('public.lot_mp_id_seq', 1, false);
SELECT setval('public.lot_produit_id_seq', 1, false);
SELECT setval('public.matiere_premiere_id_seq', 1, false);
SELECT setval('public.employe_id_seq', 1, false);
SELECT setval('public.ligne_commande_id_seq', 1, false);
SELECT setval('public.ligne_vente_id_seq', 1, false);
SELECT setval('public.ligne_vente_lot_id_seq', 1, false);
SELECT setval('public.recette_produit_id_seq', 1, false);
SELECT setval('public.mouvement_stock_mp_id_seq', 1, false);
SELECT setval('public.mouvement_stock_produit_id_seq', 1, false);
SELECT setval('public.fabrication_id_seq', 1, false);
SELECT setval('public.fabrication_mp_id_seq', 1, false);
SELECT setval('public.depense_lot_id_seq', 1, false);
SELECT setval('public.historique_prix_id_seq', 1, false);
SELECT setval('public.historique_prix_produit_id_seq', 1, false);
SELECT setval('public.historique_statut_livraison_id_seq', 1, false);
SELECT setval('public.categorie_depense_id_seq', 1, false);
SELECT setval('public.categorie_id_seq', 1, false);
SELECT setval('public.phase_id_seq', 1, false);
SELECT setval('public.role_id_seq', 1, false);
SELECT setval('public.service_id_seq', 1, false);
SELECT setval('public.statut_commande_id_seq', 1, false);
SELECT setval('public.statut_depense_id_seq', 1, false);
SELECT setval('public.statut_livraison_id_seq', 1, false);
SELECT setval('public.statut_vente_id_seq', 1, false);
SELECT setval('public.type_client_id_seq', 1, false);
SELECT setval('public.type_mouvement_id_seq', 1, false);
SELECT setval('public.type_prix_id_seq', 1, false);
SELECT setval('public.unite_id_seq', 1, false);

-- ============================================
-- 6. VÉRIFICATION FINALE
-- ============================================

DO $$
DECLARE
    table_name text;
    row_count bigint;
    seq_name text;
    seq_value bigint;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE '=== VÉRIFICATION DES TABLES VIDES ===';
    RAISE NOTICE '========================================';
    
    FOR table_name IN (
        SELECT tablename 
        FROM pg_tables 
        WHERE schemaname = 'public' 
        ORDER BY tablename
    ) LOOP
        EXECUTE format('SELECT COUNT(*) FROM public.%I', table_name) INTO row_count;
        IF row_count > 0 THEN
            RAISE NOTICE '⚠️ Table % contient % lignes', table_name, row_count;
        ELSE
            RAISE NOTICE '✅ Table % est vide', table_name;
        END IF;
    END LOOP;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE '=== VÉRIFICATION DES SÉQUENCES ===';
    RAISE NOTICE '========================================';
    
    FOR seq_name IN (
        SELECT relname 
        FROM pg_class 
        WHERE relkind = 'S' 
        AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')
        ORDER BY relname
    ) LOOP
        EXECUTE format('SELECT currval(%L)', seq_name) INTO seq_value;
        RAISE NOTICE '✅ Séquence % = %', seq_name, seq_value;
    END LOOP;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE '=== FIN DE LA VÉRIFICATION ===';
    RAISE NOTICE '========================================';
END $$;

-- ============================================
-- FIN DU SCRIPT DE PURGE + CORRECTION
-- ============================================



-- ============================================
-- SCRIPT D'INSERTION SANS IDs EXPLICITES (colonnes SERIAL)
-- IMPORTANT : ce script doit être exécuté DANS L'ORDRE sur des tables VIDES.
-- Les clés étrangères ci-dessous supposent que PostgreSQL générera les ids
-- 1, 2, 3... dans le même ordre que les lignes insérées ici.
-- Si les tables ne sont pas vides, faire d'abord :
--   TRUNCATE TABLE <table> RESTART IDENTITY CASCADE;
-- ============================================

-- ============================================
-- 2. TABLES DE RÉFÉRENCE (INSERT dans le bon ordre)
-- ============================================

-- UNITE
INSERT INTO public.unite (libelle, created_at, updated_at) VALUES
('kg', NOW(), NOW()),
('g', NOW(), NOW()),
('L', NOW(), NOW()),
('unité', NOW(), NOW());

-- ROLE
INSERT INTO public.role (poste, created_at) VALUES
('Administrateur', NOW()),
('Responsable de production', NOW()),
('Responsable commercial', NOW());

-- EMPLOYE
INSERT INTO public.employe (nom, prenom, email, contact, mdp, id_role, created_at, updated_at, actif, derniere_connexion) VALUES
('Admin', 'Sys', 'admin@vinaakoho.mg', NULL, '$2a$10$XwFIxBeDJqCX30E6XRe3IOMM6ZncY8.kDRMjzpDLr4H2kpCzIzv8O', 1, NOW(), NOW(), true, NULL),
('Production', 'Responsable', 'production@vinaakoho.mg', NULL, '$2a$10$sBbJUwXjaFJ0eehrCOMERelyjcHRt2piX2jl8FTYO8MLqJJgASXYm', 2, NOW(), NOW(), true, NULL),
('Commercial', 'Responsable', 'commercial@vinaakoho.mg', NULL, '$2a$10$97H3BldJA5InSJmrQDaMOew.75kl52Wkl35N5.hv2COl.CoEuzvm.', 3, NOW(), NOW(), true, NULL);

-- SERVICE
INSERT INTO public.service (libelle, description, created_at, updated_at) VALUES
('Vente', 'Service de vente aux clients', NOW(), NOW()),
('Elevage', 'Clients eleveurs et fermes avicoles', NOW(), NOW());

-- TYPE_CLIENT
INSERT INTO public.type_client (libelle, created_at) VALUES
('Eleveur', NOW()),
('Ferme', NOW()),
('Revendeur', NOW()),
('Eleveur familial', NOW());

-- MODE_PAIEMENT
INSERT INTO public.mode_paiement (libelle, created_at, updated_at) VALUES
('Espèces', NOW(), NOW()),
('Transfert bancaire', NOW(), NOW()),
('Mobile money', NOW(), NOW()),
('Especes test', NOW(), NOW()),
('Mobile money test', NOW(), NOW());

-- PHASE
INSERT INTO public.phase (libelle, description, created_at, updated_at) VALUES
('Approvisionnement', 'Achat et reception des matieres premieres', NOW(), NOW()),
('Production', 'Fabrication des aliments', NOW(), NOW()),
('Stockage', 'Gestion des stocks de produits finis', NOW(), NOW()),
('Commercialisation', 'Vente et livraison aux clients', NOW(), NOW());

-- STATUT_COMMANDE
INSERT INTO public.statut_commande (libelle, created_at) VALUES
('Validée', NOW()),
('En cours', NOW()),
('Annulée', NOW());

-- STATUT_VENTE
INSERT INTO public.statut_vente (libelle, created_at) VALUES
('Validée', NOW()),
('Annulée', NOW()),
('En attente', NOW()),
('En attente de paiement', NOW()),
('En préparation', NOW()),
('En livraison', NOW()),
('Livrée', NOW());

-- STATUT_LIVRAISON
INSERT INTO public.statut_livraison (libelle, created_at) VALUES
('En attente d''affectation', NOW()),
('En cours de livraison', NOW()),
('Livrée', NOW()),
('Échec de livraison', NOW());

-- STATUT_DEPENSE
INSERT INTO public.statut_depense (libelle, created_at) VALUES
('En attente', NOW()),
('Validée', NOW()),
('Payée', NOW()),
('Rejetée', NOW());

-- CATEGORIE_DEPENSE
INSERT INTO public.categorie_depense (libelle, created_at) VALUES
('Achat matieres premieres', NOW()),
('Salaires', NOW()),
('Transport', NOW()),
('Entretien equipement', NOW()),
('Marketing', NOW()),
('Frais administratifs', NOW());

-- TYPE_MOUVEMENT
INSERT INTO public.type_mouvement (libelle, created_at) VALUES
('Entree', NOW()),
('Sortie', NOW());

-- TYPE_PRIX
INSERT INTO public.type_prix (libelle, created_at) VALUES
('Prix de vente', NOW()),
('Prix d''achat', NOW()),
('Prix promotionnel', NOW());

-- ZONE_LIVRAISON (clé texte, PAS serial -> id conservé)
INSERT INTO public.zone_livraison (id, libelle) VALUES
('ITAOSY', 'Itaosy'),
('MAHITSY', 'Mahitsy'),
('AMBOHIDRATRIMO', 'Ambohidratrimo'),
('ANKAZOBE', 'Ankazobe'),
('ZONE-VTE-01', 'Zone Vente 01');

-- CATEGORIE
INSERT INTO public.categorie (libelle, description, pourcentage_proteine, pourcentage_matiere_grasses, pourcentage_humidite_max, marge_pourcentage, created_at, updated_at, actif) VALUES
('Poussin', 'Aliment pour poussins', 22.00, 5.00, 12.00, 20.00, NOW(), NOW(), true),
('Croissance', 'Aliment croissance', 19.00, 4.00, 12.00, 18.00, NOW(), NOW(), true),
('Finition', 'Aliment finition', 17.00, 3.00, 12.00, 15.00, NOW(), NOW(), true);

-- ============================================
-- 3. FOURNISSEURS
-- ============================================

INSERT INTO public.fournisseur (nom, email, telephone, created_at, updated_at) VALUES
('AGRIVET Madagascar', 'contact@agrivet.mg', '0341234567', NOW(), NOW()),
('Sonapo Madagascar', 'contact@sonapo.mg', '0331122334', NOW(), NOW()),
('Agro Import Tana', 'contact@agroimport.mg', '0324455667', NOW(), NOW()),
('Nourriments Vakinankaratra', 'contact@nourriments.mg', '0349876543', NOW(), NOW()),
('Madagascar Protein', 'info@madaprotein.mg', '0323456789', NOW(), NOW()),
('Grains d''Or Tana', 'contact@grainsdor.mg', '0338765432', NOW(), NOW()),
('Vitaminex Madagascar', 'commercial@vitaminex.mg', '0345671234', NOW(), NOW());

-- ============================================
-- 4. MATIERES PREMIERES
-- ============================================

INSERT INTO public.matiere_premiere (code, nom, id_fournisseur, cout_unitaire, id_unite, seuil_minimum, created_at, updated_at) VALUES
('MP001', 'Mais jaune', 1, 1200.00, 1, 100.00, NOW(), NOW()),
('MP002', 'Son de riz', 1, 900.00, 1, 80.00, NOW(), NOW()),
('MP003', 'Tourteau de soja', 2, 2500.00, 1, 50.00, NOW(), NOW()),
('MP004', 'Farine de poisson', 2, 4000.00, 1, 20.00, NOW(), NOW()),
('MP005', 'Premix vitamines', 3, 8000.00, 1, 5.00, NOW(), NOW()),
('MP006', 'Calcaire', 1, 400.00, 1, 50.00, NOW(), NOW()),
('MP007', 'Sel', 3, 600.00, 1, 20.00, NOW(), NOW()),
('MP-DASH-01', 'Mais grain Dash', 1, 1100.00, 1, 200.00, NOW(), NOW()),
('MP-DASH-02', 'Tourteau soja Dash', 1, 2400.00, 1, 100.00, NOW(), NOW()),
('MP-DASH-03', 'Farine poisson Dash', 1, 3900.00, 1, 50.00, NOW(), NOW()),
('MP-DASH-04', 'Premix mineral Dash', 1, 8500.00, 1, 10.00, NOW(), NOW()),
('MP-DASH-05', 'Ble tendre Dash', 1, 1000.00, 1, 150.00, NOW(), NOW());

-- ============================================
-- 5. PRODUITS
-- ============================================

INSERT INTO public.produit (ref, id_categorie, nom, prix_vente, seuil_alerte, description, created_at, updated_at, actif, id_unite) VALUES
('PRD-001', 1, 'Aliment Poussin 10kg', 25000.00, 10, 'Aliment demarrage riche en proteines pour poussins', NOW(), NOW(), true, 4),
('PRD-PF-001', 1, 'Aliment Poussin 10kg Evolution', 28000.00, 200, 'Formule evolution pour poussins', NOW(), NOW(), true, 4),
('PRD-PF-004', 1, 'Aliment Poussin 25kg Debut', 97000.00, 150, 'Formule demarrage grand conditionnement', NOW(), NOW(), true, 4),
('TST-REC-001', 2, 'Aliment Croissance 50kg', 125000.00, 5, 'Aliment croissance standard', NOW(), NOW(), true, 4),
('PRD-PF-002', 2, 'Aliment Croissance 50kg Premium', 132000.00, 500, 'Formule premium croissance', NOW(), NOW(), true, 4),
('PRD-PF-005', 2, 'Aliment Croissance 10kg Compact', 28500.00, 300, 'Petit conditionnement croissance', NOW(), NOW(), true, 4),
('TST-REC-002', 3, 'Aliment Finition 50kg', 118000.00, 5, 'Aliment finition standard', NOW(), NOW(), true, 4),
('PRD-PF-003', 3, 'Aliment Finition 50kg Excellence', 132500.00, 500, 'Formule excellence finition', NOW(), NOW(), true, 4);

-- ============================================
-- 6. RECETTES PRODUITS (inchangé - pas de colonne id explicite ici)
-- ============================================

INSERT INTO public.recette_produit (id_categorie, version, id_mp, quantite_mp, id_unite, is_active, date_creation, date_fin, id_employe_creation) VALUES
(1, 1, 1, 2.00, 1, true, NOW(), NULL, 1),
(1, 1, 2, 1.00, 1, true, NOW(), NULL, 1),
(1, 1, 5, 0.05, 1, true, NOW(), NULL, 1),
(2, 1, 1, 1.80, 1, true, NOW(), NULL, 1),
(2, 1, 2, 1.20, 1, true, NOW(), NULL, 1),
(2, 1, 3, 0.60, 1, true, NOW(), NULL, 1),
(2, 1, 5, 0.05, 1, true, NOW(), NULL, 1),
(3, 1, 1, 1.50, 1, true, NOW(), NULL, 1),
(3, 1, 2, 1.50, 1, true, NOW(), NULL, 1),
(3, 1, 3, 0.80, 1, true, NOW(), NULL, 1),
(3, 1, 4, 0.20, 1, true, NOW(), NULL, 1),
(3, 1, 5, 0.05, 1, true, NOW(), NULL, 1);

-- ============================================
-- 7. CLIENTS
-- ============================================

INSERT INTO public.client (nom, prenom, date_inscription, is_actif, numero_telephone, adresse, id_localite, id_zone_livraison, notes, id_service, id_typeclient, taille_cheptel, est_supprimer, created_at, updated_at) VALUES
('Rasoa', 'Marie', '2026-07-01', true, '0341234567', 'Lot 123 Quartier Itaosy, Antananarivo', 'Itaosy', 'ITAOSY', NULL, 1, 1, NULL, false, NOW(), NOW()),
('Rakoto', 'Jean', '2026-07-01', true, '0342345678', 'Zone industrielle Mahitsy, Antananarivo', 'Mahitsy', 'MAHITSY', NULL, 1, 1, NULL, false, NOW(), NOW()),
('Randria', 'Fara', '2026-07-01', true, '0343456789', 'Ambohidratrimo, Antananarivo', 'Ambohidratrimo', 'AMBOHIDRATRIMO', NULL, 1, 1, NULL, false, NOW(), NOW()),
('Ravelo', 'Paul', '2026-07-01', true, '0344567890', 'Village Ankazobe, Antananarivo', 'Ankazobe', 'ANKAZOBE', NULL, 1, 1, NULL, false, NOW(), NOW()),
('Andriamanitra', 'Luc', '2026-07-01', true, '0345678901', 'Antananarivo', 'Antananarivo', NULL, NULL, 1, 1, NULL, false, NOW(), NOW()),
('Rakoto', 'Test Recette', '2026-07-01', true, '0340000001', 'Mahitsy', 'Mahitsy', 'MAHITSY', 'Client test recette', 2, 2, 250, false, NOW(), NOW()),
('Rabe', 'Test Recette', '2026-07-01', true, '0340000002', 'Ankazobe', 'Ankazobe', 'ANKAZOBE', 'Client test recette', 2, 3, 120, false, NOW(), NOW()),
('Ramanantsoa', 'Maminiaina', '2026-07-10', true, '0347890123', 'Lot 45 Ambohimanga', 'Ambohidratrimo', 'AMBOHIDRATRIMO', 'Ferme avicole 500 poules', 2, 2, 500, false, NOW(), NOW()),
('Rakotomalala', 'Fidiniaina', '2026-07-12', true, '0334567890', 'Zone Andranobevava', 'Itaosy', 'ITAOSY', 'Elevage de poulets de chair', 1, 1, 800, false, NOW(), NOW()),
('Andriantsoa', 'Mamy', '2026-07-14', true, '0348901234', 'Lot 56 Ankazobe', 'Ankazobe', 'ANKAZOBE', 'Revendeur de produits avicoles', 1, 3, NULL, false, NOW(), NOW());

-- ============================================
-- 8. LOTS MP (avec séquence des mouvements)
-- ============================================

-- D'abord créer les lots sans id_mouvement_entree
INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire) VALUES
(1, 40.00, 40.00, '2026-01-10', '2026-12-31', NULL, NOW(), 1, 1200.00),
(1, 100.00, 100.00, '2026-03-05', '2026-12-31', NULL, NOW(), 1, 1200.00),
(2, 50.00, 50.00, '2026-02-01', '2026-12-31', NULL, NOW(), 1, 900.00),
(1, 50.00, 50.00, '2026-06-01', '2026-12-31', NULL, NOW(), 1, 1200.00),
(1, 100.00, 100.00, '2026-06-25', '2026-12-31', NULL, NOW(), 1, 1200.00),
(2, 50.00, 50.00, '2026-06-10', '2026-12-31', NULL, NOW(), 1, 900.00),
(3, 30.00, 30.00, '2026-07-10', '2026-12-31', NULL, NOW(), 2, 2500.00),
(8, 1000.00, 600.00, '2026-01-20', '2026-08-20', NULL, NOW(), 1, 1100.00),
(8, 500.00, 200.00, '2026-04-12', '2026-07-25', NULL, NOW(), 1, 1100.00),
(9, 400.00, 300.00, '2026-03-01', '2026-09-01', NULL, NOW(), 1, 2400.00),
(9, 300.00, 150.00, '2026-05-15', '2026-07-30', NULL, NOW(), 1, 2400.00),
(10, 200.00, 120.00, '2026-02-10', '2026-10-10', NULL, NOW(), 1, 3900.00),
(11, 100.00, 5.00, '2026-06-01', '2026-07-20', NULL, NOW(), 1, 8500.00),
(12, 800.00, 600.00, '2026-01-15', '2027-01-15', NULL, NOW(), 1, 1000.00),
(12, 600.00, 450.00, '2026-04-20', '2026-08-05', NULL, NOW(), 1, 1000.00),
(1, 500.00, 480.00, '2026-07-10', '2026-12-31', NULL, NOW(), 1, 1200.00),
(3, 200.00, 190.00, '2026-07-12', '2026-12-31', NULL, NOW(), 2, 2500.00),
(8, 1500.00, 1450.00, '2026-07-15', '2026-08-20', NULL, NOW(), 1, 1100.00);

-- Puis créer les mouvements de stock pour chaque lot
INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at) VALUES
(1, '2026-01-10', 1, 40.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-1', NOW()),
(1, '2026-03-05', 2, 100.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-2', NOW()),
(1, '2026-02-01', 3, 50.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-3', NOW()),
(1, '2026-06-01', 4, 50.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-4', NOW()),
(1, '2026-06-25', 5, 100.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-5', NOW()),
(1, '2026-06-10', 6, 50.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-6', NOW()),
(1, '2026-07-10', 7, 30.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-7', NOW()),
(1, '2026-01-20', 8, 1000.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-8', NOW()),
(1, '2026-04-12', 9, 500.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-9', NOW()),
(1, '2026-03-01', 10, 400.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-10', NOW()),
(1, '2026-05-15', 11, 300.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-11', NOW()),
(1, '2026-02-10', 12, 200.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-12', NOW()),
(1, '2026-06-01', 13, 100.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-13', NOW()),
(1, '2026-01-15', 14, 800.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-14', NOW()),
(1, '2026-04-20', 15, 600.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-15', NOW()),
(1, '2026-07-10', 16, 500.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-16', NOW()),
(1, '2026-07-12', 17, 200.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-17', NOW()),
(1, '2026-07-15', 18, 1500.00, 1, 1, 'Reception lot matiere premiere', 'ACHAT-MP-18', NOW());

-- Mettre à jour les lots avec leur mouvement d'entrée
-- (les ids générés par les 2 séquences ci-dessus avancent en parallèle, 1 à 1)
UPDATE public.lot_mp SET id_mouvement_entree = 1 WHERE id = 1;
UPDATE public.lot_mp SET id_mouvement_entree = 2 WHERE id = 2;
UPDATE public.lot_mp SET id_mouvement_entree = 3 WHERE id = 3;
UPDATE public.lot_mp SET id_mouvement_entree = 4 WHERE id = 4;
UPDATE public.lot_mp SET id_mouvement_entree = 5 WHERE id = 5;
UPDATE public.lot_mp SET id_mouvement_entree = 6 WHERE id = 6;
UPDATE public.lot_mp SET id_mouvement_entree = 7 WHERE id = 7;
UPDATE public.lot_mp SET id_mouvement_entree = 8 WHERE id = 8;
UPDATE public.lot_mp SET id_mouvement_entree = 9 WHERE id = 9;
UPDATE public.lot_mp SET id_mouvement_entree = 10 WHERE id = 10;
UPDATE public.lot_mp SET id_mouvement_entree = 11 WHERE id = 11;
UPDATE public.lot_mp SET id_mouvement_entree = 12 WHERE id = 12;
UPDATE public.lot_mp SET id_mouvement_entree = 13 WHERE id = 13;
UPDATE public.lot_mp SET id_mouvement_entree = 14 WHERE id = 14;
UPDATE public.lot_mp SET id_mouvement_entree = 15 WHERE id = 15;
UPDATE public.lot_mp SET id_mouvement_entree = 16 WHERE id = 16;
UPDATE public.lot_mp SET id_mouvement_entree = 17 WHERE id = 17;
UPDATE public.lot_mp SET id_mouvement_entree = 18 WHERE id = 18;

-- ============================================
-- 9. LOTS PRODUITS (avec séquence des mouvements)
-- ============================================

-- D'abord créer les lots sans id_mouvement_entree
INSERT INTO public.lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_peremption, id_mouvement_entree, created_at) VALUES
(1, 3000.00, 2800.00, '2026-06-05', '2026-12-05', NULL, NOW()),
(1, 1500.00, 1500.00, '2026-07-05', '2027-01-05', NULL, NOW()),
(4, 2500.00, 2200.00, '2026-06-19', '2026-12-19', NULL, NOW()),
(7, 2000.00, 1800.00, '2026-06-24', '2026-12-24', NULL, NOW()),
(2, 2000.00, 1500.00, '2026-06-10', '2026-09-10', NULL, NOW()),
(2, 1000.00, 800.00, '2026-07-01', '2026-10-01', NULL, NOW()),
(2, 500.00, 500.00, '2026-07-06', '2026-10-06', NULL, NOW()),
(5, 3000.00, 2000.00, '2026-05-15', '2026-12-15', NULL, NOW()),
(5, 1000.00, 0.00, '2026-04-20', '2026-11-20', NULL, NOW()),
(8, 2500.00, 1500.00, '2026-06-25', '2026-12-25', NULL, NOW()),
(3, 500.00, 300.00, '2026-07-05', '2026-10-05', NULL, NOW()),
(6, 800.00, 600.00, '2026-07-08', '2027-01-08', NULL, NOW()),
(1, 2000.00, 2000.00, '2026-07-12', '2027-01-12', NULL, NOW()),
(2, 1500.00, 1500.00, '2026-07-13', '2026-10-13', NULL, NOW()),
(4, 1000.00, 1000.00, '2026-07-14', '2027-01-14', NULL, NOW());

-- Puis créer les mouvements de stock pour chaque lot
INSERT INTO public.mouvement_stock_produit (id_type_mouvement, date_mouvement, id_lot_produit, quantite, id_unite, id_employe, observation, reference_document, created_at) VALUES
(1, '2026-06-05', 1, 3000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-1', NOW()),
(1, '2026-07-05', 2, 1500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-2', NOW()),
(1, '2026-06-19', 3, 2500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-3', NOW()),
(1, '2026-06-24', 4, 2000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-4', NOW()),
(1, '2026-06-10', 5, 2000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-5', NOW()),
(1, '2026-07-01', 6, 1000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-6', NOW()),
(1, '2026-07-06', 7, 500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-7', NOW()),
(1, '2026-05-15', 8, 3000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-8', NOW()),
(1, '2026-04-20', 9, 1000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-9', NOW()),
(1, '2026-06-25', 10, 2500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-10', NOW()),
(1, '2026-07-05', 11, 500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-11', NOW()),
(1, '2026-07-08', 12, 800.00, 4, 2, 'Entree lot produit', 'FAB-PRD-12', NOW()),
(1, '2026-07-12', 13, 2000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-13', NOW()),
(1, '2026-07-13', 14, 1500.00, 4, 2, 'Entree lot produit', 'FAB-PRD-14', NOW()),
(1, '2026-07-14', 15, 1000.00, 4, 2, 'Entree lot produit', 'FAB-PRD-15', NOW());

-- Mettre à jour les lots avec leur mouvement d'entrée
UPDATE public.lot_produit SET id_mouvement_entree = 1 WHERE id = 1;
UPDATE public.lot_produit SET id_mouvement_entree = 2 WHERE id = 2;
UPDATE public.lot_produit SET id_mouvement_entree = 3 WHERE id = 3;
UPDATE public.lot_produit SET id_mouvement_entree = 4 WHERE id = 4;
UPDATE public.lot_produit SET id_mouvement_entree = 5 WHERE id = 5;
UPDATE public.lot_produit SET id_mouvement_entree = 6 WHERE id = 6;
UPDATE public.lot_produit SET id_mouvement_entree = 7 WHERE id = 7;
UPDATE public.lot_produit SET id_mouvement_entree = 8 WHERE id = 8;
UPDATE public.lot_produit SET id_mouvement_entree = 9 WHERE id = 9;
UPDATE public.lot_produit SET id_mouvement_entree = 10 WHERE id = 10;
UPDATE public.lot_produit SET id_mouvement_entree = 11 WHERE id = 11;
UPDATE public.lot_produit SET id_mouvement_entree = 12 WHERE id = 12;
UPDATE public.lot_produit SET id_mouvement_entree = 13 WHERE id = 13;
UPDATE public.lot_produit SET id_mouvement_entree = 14 WHERE id = 14;
UPDATE public.lot_produit SET id_mouvement_entree = 15 WHERE id = 15;

-- ============================================
-- 10. FABRICATION
-- ============================================

INSERT INTO public.fabrication (date_fabrication, quantite_produite, id_lot_produit, id_employe, created_at) VALUES
('2026-07-06 07:30:00', 500.00, 7, 2, NOW()),
('2026-07-12 08:00:00', 2000.00, 13, 2, NOW()),
('2026-07-13 09:30:00', 1500.00, 14, 2, NOW()),
('2026-07-14 07:45:00', 1000.00, 15, 2, NOW());

-- ============================================
-- 11. LIVREUR
-- ============================================

INSERT INTO public.livreur (nom, prenom, email, contact, created_at, updated_at) VALUES
('Ranaivo', 'Livreur', 'livreur@vinaakoho.mg', '0321000001', NOW(), NOW());

-- ============================================
-- 12. COMMANDES
-- ============================================

INSERT INTO public.commande (date_commande, id_client, id_statut_commande, commentaire, created_at, updated_at) VALUES
('2026-06-18 10:00:00', 1, 1, 'Commande pilote pour client Itaosy', NOW(), NOW()),
('2026-06-20 14:30:00', 2, 2, 'Commande en preparation', NOW(), NOW()),
('2026-06-22 08:45:00', 3, 3, 'Commande annulee', NOW(), NOW()),
('2026-07-08 09:00:00', 1, 2, 'Commande de 15 sacs aliment poussin', NOW(), NOW()),
('2026-07-09 14:30:00', 2, 1, 'Urgent - 5 sacs croissance 50kg', NOW(), NOW()),
('2026-07-11 11:15:00', 3, 3, 'Annulee par le client', NOW(), NOW()),
('2026-07-13 08:45:00', 8, 2, 'Commande ferme avicole', NOW(), NOW());

-- LIGNES DE COMMANDE
INSERT INTO public.ligne_commande (id_commande, id_produit, quantite, prix_unitaire) VALUES
(1, 1, 10.00, 25000.00),
(1, 2, 5.00, 28000.00),
(2, 6, 12.00, 32000.00),
(2, 4, 3.00, 125000.00),
(3, 8, 2.00, 122000.00),
(4, 1, 15.00, 25000.00),
(4, 2, 5.00, 28000.00),
(5, 4, 5.00, 125000.00),
(7, 1, 10.00, 25000.00);

-- ============================================
-- 13. VENTES
-- ============================================

INSERT INTO public.vente (date_vente, montant_total, id_mode_paiement, id_statut_vente, created_at, updated_at, id_client) VALUES
('2026-07-10 08:30:00', 375000.00, 1, 7, NOW(), NOW(), 1),
('2026-07-12 14:15:00', 432000.00, 3, 6, NOW(), NOW(), 2),
('2026-07-13 09:45:00', 250000.00, 2, 5, NOW(), NOW(), 3),
('2026-07-15 10:00:00', 118000.00, 1, 1, NOW(), NOW(), 4),
('2026-07-11 16:20:00', 560000.00, 3, 7, NOW(), NOW(), 5),
('2026-07-14 11:30:00', 320000.00, 1, 3, NOW(), NOW(), 8),
('2026-07-14 15:00:00', 890000.00, 2, 4, NOW(), NOW(), 9);

-- LIGNES DE VENTE
INSERT INTO public.ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant) VALUES
(1, 1, 10, 25000.00, 250000.00),
(1, 2, 5, 25000.00, 125000.00),
(2, 4, 3, 125000.00, 375000.00),
(2, 6, 2, 28500.00, 57000.00),
(3, 5, 2, 125000.00, 250000.00),
(4, 7, 1, 118000.00, 118000.00),
(5, 8, 3, 122000.00, 366000.00),
(5, 3, 2, 97000.00, 194000.00),
(6, 1, 8, 25000.00, 200000.00),
(6, 6, 5, 24000.00, 120000.00),
(7, 4, 5, 125000.00, 625000.00),
(7, 8, 2, 132500.00, 265000.00);

-- ============================================
-- 14. FACTURES
-- ============================================

INSERT INTO public.facture (id_vente, numero, date_emission, montant_ht, taux_tva, montant_tva, montant_ttc, created_at, remise) VALUES
(1, 'FACT-2026-001', '2026-07-10', 375000.00, 5.00, 18750.00, 393750.00, NOW(), 0),
(4, 'FACT-2026-004', '2026-07-15', 118000.00, 5.00, 5900.00, 123900.00, NOW(), 0),
(5, 'FACT-2026-005', '2026-07-11', 560000.00, 5.00, 28000.00, 588000.00, NOW(), 0),
(6, 'FACT-2026-006', '2026-07-14', 320000.00, 5.00, 16000.00, 336000.00, NOW(), 0);

-- ============================================
-- 15. LIVRAISONS
-- ============================================

INSERT INTO public.livraison (id_vente, id_livreur, lieu_exact, contact, date_livraison, commentaire, id_statut_livraison, created_at, updated_at, id_zone_livraison) VALUES
(1, 1, 'Lot 123 Quartier Itaosy, Antananarivo', '0341234567', '2026-07-10', 'Livraison effectuee avec succes', 3, NOW(), NOW(), 'ITAOSY'),
(2, 1, 'Zone industrielle Mahitsy, Antananarivo', '0342345678', '2026-07-13', 'En cours de livraison', 2, NOW(), NOW(), 'MAHITSY'),
(5, 1, 'Antananarivo', '0345678901', '2026-07-11', 'Livraison recue par le client', 3, NOW(), NOW(), NULL),
(6, 1, 'Lot 45 Ambohimanga', '0347890123', '2026-07-16', 'Prevue le 16 juillet', 1, NOW(), NOW(), 'AMBOHIDRATRIMO');

-- ============================================
-- 16. DEPENSES
-- ============================================

INSERT INTO public.depense (date, designation, id_categorie_depense, id_phase, montant, id_statut_depense, created_at, updated_at) VALUES
('2026-07-05', 'Achat matieres premieres pour production', 1, 1, 420000.00, 2, NOW(), NOW()),
('2026-07-09', 'Transport livraison zone Itaosy', 3, 4, 50000.00, 3, NOW(), NOW()),
('2026-07-10', 'Maintenance melangeur', 4, 2, 120000.00, 1, NOW(), NOW()),
('2026-07-12', 'Achat supplement de mais et soja', 1, 1, 350000.00, 2, NOW(), NOW()),
('2026-07-14', 'Salaire employes production - Juillet', 2, 2, 2800000.00, 2, NOW(), NOW()),
('2026-07-11', 'Campagne marketing digitale', 5, 4, 150000.00, 3, NOW(), NOW()),
('2026-07-15', 'Carburant livraison zone Mahitsy', 3, 4, 45000.00, 1, NOW(), NOW());

-- DEPENSE_LOT
INSERT INTO public.depense_lot (id_depense, id_lot_produit, id_lot_mp) VALUES
(1, NULL, 1),
(2, 5, NULL),
(4, NULL, 16),
(4, NULL, 17);

-- ============================================
-- 17. HISTORIQUE PRIX
-- ============================================

INSERT INTO public.historique_prix (id_produit, id_type_prix, ancien_prix, nouveau_prix, date_modification, id_employe) VALUES
(1, 1, 24000.00, 25000.00, NOW(), 1),
(5, 1, 128000.00, 132000.00, NOW(), 1);

INSERT INTO public.historique_prix_produit (id_produit, ancien_prix, nouveau_prix, date_modification, id_employe) VALUES
(1, 24000.00, 25000.00, NOW(), 1),
(5, 128000.00, 132000.00, NOW(), 1),
(8, 118000.00, 122000.00, NOW(), 1);

-- ============================================
-- 18. HISTORIQUE STATUT LIVRAISON
-- ============================================

INSERT INTO public.historique_statut_livraison (id_livraison, ancien_statut, nouveau_statut, date_changement, created_at) VALUES
(1, 1, 3, '2026-07-10 10:00:00', NOW()),
(2, 1, 2, '2026-07-13 14:00:00', NOW());

-- ============================================
-- 19. FABRICATION_MP
-- ============================================

INSERT INTO public.fabrication_mp (id_fabrication, id_lot_mp, quantite, id_unite) VALUES
(1, 1, 10.00, 1),
(1, 2, 5.00, 1),
(1, 3, 3.00, 1);

-- ============================================
-- 20. MISE À JOUR DES PRIX DES PRODUITS
-- ============================================

UPDATE public.produit SET prix_vente = 25000.00 WHERE id = 1;
UPDATE public.produit SET prix_vente = 132000.00 WHERE id = 5;
UPDATE public.produit SET prix_vente = 122000.00 WHERE id = 8;
UPDATE public.produit SET prix_vente = 28500.00 WHERE id = 6;
UPDATE public.produit SET prix_vente = 97000.00 WHERE id = 3;
UPDATE public.produit SET prix_vente = 132500.00 WHERE id = 8;

-- ============================================
-- AJOUT DE STOCK SUPPLÉMENTAIRE POUR MATIERES PREMIERES
-- Ce script est SÛR à exécuter même si ta base contient déjà des données
-- (il ne suppose pas que les ids commencent à 1, il récupère les vrais ids
-- générés grâce à RETURNING ... INTO).
--
-- Rappel des id_mp (matiere_premiere) si tu as utilisé le script original :
--   1 = Mais jaune          2 = Son de riz         3 = Tourteau de soja
--   4 = Farine de poisson   5 = Premix vitamines   6 = Calcaire
--   7 = Sel                 8-12 = gamme "Dash"
-- Adapte les valeurs id_mp ci-dessous si tes ids sont différents.
-- ============================================

DO $$
DECLARE
  v_lot_id INT;
  v_mvt_id INT;
BEGIN

  -- ===================== MAIS JAUNE (id_mp = 1) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (1, 3000.00, 3000.00, '2026-07-15', '2027-01-15', NULL, NOW(), 1, 1200.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 3000.00, 1, 1, 'Reapprovisionnement stock mais jaune', 'ACHAT-MP-EXTRA-01', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (1, 2500.00, 2500.00, '2026-07-16', '2027-01-16', NULL, NOW(), 1, 1200.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-16', v_lot_id, 2500.00, 1, 1, 'Reapprovisionnement stock mais jaune', 'ACHAT-MP-EXTRA-02', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== SON DE RIZ (id_mp = 2) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (2, 2000.00, 2000.00, '2026-07-15', '2027-01-15', NULL, NOW(), 1, 900.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 2000.00, 1, 1, 'Reapprovisionnement stock son de riz', 'ACHAT-MP-EXTRA-03', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (2, 1500.00, 1500.00, '2026-07-16', '2027-01-16', NULL, NOW(), 1, 900.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-16', v_lot_id, 1500.00, 1, 1, 'Reapprovisionnement stock son de riz', 'ACHAT-MP-EXTRA-04', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== TOURTEAU DE SOJA (id_mp = 3) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (3, 1500.00, 1500.00, '2026-07-15', '2027-01-15', NULL, NOW(), 2, 2500.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 1500.00, 1, 1, 'Reapprovisionnement stock tourteau de soja', 'ACHAT-MP-EXTRA-05', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (3, 1000.00, 1000.00, '2026-07-16', '2027-01-16', NULL, NOW(), 2, 2500.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-16', v_lot_id, 1000.00, 1, 1, 'Reapprovisionnement stock tourteau de soja', 'ACHAT-MP-EXTRA-06', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== FARINE DE POISSON (id_mp = 4) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (4, 800.00, 800.00, '2026-07-15', '2027-01-15', NULL, NOW(), 2, 4000.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 800.00, 1, 1, 'Reapprovisionnement stock farine de poisson', 'ACHAT-MP-EXTRA-07', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (4, 600.00, 600.00, '2026-07-16', '2027-01-16', NULL, NOW(), 2, 4000.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-16', v_lot_id, 600.00, 1, 1, 'Reapprovisionnement stock farine de poisson', 'ACHAT-MP-EXTRA-08', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== PREMIX VITAMINES (id_mp = 5) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (5, 200.00, 200.00, '2026-07-15', '2027-07-15', NULL, NOW(), 3, 8000.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 200.00, 1, 1, 'Reapprovisionnement stock premix vitamines', 'ACHAT-MP-EXTRA-09', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (5, 150.00, 150.00, '2026-07-16', '2027-07-16', NULL, NOW(), 3, 8000.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-16', v_lot_id, 150.00, 1, 1, 'Reapprovisionnement stock premix vitamines', 'ACHAT-MP-EXTRA-10', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== CALCAIRE (id_mp = 6) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (6, 500.00, 500.00, '2026-07-15', '2027-07-15', NULL, NOW(), 1, 400.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 500.00, 1, 1, 'Reapprovisionnement stock calcaire', 'ACHAT-MP-EXTRA-11', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

  -- ===================== SEL (id_mp = 7) =====================
  INSERT INTO public.lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption, id_mouvement_entree, created_at, id_fournisseur, cout_unitaire)
  VALUES (7, 300.00, 300.00, '2026-07-15', '2027-07-15', NULL, NOW(), 3, 600.00)
  RETURNING id INTO v_lot_id;
  INSERT INTO public.mouvement_stock_mp (id_type_mouvement, date_mouvement, id_lot_mp, quantite, id_unite, id_employe, observation, reference_document, created_at)
  VALUES (1, '2026-07-15', v_lot_id, 300.00, 1, 1, 'Reapprovisionnement stock sel', 'ACHAT-MP-EXTRA-12', NOW())
  RETURNING id INTO v_mvt_id;
  UPDATE public.lot_mp SET id_mouvement_entree = v_mvt_id WHERE id = v_lot_id;

END $$;

-- ============================================
-- FIN DU SCRIPT
-- ============================================