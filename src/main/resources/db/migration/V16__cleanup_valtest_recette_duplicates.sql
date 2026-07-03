-- Nettoyage des doublons possibles si V15__valtest_recette.sql a ete execute manuellement.
-- On conserve la premiere vente de chaque jeu de test et on supprime les copies.

DELETE FROM facture
WHERE id_vente IN (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (
                PARTITION BY id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente
                ORDER BY id
            ) AS rang
        FROM vente
    ) ventes
    WHERE rang > 1
);

DELETE FROM vente
WHERE id IN (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (
                PARTITION BY id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente
                ORDER BY id
            ) AS rang
        FROM vente
    ) ventes
    WHERE rang > 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_vente_valtest_recette_unique
ON vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente);