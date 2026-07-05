-- Donnees de test pour valider le module ventes
-- A executer apres les migrations et les donnees de base

INSERT INTO service (libelle)
SELECT 'Commercial'
WHERE NOT EXISTS (SELECT 1 FROM service WHERE libelle = 'Commercial');

INSERT INTO type_client (libelle)
SELECT 'Particulier'
WHERE NOT EXISTS (SELECT 1 FROM type_client WHERE libelle = 'Particulier');

INSERT INTO categorie (
    libelle,
    description,
    pourcentage_proteine,
    pourcentage_matiere_grasses,
    pourcentage_humidite_max,
    marge_pourcentage,
    actif
)
SELECT
    'Test Ventes',
    'Categorie de test pour le module ventes',
    12,
    4,
    10,
    15,
    true
WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE libelle = 'Test Ventes');

INSERT INTO produit (
    ref,
    id_categorie,
    nom,
    prix_vente,
    seuil_alerte,
    description
)
SELECT
    'TEST-VENTE-001',
    c.id,
    'Produit Vente Test A',
    25000,
    5,
    'Produit de test pour une premiere vente'
FROM categorie c
WHERE c.libelle = 'Test Ventes'
  AND NOT EXISTS (SELECT 1 FROM produit p WHERE p.ref = 'TEST-VENTE-001');

INSERT INTO produit (
    ref,
    id_categorie,
    nom,
    prix_vente,
    seuil_alerte,
    description
)
SELECT
    'TEST-VENTE-002',
    c.id,
    'Produit Vente Test B',
    15000,
    3,
    'Second produit pour tester plusieurs lignes de vente'
FROM categorie c
WHERE c.libelle = 'Test Ventes'
  AND NOT EXISTS (SELECT 1 FROM produit p WHERE p.ref = 'TEST-VENTE-002');

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
    'Ventes',
    CURRENT_DATE,
    true,
    '0341000000',
    'Quartier test ventes',
    'LOC-VTE-01',
    'ZONE-VTE-01',
    'Client de test pour le module ventes',
    s.id,
    tc.id,
    40,
    false
FROM service s,
     type_client tc
WHERE s.libelle = 'Commercial'
  AND tc.libelle = 'Particulier'
  AND NOT EXISTS (
      SELECT 1
      FROM client c
      WHERE c.numero_telephone = '0341000000'
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
    'Commande de test ventes validee'
FROM client c,
     statut_commande sc
WHERE c.numero_telephone = '0341000000'
  AND sc.libelle = 'Validée'
  AND NOT EXISTS (
      SELECT 1
      FROM commande cmd
      WHERE cmd.id_client = c.id
        AND cmd.commentaire = 'Commande de test ventes validee'
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
    'Commande de test ventes annulee'
FROM client c,
     statut_commande sc
WHERE c.numero_telephone = '0341000000'
  AND sc.libelle = 'Annulée'
  AND NOT EXISTS (
      SELECT 1
      FROM commande cmd
      WHERE cmd.id_client = c.id
        AND cmd.commentaire = 'Commande de test ventes annulee'
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
    65000,
    mp.id,
    sv.id
FROM commande cmd,
     mode_paiement mp,
     statut_vente sv
WHERE cmd.commentaire = 'Commande de test ventes validee'
  AND mp.libelle = 'Espèces'
  AND sv.libelle = 'Validée'
  AND NOT EXISTS (
      SELECT 1
      FROM vente v
      WHERE v.id_commande = cmd.id
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
    15000,
    mp.id,
    sv.id
FROM commande cmd,
     mode_paiement mp,
     statut_vente sv
WHERE cmd.commentaire = 'Commande de test ventes annulee'
  AND mp.libelle = 'Espèces'
  AND sv.libelle = 'Annulée'
  AND NOT EXISTS (
      SELECT 1
      FROM vente v
      WHERE v.id_commande = cmd.id
  );

INSERT INTO ligne_vente (
    id_vente,
    id_produit,
    quantite,
    prix_unitaire,
    montant
)
SELECT
    v.id,
    p.id,
    2,
    25000,
    50000
FROM vente v,
     commande cmd,
     produit p
WHERE v.id_commande = cmd.id
  AND cmd.commentaire = 'Commande de test ventes validee'
  AND p.ref = 'TEST-VENTE-001'
  AND NOT EXISTS (
      SELECT 1
      FROM ligne_vente lv
      WHERE lv.id_vente = v.id
        AND lv.id_produit = p.id
  );

INSERT INTO ligne_vente (
    id_vente,
    id_produit,
    quantite,
    prix_unitaire,
    montant
)
SELECT
    v.id,
    p.id,
    1,
    15000,
    15000
FROM vente v,
     commande cmd,
     produit p
WHERE v.id_commande = cmd.id
  AND cmd.commentaire = 'Commande de test ventes validee'
  AND p.ref = 'TEST-VENTE-002'
  AND NOT EXISTS (
      SELECT 1
      FROM ligne_vente lv
      WHERE lv.id_vente = v.id
        AND lv.id_produit = p.id
  );

INSERT INTO ligne_vente (
    id_vente,
    id_produit,
    quantite,
    prix_unitaire,
    montant
)
SELECT
    v.id,
    p.id,
    1,
    15000,
    15000
FROM vente v,
     commande cmd,
     produit p
WHERE v.id_commande = cmd.id
  AND cmd.commentaire = 'Commande de test ventes annulee'
  AND p.ref = 'TEST-VENTE-002'
  AND NOT EXISTS (
      SELECT 1
      FROM ligne_vente lv
      WHERE lv.id_vente = v.id
        AND lv.id_produit = p.id
  );

INSERT INTO facture (
    id_vente,
    numero,
    date_emission,
    montant_ht,
    taux_tva,
    montant_tva,
    montant_ttc
)
SELECT
    v.id,
    'FAC-VENTE-TEST-001',
    CURRENT_DATE,
    65000,
    0,
    0,
    65000
FROM vente v,
     commande cmd
WHERE v.id_commande = cmd.id
  AND cmd.commentaire = 'Commande de test ventes validee'
  AND NOT EXISTS (
      SELECT 1
      FROM facture f
      WHERE f.id_vente = v.id
  );

SELECT 'Donnees de test ventes inserees.' AS message;