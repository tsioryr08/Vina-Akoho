-- V33 : Donnees de test pour le Dashboard Production (stock + lots + alertes)


-- 1. Produits finis supplements + seuils

INSERT INTO produit (ref, id_categorie, nom, prix_vente, seuil_alerte, actif)
VALUES
    ('PRD-PF-001', (SELECT id FROM categorie WHERE libelle = 'Poussin'), 'Aliment Poussin 10kg Evolution', 28000, 200, true),
    ('PRD-PF-002', (SELECT id FROM categorie WHERE libelle = 'Croissance'), 'Aliment Croissance 50kg Premium', 132000, 500, true),
    ('PRD-PF-003', (SELECT id FROM categorie WHERE libelle = 'Finition'), 'Aliment Finition 50kg Excellence', 122000, 500, true),
    ('PRD-PF-004', (SELECT id FROM categorie WHERE libelle = 'Poussin'), 'Aliment Poussin 25kg Debut', 60000, 150, true),
    ('PRD-PF-005', (SELECT id FROM categorie WHERE libelle = 'Croissance'), 'Aliment Croissance 10kg Compact', 32000, 300, true)
ON CONFLICT (ref) DO NOTHING;


-- 2. Lots produits

INSERT INTO lot_produit (id_produit, quantite_initiale, quantite_restante, date_fabrication, date_peremption)
VALUES
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-001'), 2000, 1500, '2026-06-10', '2026-09-10'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-001'), 1000, 800,  '2026-07-01', '2026-10-01'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-002'), 3000, 2000, '2026-05-15', '2026-12-15'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-002'), 1000, 0,    '2026-04-20', '2026-11-20'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-003'), 2500, 1500, '2026-06-25', '2026-12-25'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-004'), 500,  300,  '2026-07-05', '2026-10-05'),
    ((SELECT id FROM produit WHERE ref = 'PRD-PF-005'), 800,  600,  '2026-07-08', '2027-01-08');


-- 3. Matieres premieres supplementaires

INSERT INTO matiere_premiere (code, nom, id_fournisseur, cout_unitaire, id_unite, seuil_minimum)
VALUES
    ('MP-DASH-01', 'Maïs grain Dash',       (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1), 1100, (SELECT id FROM unite WHERE libelle = 'kg'), 200),
    ('MP-DASH-02', 'Tourteau soja Dash',    (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1), 2400, (SELECT id FROM unite WHERE libelle = 'kg'), 100),
    ('MP-DASH-03', 'Farine poisson Dash',   (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1), 3900, (SELECT id FROM unite WHERE libelle = 'kg'), 50),
    ('MP-DASH-04', 'Prémix minéral Dash',   (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1), 8500, (SELECT id FROM unite WHERE libelle = 'kg'), 10),
    ('MP-DASH-05', 'Blé tendre Dash',       (SELECT id FROM fournisseur WHERE email = 'contact@agrivet.mg' ORDER BY id LIMIT 1), 1000, (SELECT id FROM unite WHERE libelle = 'kg'), 150)
ON CONFLICT (code) DO NOTHING;


-- 4. Lots MP (quelques-uns bientôt expirants)

INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat, date_peremption)
VALUES
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-01'), 1000, 600,  '2026-01-20', '2026-08-20'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-01'), 500,  200,  '2026-04-12', '2026-07-25'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-02'), 400,  300,  '2026-03-01', '2026-09-01'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-02'), 300,  150,  '2026-05-15', '2026-07-30'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-03'), 200,  120,  '2026-02-10', '2026-10-10'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-04'), 100,  5,    '2026-06-01', '2026-07-20'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-05'), 800,  600,  '2026-01-15', '2027-01-15'),
    ((SELECT id FROM matiere_premiere WHERE code = 'MP-DASH-05'), 600,  450,  '2026-04-20', '2026-08-05');
