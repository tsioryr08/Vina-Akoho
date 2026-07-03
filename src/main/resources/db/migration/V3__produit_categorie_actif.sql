-- ============================================
-- AJOUT DE LA COLONNE ACTIF POUR SOFT DELETE
-- ============================================

-- 1. Donner la propriété des tables à l'utilisateur vinakoho (si ce n'est pas déjà fait)
ALTER TABLE produit OWNER TO vinakoho;
ALTER TABLE categorie OWNER TO vinakoho;

-- 2. Ajouter la colonne actif dans la table produit
ALTER TABLE produit ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE NOT NULL;

-- 3. Ajouter la colonne actif dans la table categorie
ALTER TABLE categorie ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE NOT NULL;

-- 4. Créer des index pour optimiser les requêtes sur actif
CREATE INDEX IF NOT EXISTS idx_produit_actif ON produit(actif);
CREATE INDEX IF NOT EXISTS idx_categorie_actif ON categorie(actif);