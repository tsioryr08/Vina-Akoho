-- Donnees de test pour valider le module livraison
-- A executer apres les migrations et les donnees de base

INSERT INTO service (libelle)
SELECT 'Commercial'
WHERE NOT EXISTS (SELECT 1 FROM service WHERE libelle = 'Commercial');

INSERT INTO type_client (libelle)
SELECT 'Particulier'
WHERE NOT EXISTS (SELECT 1 FROM type_client WHERE libelle = 'Particulier');

INSERT INTO statut_livraison (libelle)
SELECT 'En préparation'
WHERE NOT EXISTS (SELECT 1 FROM statut_livraison WHERE libelle = 'En préparation');

INSERT INTO statut_livraison (libelle)
SELECT 'En cours'
WHERE NOT EXISTS (SELECT 1 FROM statut_livraison WHERE libelle = 'En cours');

INSERT INTO statut_livraison (libelle)
SELECT 'Livrée'
WHERE NOT EXISTS (SELECT 1 FROM statut_livraison WHERE libelle = 'Livrée');

INSERT INTO statut_livraison (libelle)
SELECT 'Annulée'
WHERE NOT EXISTS (SELECT 1 FROM statut_livraison WHERE libelle = 'Annulée');

INSERT INTO livreur (nom, prenom, email, contact)
SELECT 'Rakoto', 'Livreur', 'livreur.test@vinaakoho.mg', '0340000001'
WHERE NOT EXISTS (
    SELECT 1 FROM livreur WHERE email = 'livreur.test@vinaakoho.mg'
);

INSERT INTO client (
    nom,
    prenom,
    date_inscription,
    is_actif,
    numero_telephone,
    adresse,
    id_localite,
    id_zone_livraison,
    notes,
    id_service,
    id_typeClient,
    taille_cheptel,
    est_supprimer
)
SELECT
    'Client',
    'Livraison',
    CURRENT_DATE,
    true,
    '0340000002',
    'Quartier test livraison',
    'LOC-LIV-01',
    'ZONE-LIV-01',
    'Client de test pour le module livraison',
    s.id,
    tc.id,
    25,
    false
FROM service s,
     type_client tc
WHERE s.libelle = 'Commercial'
  AND tc.libelle = 'Particulier'
  AND NOT EXISTS (
      SELECT 1
      FROM client c
      WHERE c.numero_telephone = '0340000002'
  );

INSERT INTO commande (
    date_commande,
    id_client,
    id_statut_commande,
    commentaire
)
SELECT
    CURRENT_TIMESTAMP,
    c.id,
    sc.id,
    'Commande de test pour livraison'
FROM client c,
     statut_commande sc
WHERE c.numero_telephone = '0340000002'
  AND sc.libelle = 'Validée'
  AND NOT EXISTS (
      SELECT 1
      FROM commande cmd
      WHERE cmd.id_client = c.id
        AND cmd.commentaire = 'Commande de test pour livraison'
  );

INSERT INTO vente (
    id_commande,
    date_vente,
    montant_total,
    id_mode_paiement,
    id_statut_vente
)
SELECT
    cmd.id,
    CURRENT_TIMESTAMP,
    45000,
    mp.id,
    sv.id
FROM commande cmd,
     mode_paiement mp,
     statut_vente sv
WHERE cmd.commentaire = 'Commande de test pour livraison'
  AND mp.libelle = 'Espèces'
  AND sv.libelle = 'Validée'
  AND NOT EXISTS (
      SELECT 1
      FROM vente v
      WHERE v.id_commande = cmd.id
  );

INSERT INTO livraison (
    id_vente,
    id_livreur,
    lieu_exact,
    contact,
    date_livraison,
    commentaire,
    id_statut_livraison
)
SELECT
    v.id,
    l.id,
    'Avenue test livraison, Antananarivo',
    '0340000002',
    CURRENT_DATE + 1,
    'Livraison de test pour verifier le flux complet',
    sl.id
FROM vente v,
     livreur l,
     statut_livraison sl
WHERE v.id_commande = (
          SELECT cmd.id
          FROM commande cmd
          WHERE cmd.commentaire = 'Commande de test pour livraison'
          LIMIT 1
      )
  AND l.email = 'livreur.test@vinaakoho.mg'
  AND sl.libelle = 'En préparation'
  AND NOT EXISTS (
      SELECT 1
      FROM livraison liv
      WHERE liv.id_vente = v.id
  );

INSERT INTO historique_statut_livraison (
    id_livraison,
    ancien_statut,
    nouveau_statut,
    date_changement
)
SELECT
    liv.id,
    NULL,
    sl.id,
    CURRENT_TIMESTAMP
FROM livraison liv,
     statut_livraison sl
WHERE liv.contact = '0340000002'
  AND sl.libelle = 'En préparation'
  AND NOT EXISTS (
      SELECT 1
      FROM historique_statut_livraison hist
      WHERE hist.id_livraison = liv.id
  );

SELECT 'Donnees de test livraison inserees.' AS message;