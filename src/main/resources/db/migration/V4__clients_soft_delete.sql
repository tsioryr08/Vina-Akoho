-- ============================================
-- AJOUT DE LA SUPPRESSION LOGIQUE POUR CLIENT
-- ============================================

ALTER TABLE client
ADD COLUMN IF NOT EXISTS est_supprimer BOOLEAN NOT NULL DEFAULT FALSE;
