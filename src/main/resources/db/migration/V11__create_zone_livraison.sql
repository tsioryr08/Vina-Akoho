-- Créer la table zone_livraison avec les zones correspondant aux filtres
CREATE TABLE IF NOT EXISTS zone_livraison (
    id VARCHAR(50) PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- Insérer les zones de livraison correspondant aux filtres du dashboard
INSERT INTO zone_livraison (id, libelle) VALUES 
('ITAOSY', 'Itaosy'),
('MAHITSY', 'Mahitsy'),
('AMBOHIDRATRIMO', 'Ambohidratrimo'),
('ANKAZOBE', 'Ankazobe'),
('ZONE-VTE-01', 'Zone Vente 01')
ON CONFLICT (id) DO NOTHING;
