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

INSERT INTO statut_commande (libelle)
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
        'Zone Nord',
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
        'Zone Ouest',
        'Client test recette',
        (SELECT id FROM service WHERE libelle = 'Elevage'),
        (SELECT id FROM type_client WHERE libelle = 'Revendeur'),
        120,
        false
    )
ON CONFLICT DO NOTHING;

WITH commande_test AS (
    INSERT INTO commande (date_commande, id_client, id_statut_commande, commentaire)
    VALUES
        (
            '2026-07-01 09:00:00',
            (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1),
            (SELECT id FROM statut_commande WHERE libelle = 'Validée'),
            'VALTEST_RECETTE_001'
        ),
        (
            '2026-07-02 10:00:00',
            (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1),
            (SELECT id FROM statut_commande WHERE libelle = 'Validée'),
            'VALTEST_RECETTE_002'
        ),
        (
            '2026-07-03 11:00:00',
            (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1),
            (SELECT id FROM statut_commande WHERE libelle = 'Validée'),
            'VALTEST_RECETTE_003'
        ),
        (
            '2026-07-04 12:00:00',
            (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1),
            (SELECT id FROM statut_commande WHERE libelle = 'Annulée'),
            'VALTEST_RECETTE_ANNULEE'
        )
    ON CONFLICT DO NOTHING
    RETURNING id, commentaire
)
INSERT INTO vente (id_commande, date_vente, montant_total, id_mode_paiement, id_statut_vente)
SELECT
    c.id,
    CASE c.commentaire
        WHEN 'VALTEST_RECETTE_001' THEN TIMESTAMP '2026-07-01 09:15:00'
        WHEN 'VALTEST_RECETTE_002' THEN TIMESTAMP '2026-07-02 10:20:00'
        WHEN 'VALTEST_RECETTE_003' THEN TIMESTAMP '2026-07-03 11:25:00'
        ELSE TIMESTAMP '2026-07-04 12:30:00'
    END,
    CASE c.commentaire
        WHEN 'VALTEST_RECETTE_001' THEN 375000
        WHEN 'VALTEST_RECETTE_002' THEN 236000
        WHEN 'VALTEST_RECETTE_003' THEN 250000
        ELSE 118000
    END,
    (SELECT id FROM mode_paiement WHERE libelle = 'Especes test'),
    CASE c.commentaire
        WHEN 'VALTEST_RECETTE_ANNULEE' THEN (SELECT id FROM statut_vente WHERE libelle = 'Annulée')
        ELSE (SELECT id FROM statut_vente WHERE libelle = 'Validée')
    END
FROM commande_test c
ON CONFLICT DO NOTHING;

INSERT INTO ligne_commande (id_commande, id_produit, quantite, prix_unitaire)
SELECT
    c.id,
    p.id,
    v.quantite,
    p.prix_vente
FROM (
    VALUES
        ('VALTEST_RECETTE_001', 'TST-REC-001', 3.00),
        ('VALTEST_RECETTE_002', 'TST-REC-002', 2.00),
        ('VALTEST_RECETTE_003', 'TST-REC-001', 2.00),
        ('VALTEST_RECETTE_ANNULEE', 'TST-REC-002', 1.00)
) AS v(commentaire, ref, quantite)
JOIN commande c ON c.commentaire = v.commentaire
JOIN produit p ON p.ref = v.ref
ON CONFLICT (id_commande, id_produit) DO NOTHING;

INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
SELECT
    vente.id,
    p.id,
    v.quantite,
    p.prix_vente,
    v.quantite * p.prix_vente
FROM (
    VALUES
        ('VALTEST_RECETTE_001', 'TST-REC-001', 3.00),
        ('VALTEST_RECETTE_002', 'TST-REC-002', 2.00),
        ('VALTEST_RECETTE_003', 'TST-REC-001', 2.00),
        ('VALTEST_RECETTE_ANNULEE', 'TST-REC-002', 1.00)
) AS v(commentaire, ref, quantite)
JOIN commande c ON c.commentaire = v.commentaire
JOIN vente ON vente.id_commande = c.id
JOIN produit p ON p.ref = v.ref
ON CONFLICT (id_vente, id_produit) DO NOTHING;

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
    vente.id,
    'FACT-' || c.commentaire,
    CAST(vente.date_vente AS date),
    vente.montant_total,
    0,
    0,
    vente.montant_total
FROM vente
JOIN commande c ON c.id = vente.id_commande
WHERE c.commentaire LIKE 'VALTEST_RECETTE%'
ON CONFLICT (id_vente) DO NOTHING;
