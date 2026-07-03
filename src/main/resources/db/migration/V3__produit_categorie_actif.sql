-- ============================================
-- AJOUT DE LA COLONNE ACTIF POUR SOFT DELETE
-- ============================================

-- 1. Ajouter la colonne actif dans la table produit
ALTER TABLE produit 
ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE NOT NULL;

-- 2. Ajouter la colonne actif dans la table categorie
ALTER TABLE categorie 
ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE NOT NULL;

-- 3. Créer des index pour optimiser les requêtes sur actif
CREATE INDEX IF NOT EXISTS idx_produit_actif ON produit(actif);
CREATE INDEX IF NOT EXISTS idx_categorie_actif ON categorie(actif);

-- 4. Mettre à jour les données existantes (tous les produits et catégories sont actifs par défaut)
-- (Déjà fait par le DEFAULT TRUE)
