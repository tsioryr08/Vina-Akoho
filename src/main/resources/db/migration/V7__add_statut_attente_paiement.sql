-- Ajouter le statut "En attente de paiement" pour les ventes
INSERT INTO statut_vente (libelle) VALUES ('En attente de paiement') ON CONFLICT (libelle) DO NOTHING;
