
INSERT INTO role (poste) VALUES
    ('Administrateur'),
    ('Responsable Production'),
    ('Gestionnaire de Stock'),
    ('Responsable Commercial'),
    ('Comptable'),
    ('Responsable Achats'),
    ('Livreur');

-- 2. Employés de test (1 par rôle)
-- mot de passe : admin123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Rakoto', 'Ny Antema', 'admin@vinaakoho.mg', '0340000001',
    '$2a$10$6KT4Hyq.H0kCscW57KfAhOITBBocLKz7wHQyp8NO8YmoycDwM1c72',
    (SELECT id FROM role WHERE poste = 'Administrateur')
);

-- mot de passe : prod123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Andry', 'Hery', 'production@vinaakoho.mg', '0340000002',
    '$2a$10$d15X40dc8LSbaNKs9qLiY.gJKgCXW2GqSIT.AkQ7KTrILBnZ4D9mu',
    (SELECT id FROM role WHERE poste = 'Responsable Production')
);

-- mot de passe : stock123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Nekena', 'Mialy', 'stock@vinaakoho.mg', '0340000003',
    '$2a$10$spZDL19F.mmHPh6EXpslquMrOdJnXMbCs.aSgRbLS5d4r77DZdKSC',
    (SELECT id FROM role WHERE poste = 'Gestionnaire de Stock')
);

-- mot de passe : commercial123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Rary', 'Fanja', 'commercial@vinaakoho.mg', '0340000004',
    '$2a$10$czofKmCfmjTrOg4WfpeK6.5tyqZuRMIq3t7GJKFLGZbxQxN3xVJUe',
    (SELECT id FROM role WHERE poste = 'Responsable Commercial')
);

-- mot de passe : compta123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Razafy', 'Tojo', 'comptable@vinaakoho.mg', '0340000005',
    '$2a$10$r2UgQBLymrYfyBSbAsIDUeHzZf0INvj65.AhrSCuvk4Ft18.cZo36',
    (SELECT id FROM role WHERE poste = 'Comptable')
);

-- mot de passe : achat123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Solofo', 'Mamy', 'achats@vinaakoho.mg', '0340000006',
    '$2a$10$DLXLjcyrkkjNnyvAWxPpJ.4UdOV3EVn5CnecgF6BQnva2ElRzVP22',
    (SELECT id FROM role WHERE poste = 'Responsable Achats')
);

-- mot de passe : livreur123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role)
VALUES (
    'Jean', 'Paul', 'livreur@vinaakoho.mg', '0340000007',
    '$2a$10$pGrTmknpRQwiiWYkAyebwu29l6YlP/LuIriRdKfxeCkdAIXLV2XJ.',
    (SELECT id FROM role WHERE poste = 'Livreur')
);

-- Vérification rapide
SELECT e.id, e.nom, e.prenom, e.email, r.poste
FROM employe e
JOIN role r ON r.id = e.id_role
ORDER BY e.id;
