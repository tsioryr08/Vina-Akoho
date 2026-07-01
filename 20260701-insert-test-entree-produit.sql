-- ============================================
-- PRODUIT à fabriquer (catégorie Poussin)
-- ============================================
INSERT INTO unite (libelle) VALUES ('unité') ON CONFLICT (libelle) DO NOTHING;
INSERT INTO produit (ref, id_categorie, nom, prix_vente, seuil_alerte, actif)
VALUES ('PRD-001', 1, 'Aliment Poussin 10kg', 25000, 10, true)
RETURNING id;
-- suppose id=1

-- ============================================
-- RECETTE ACTIVE pour la catégorie Poussin (id=1)
-- ============================================

INSERT INTO recette_produit (id_categorie, version, id_mp, quantite_mp, id_unite, is_active, id_employe_creation)
VALUES 
  (1, 1, 1, 2, 1, true, 1),   -- 2 kg de Maïs jaune par unité produite
  (1, 1, 2, 1, 1, true, 1);   -- 1 kg de Son de riz par unité produite