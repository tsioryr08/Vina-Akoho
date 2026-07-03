-- Donnees de test pour produit, matieres premieres, lots et recette.

INSERT INTO unite (libelle)
VALUES ('kg'), ('g'), ('L'), ('unité')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO fournisseur (nom, email, telephone)
VALUES ('AGRIVET Madagascar', 'contact@agrivet.mg', '0341234567');

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
    ('Poussin', 'Aliment pour poussins', 22, 5, 12, 20, true),
    ('Croissance', 'Aliment croissance', 19, 4, 12, 18, true),
    ('Finition', 'Aliment finition', 17, 3, 12, 15, true)
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO matiere_premiere
    (code, nom, id_fournisseur, cout_unitaire, id_unite, seuil_minimum)
VALUES
    (
        'MP001',
        'Maïs jaune',
        (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1),
        1200,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        100
    ),
    (
        'MP002',
        'Son de riz',
        (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1),
        900,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        80
    ),
    (
        'MP003',
        'Tourteau de soja',
        (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1),
        2500,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        50
    ),
    (
        'MP004',
        'Farine de poisson',
        (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1),
        4000,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        20
    ),
    (
        'MP005',
        'Prémix vitamines',
        (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1),
        8000,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        5
    )
ON CONFLICT (code) DO NOTHING;

INSERT INTO type_mouvement (libelle)
VALUES ('Entree'), ('Sortie')
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat)
VALUES
    ((SELECT id FROM matiere_premiere WHERE code = 'MP001'), 40, 40, '2026-01-10'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP001'), 100, 100, '2026-03-05'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP002'), 50, 50, '2026-02-01'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP001'), 50, 50, '2026-06-01'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP001'), 100, 100, '2026-06-25'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP002'), 50, 50, '2026-06-10');

INSERT INTO produit (ref, id_categorie, nom, prix_vente, seuil_alerte, actif)
VALUES (
    'PRD-001',
    (SELECT id FROM categorie WHERE libelle = 'Poussin'),
    'Aliment Poussin 10kg',
    25000,
    10,
    true
)
ON CONFLICT (ref) DO NOTHING;

INSERT INTO recette_produit
    (id_categorie, version, id_mp, quantite_mp, id_unite, is_active, id_employe_creation)
VALUES
    (
        (SELECT id FROM categorie WHERE libelle = 'Poussin'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP001'),
        2,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        true,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg')
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Poussin'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP002'),
        1,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        true,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg')
    )
ON CONFLICT (id_categorie, version, id_mp) DO NOTHING;
