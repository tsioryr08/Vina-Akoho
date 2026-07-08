-- Option A : on garde une seule entité Vente, mais on enrichit son cycle de vie
-- avec les statuts nécessaires à la séparation logique Commande / Vente réalisée,
-- sans créer de nouvelle table.

-- Nouveaux statuts de vente (les statuts "Validée", "Annulée", "En attente de
-- paiement" existent déjà depuis V2/V7)
INSERT INTO statut_vente (libelle) VALUES ('En préparation') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_vente (libelle) VALUES ('En livraison') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_vente (libelle) VALUES ('Livrée') ON CONFLICT (libelle) DO NOTHING;

-- Seed des statuts de livraison : la table existe depuis V1 mais n'a jamais été
-- peuplée, or on va maintenant créer une livraison automatiquement depuis le
-- formulaire "Nouvelle vente".
INSERT INTO statut_livraison (libelle) VALUES ('En attente d''affectation') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_livraison (libelle) VALUES ('En cours de livraison') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_livraison (libelle) VALUES ('Livrée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_livraison (libelle) VALUES ('Échec de livraison') ON CONFLICT (libelle) DO NOTHING;
