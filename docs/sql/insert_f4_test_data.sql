-- Données de test pour F4 Ventes
-- À exécuter dans la base de données vinakoho

-- Services et types de client nécessaires pour la table client
INSERT INTO service (libelle)
SELECT 'Commercial'
WHERE NOT EXISTS (SELECT 1 FROM service WHERE libelle = 'Commercial');

INSERT INTO service (libelle)
SELECT 'Ventes'
WHERE NOT EXISTS (SELECT 1 FROM service WHERE libelle = 'Ventes');

INSERT INTO type_client (libelle)
SELECT 'Particulier'
WHERE NOT EXISTS (SELECT 1 FROM type_client WHERE libelle = 'Particulier');

INSERT INTO type_client (libelle)
SELECT 'Professionnel'
WHERE NOT EXISTS (SELECT 1 FROM type_client WHERE libelle = 'Professionnel');

-- Catégorie et produit de test
INSERT INTO categorie (libelle, description, pourcentage_proteine, pourcentage_matiere_grasses, pourcentage_humidite_max, marge_pourcentage, actif)
SELECT 'Test Ventes', 'Catégorie de test pour le module F4', 10, 3, 12, 10, true
WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE libelle = 'Test Ventes');

INSERT INTO produit (ref, id_categorie, nom, prix_vente, seuil_alerte, description)
SELECT 'TEST-PROD-001', c.id, 'Produit Test F4', 15000, 5, 'Produit de test pour les scénarios F4'
FROM categorie c
WHERE c.libelle = 'Test Ventes'
  AND NOT EXISTS (SELECT 1 FROM produit p WHERE p.ref = 'TEST-PROD-001');

-- Client de test
INSERT INTO client (nom, prenom, date_inscription, is_actif, numero_telephone, adresse, id_localite, id_zone_livraison, notes, id_service, id_typeClient, taille_cheptel, est_supprimer)
SELECT 'Client', 'Test', CURRENT_DATE, true, '0340000000', 'Avenue Test', 'LOC01', 'ZONE01', 'Client de test pour F4', s.id, tc.id, 10, false
FROM service s, type_client tc
WHERE s.libelle = 'Commercial'
  AND tc.libelle = 'Particulier'
  AND NOT EXISTS (
    SELECT 1 FROM client c
    WHERE c.nom = 'Client' AND c.prenom = 'Test' AND c.numero_telephone = '0340000000'
);

-- Lots produits de test
INSERT INTO lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_peremption)
SELECT p.id, 20, 20, '2026-01-01', '2027-01-01'
FROM produit p
WHERE p.ref = 'TEST-PROD-001'
  AND NOT EXISTS (
    SELECT 1 FROM lot_produit l WHERE l.id_produit = p.id AND l.date_fabrication = '2026-01-01' AND l.quantite_initiale = 20
);

INSERT INTO lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_peremption)
SELECT p.id, 50, 50, '2026-02-01', '2027-01-01'
FROM produit p
WHERE p.ref = 'TEST-PROD-001'
  AND NOT EXISTS (
    SELECT 1 FROM lot_produit l WHERE l.id_produit = p.id AND l.date_fabrication = '2026-02-01' AND l.quantite_initiale = 50
);

-- Fournir un exemple de client pour la validation
SELECT 'Données de test F4 insérées.';
