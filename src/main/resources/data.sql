INSERT INTO role (poste) VALUES ('Administrateur') ON CONFLICT (poste) DO NOTHING;
INSERT INTO role (poste) VALUES ('Responsable achat') ON CONFLICT (poste) DO NOTHING;
INSERT INTO role (poste) VALUES ('Responsable de production') ON CONFLICT (poste) DO NOTHING;
INSERT INTO role (poste) VALUES ('Gestionnaire de stock') ON CONFLICT (poste) DO NOTHING;
INSERT INTO role (poste) VALUES ('Responsable commercial') ON CONFLICT (poste) DO NOTHING;
INSERT INTO role (poste) VALUES ('Comptable') ON CONFLICT (poste) DO NOTHING;

--mdp: admin123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Admin', 'Sys', 'admin@vinaakoho.mg', NULL, '$2a$10$XwFIxBeDJqCX30E6XRe3IOMM6ZncY8.kDRMjzpDLr4H2kpCzIzv8O', 1) ON CONFLICT (email) DO NOTHING;
--mdp: achat123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Achat', 'Responsable', 'achat@vinaakoho.mg', NULL, '$2a$10$jZwXCQrh9XWwJhJeuGQvaef1iQZnvATmQAzCQkJJEZUcSwmkGI6Q2', 2) ON CONFLICT (email) DO NOTHING;
--mdp: prod123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Production', 'Responsable', 'production@vinaakoho.mg', NULL, '$2a$10$sBbJUwXjaFJ0eehrCOMERelyjcHRt2piX2jl8FTYO8MLqJJgASXYm', 3) ON CONFLICT (email) DO NOTHING;
--mdp: stock123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Stock', 'Gestionnaire', 'stock@vinaakoho.mg', NULL, '$2a$10$SUkjYIvBUsZY0gmiIwniPusmAyRt35ZBgp2jxp2s1R527Jf.PFYjG', 4) ON CONFLICT (email) DO NOTHING;
--mdp: commercial123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Commercial', 'Responsable', 'commercial@vinaakoho.mg', NULL, '$2a$10$97H3BldJA5InSJmrQDaMOew.75kl52Wkl35N5.hv2COl.CoEuzvm.', 5) ON CONFLICT (email) DO NOTHING;
--mdp: comptable123
INSERT INTO employe (nom, prenom, email, contact, mdp, id_role) VALUES ('Comptable', 'Sys', 'comptable@vinaakoho.mg', NULL, '$2a$10$jP.L5EwtrXUPE6kcooG50eIlJvjKfwSCRToRmlR4UtlBdIwhet8s.', 6) ON CONFLICT (email) DO NOTHING;

INSERT INTO mode_paiement (libelle) VALUES ('Espèces') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO mode_paiement (libelle) VALUES ('Transfert bancaire') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO mode_paiement (libelle) VALUES ('Mobile money') ON CONFLICT (libelle) DO NOTHING;

INSERT INTO statut_vente (libelle) VALUES ('Validée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_vente (libelle) VALUES ('Annulée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_vente (libelle) VALUES ('En attente') ON CONFLICT (libelle) DO NOTHING;

INSERT INTO statut_commande (libelle) VALUES ('Validée') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_commande (libelle) VALUES ('En cours') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO statut_commande (libelle) VALUES ('Annulée') ON CONFLICT (libelle) DO NOTHING;