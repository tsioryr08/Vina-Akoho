-- POUR RECETTE PRODUIT
INSERT INTO unite (libelle)
VALUES
('kg'),
('g'),
('L');

INSERT INTO fournisseur (nom, email, telephone)
VALUES
('AGRIVET Madagascar', 'contact@agrivet.mg', '0341234567');

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
(
'Poussin',
'Aliment pour poussins',
22,
5,
12,
20,
true
),
(
'Croissance',
'Aliment croissance',
19,
4,
12,
18,
true
),
(
'Finition',
'Aliment finition',
17,
3,
12,
15,
true
);


INSERT INTO matiere_premiere
(code, nom, id_fournisseur, cout_unitaire, id_unite, seuil_minimum)
VALUES

('MP001','Maïs jaune',1,1200,1,100),

('MP002','Son de riz',1,900,1,80),

('MP003','Tourteau de soja',1,2500,1,50),

('MP004','Farine de poisson',1,4000,1,20),

('MP005','Prémix vitamines',1,8000,1,5);

-- POUR SORTIE MP 
INSERT INTO type_mouvement (libelle) VALUES ('Entree'), ('Sortie');

-- insertion dans mp pour tester 
INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat) VALUES
(1, 40, 40, '2026-01-10'),
(1, 100, 100, '2026-03-05'),
(2, 50, 50, '2026-02-01');

-- donnees pour tester le FIFO
-- Lot ancien (doit être consommé en premier)
INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat)
VALUES (1, 50, 50, '2026-06-01');

-- Lot récent (ne doit être touché que si le 1er ne suffit pas)
INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat)
VALUES (1, 100, 100, '2026-06-25');

-- MP id=2 : un lot suffisant
INSERT INTO lot_mp (id_mp, quantite_initiale, quantite_restante, date_achat)
VALUES (2, 50, 50, '2026-06-10');