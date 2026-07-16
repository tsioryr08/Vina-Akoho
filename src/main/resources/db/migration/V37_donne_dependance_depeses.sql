-- Catégories de dépenses de l'usine d'aliments pour poulets
INSERT INTO categorie_depense (libelle) VALUES
('Achat Matières Premières (Maïs, Soja, Minéraux)'),
('Sacs et Emballages de Conditionnement'),
('Énergie Usine (Électricité, Eau, Fioul)'),
('Maintenance Industrielle (Broyeurs, Mélangeurs)'),
('Transport et Logistique (Livraison clients)'),
('Salaires et Main d''œuvre Production'),
('Loyer Usine et Stockage'),
('Assurances et Certifications Sanitaires'),
('Frais Commerciaux et Marketing'),
('Frais Administratifs et Taxes');

-- Phases du projet (Démarrage, Croissance, Stabilité)
INSERT INTO phase (libelle, description) VALUES
('Démarrage', 'Phase initiale de lancement du projet ou de l''activité, caractérisée par la mise en place des ressources et les premiers investissements.'),
('Croissance', 'Phase d''accélération et de développement où l''activité se déploie rapidement et gagne en parts de marché.'),
('Stabilité', 'Phase de maturité et de vitesse de croisière où l''activité est stabilisée, sécurisée et optimisée pour le long terme.');

-- Statuts des dépenses d'origine
INSERT INTO statut_depense (libelle) VALUES
('En attente'),
('Approuvé'),
('Rejeté'),
('Payé'),
('Remboursé');