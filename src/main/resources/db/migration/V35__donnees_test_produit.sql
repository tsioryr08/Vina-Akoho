-- Donnees de test pour le module produits.
-- Objectif : fournir des recettes actives pour les categories deja utilisees par
-- les produits de test, afin d'eviter l'erreur "Aucune recette active pour la
-- categorie du produit" lors de l'entree produit.

INSERT INTO recette_produit (
    id_categorie,
    version,
    id_mp,
    quantite_mp,
    id_unite,
    is_active,
    id_employe_creation
)
VALUES
    -- Poussin
    (
        (SELECT id FROM categorie WHERE libelle = 'Poussin'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP001'),
        2.00,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Poussin'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP002'),
        1.00,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Poussin'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP005'),
        0.05,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),

    -- Croissance
    (
        (SELECT id FROM categorie WHERE libelle = 'Croissance'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP001'),
        1.80,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Croissance'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP002'),
        1.20,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Croissance'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP003'),
        0.60,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Croissance'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP005'),
        0.05,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),

    -- Finition
    (
        (SELECT id FROM categorie WHERE libelle = 'Finition'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP001'),
        1.50,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Finition'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP002'),
        1.50,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Finition'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP003'),
        0.80,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Finition'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP004'),
        0.20,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM categorie WHERE libelle = 'Finition'),
        1,
        (SELECT id FROM matiere_premiere WHERE code = 'MP005'),
        0.05,
        (SELECT id FROM unite WHERE libelle = 'kg'),
        TRUE,
        (SELECT id FROM employe WHERE email = 'admin@vinaakoho.mg' ORDER BY id LIMIT 1)
    )
ON CONFLICT DO NOTHING;

-- Stock de test pour la production produit.
-- Le lot de Tourteau de soja est volontairement plus généreux, car la
-- production de l'aliment croissance consomme cette matiere premiere en premier.

INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat)
VALUES
    (
        (SELECT id FROM matiere_premiere WHERE code = 'MP001'),
        25.00,
        25.00,
        DATE '2026-07-10'
    ),
    (
        (SELECT id FROM matiere_premiere WHERE code = 'MP002'),
        20.00,
        20.00,
        DATE '2026-07-10'
    ),
    (
        (SELECT id FROM matiere_premiere WHERE code = 'MP003'),
        30.00,
        30.00,
        DATE '2026-07-10'
    ),
    (
        (SELECT id FROM matiere_premiere WHERE code = 'MP004'),
        10.00,
        10.00,
        DATE '2026-07-10'
    ),
    (
        (SELECT id FROM matiere_premiere WHERE code = 'MP005'),
        5.00,
        5.00,
        DATE '2026-07-10'
    )
ON CONFLICT DO NOTHING;
