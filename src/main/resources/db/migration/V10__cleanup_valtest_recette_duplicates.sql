-- Nettoyage des doublons possibles si V9__valtest_recette.sql a ete execute manuellement.
-- On conserve la premiere commande de chaque jeu de test et on supprime les copies.

WITH commandes_doublons AS (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (PARTITION BY commentaire ORDER BY id) AS rang
        FROM commande
        WHERE commentaire LIKE 'VALTEST_RECETTE%'
    ) c
    WHERE rang > 1
),
ventes_doublons AS (
    SELECT v.id
    FROM vente v
    JOIN commandes_doublons c ON c.id = v.id_commande
)
DELETE FROM facture
WHERE id_vente IN (SELECT id FROM ventes_doublons);

WITH commandes_doublons AS (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (PARTITION BY commentaire ORDER BY id) AS rang
        FROM commande
        WHERE commentaire LIKE 'VALTEST_RECETTE%'
    ) c
    WHERE rang > 1
)
DELETE FROM vente
WHERE id_commande IN (SELECT id FROM commandes_doublons);

WITH commandes_doublons AS (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (PARTITION BY commentaire ORDER BY id) AS rang
        FROM commande
        WHERE commentaire LIKE 'VALTEST_RECETTE%'
    ) c
    WHERE rang > 1
)
DELETE FROM commande
WHERE id IN (SELECT id FROM commandes_doublons);

CREATE UNIQUE INDEX IF NOT EXISTS uq_commande_valtest_recette_commentaire
ON commande (commentaire)
WHERE commentaire LIKE 'VALTEST_RECETTE%';
