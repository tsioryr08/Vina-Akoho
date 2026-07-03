-- Valeurs de test pour verifier le calcul des recettes.
-- Les ventes validees doivent apparaitre dans /ventes/recettes.
-- La vente annulee ne doit pas etre comptabilisee.

INSERT INTO service (libelle, description)
VALUES ('Elevage', 'Clients eleveurs et fermes avicoles')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO type_client (libelle)
VALUES ('Ferme'), ('Revendeur'), ('Eleveur familial')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO mode_paiement (libelle)
VALUES ('Especes test'), ('Mobile money test')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO statut_vente (libelle)
VALUES ('Validée'), ('Annulée')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO categorie (
    libelle,
    description,
    pourcentage_proteine,
    pourcentage_matiere_grasses,
    pourcentage_humidite_max,
    marge_pourcentage,
    actif
)
VALUES
    ('Test Recette Croissance', 'Categorie test pour recettes ventes', 19, 4, 12, 18, true),
    ('Test Recette Finition', 'Categorie test pour recettes ventes', 17, 3, 12, 15, true)
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO produit (ref, id_categorie, nom, prix_vente, seuil_alerte, actif)
VALUES
    (
        'TST-REC-001',
        (SELECT id FROM categorie WHERE libelle = 'Test Recette Croissance'),
        'Test Aliment Croissance 50kg',
        125000,
        5,
        true
    ),
    (
        'TST-REC-002',
        (SELECT id FROM categorie WHERE libelle = 'Test Recette Finition'),
        'Test Aliment Finition 50kg',
        118000,
        5,
        true
    )
ON CONFLICT (ref) DO NOTHING;

INSERT INTO client (
    nom,
    prenom,
    date_inscription,
    is_actif,
    numero_telephone,
    adresse,
    id_localite,
    id_zone_livraison,
    notes,
    id_service,
    id_typeClient,
    taille_cheptel,
    est_supprimer
)
VALUES
    (
        'Rakoto',
        'Test Recette',
        '2026-07-01',
        true,
        '0340000001',
        'Mahitsy',
        'Mahitsy',
        'MAHITSY',
        'Client test recette',
        (SELECT id FROM service WHERE libelle = 'Elevage'),
        (SELECT id FROM type_client WHERE libelle = 'Ferme'),
        250,
        false
    ),
    (
        'Rabe',
        'Test Recette',
        '2026-07-01',
        true,
        '0340000002',
        'Ankazobe',
        'Ankazobe',
        'ANKAZOBE',
        'Client test recette',
        (SELECT id FROM service WHERE libelle = 'Elevage'),
        (SELECT id FROM type_client WHERE libelle = 'Revendeur'),
        120,
        false
    )
ON CONFLICT DO NOTHING;

INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
VALUES
    (
        (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1),
        TIMESTAMP '2026-07-01 09:15:00',
        375000,
        (SELECT id FROM mode_paiement WHERE libelle = 'Especes test'),
        (SELECT id FROM statut_vente WHERE libelle = 'Validée')
    ),
    (
        (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1),
        TIMESTAMP '2026-07-02 10:20:00',
        236000,
        (SELECT id FROM mode_paiement WHERE libelle = 'Especes test'),
        (SELECT id FROM statut_vente WHERE libelle = 'Validée')
    ),
    (
        (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1),
        TIMESTAMP '2026-07-03 11:25:00',
        250000,
        (SELECT id FROM mode_paiement WHERE libelle = 'Especes test'),
        (SELECT id FROM statut_vente WHERE libelle = 'Validée')
    ),
    (
        (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1),
        TIMESTAMP '2026-07-04 12:30:00',
        118000,
        (SELECT id FROM mode_paiement WHERE libelle = 'Especes test'),
        (SELECT id FROM statut_vente WHERE libelle = 'Annulée')
    )
ON CONFLICT DO NOTHING;

INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
SELECT
    v.id,
    p.id,
    3.00,
    p.prix_vente,
    3.00 * p.prix_vente
FROM vente v
JOIN produit p ON p.ref = 'TST-REC-001'
WHERE v.id_client = (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1)
  AND v.date_vente = TIMESTAMP '2026-07-01 09:15:00'
  AND v.montant_total = 375000;

INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
SELECT
    v.id,
    p.id,
    2.00,
    p.prix_vente,
    2.00 * p.prix_vente
FROM vente v
JOIN produit p ON p.ref = 'TST-REC-002'
WHERE v.id_client = (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1)
  AND v.date_vente = TIMESTAMP '2026-07-02 10:20:00'
  AND v.montant_total = 236000;

INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
SELECT
    v.id,
    p.id,
    2.00,
    p.prix_vente,
    2.00 * p.prix_vente
FROM vente v
JOIN produit p ON p.ref = 'TST-REC-001'
WHERE v.id_client = (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1)
  AND v.date_vente = TIMESTAMP '2026-07-03 11:25:00'
  AND v.montant_total = 250000;

INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
SELECT
    v.id,
    p.id,
    1.00,
    p.prix_vente,
    1.00 * p.prix_vente
FROM vente v
JOIN produit p ON p.ref = 'TST-REC-002'
WHERE v.id_client = (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1)
  AND v.date_vente = TIMESTAMP '2026-07-04 12:30:00'
  AND v.montant_total = 118000;

INSERT INTO facture (
    id_vente,
    numero,
    date_emission,
    montant_ht,
    taux_tva,
    montant_tva,
    montant_ttc
)
SELECT
    v.id,
    'FACT-REC-' || to_char(v.date_vente, 'YYYYMMDDHH24MISS'),
    CAST(v.date_vente AS date),
    v.montant_total,
    0,
    0,
    v.montant_total
FROM vente v
WHERE v.date_vente IN (
    TIMESTAMP '2026-07-01 09:15:00',
    TIMESTAMP '2026-07-02 10:20:00',
    TIMESTAMP '2026-07-03 11:25:00',
    TIMESTAMP '2026-07-04 12:30:00'
)
ON CONFLICT (id_vente) DO NOTHING;
