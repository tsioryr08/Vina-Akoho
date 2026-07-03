-- Ajouter des clients de test avec des localisations correspondant aux zones du filtre

-- Ajouter les données de référence pour service et type_client
INSERT INTO service (libelle, description) VALUES ('Vente', 'Service de vente aux clients') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO type_client (libelle) VALUES ('Éleveur') ON CONFLICT (libelle) DO NOTHING;

-- Client à Itaosy
INSERT INTO client (nom, prenom, adresse, numero_telephone, id_zone_livraison, est_supprimer, id_service, id_typeClient) 
VALUES ('Rasoa', 'Marie', 'Lot 123 Quartier Itaosy, Antananarivo', '0341234567', 'ITAOSY', false, 1, 1) 
ON CONFLICT DO NOTHING;

-- Client à Mahitsy
INSERT INTO client (nom, prenom, adresse, numero_telephone, id_zone_livraison, est_supprimer, id_service, id_typeClient) 
VALUES ('Rakoto', 'Jean', 'Zone industrielle Mahitsy, Antananarivo', '0342345678', 'MAHITSY', false, 1, 1) 
ON CONFLICT DO NOTHING;

-- Client à Ambohidratrimo
INSERT INTO client (nom, prenom, adresse, numero_telephone, id_zone_livraison, est_supprimer, id_service, id_typeClient) 
VALUES ('Randria', 'Fara', 'Ambohidratrimo, Antananarivo', '0343456789', 'AMBOHIDRATRIMO', false, 1, 1) 
ON CONFLICT DO NOTHING;

-- Client à Ankazobe
INSERT INTO client (nom, prenom, adresse, numero_telephone, id_zone_livraison, est_supprimer, id_service, id_typeClient) 
VALUES ('Ravelo', 'Paul', 'Village Ankazobe, Antananarivo', '0344567890', 'ANKAZOBE', false, 1, 1) 
ON CONFLICT DO NOTHING;

-- Client sans localisation spécifique (pour tester le filtre "Toutes les zones")
INSERT INTO client (nom, prenom, adresse, numero_telephone, est_supprimer, id_service, id_typeClient) 
VALUES ('Andriamanitra', 'Luc', 'Antananarivo', '0345678901', false, 1, 1) 
ON CONFLICT DO NOTHING;
