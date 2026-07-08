-- Ventes réparties sur les 30 derniers jours, plusieurs produits/catégories.
-- La répartition est volontairement contrastée pour obtenir une courbe
-- non monotone (montée / baisse / remontée) selon les filtres.
-- + 1 Annulée et 1 En attente de paiement pour vérifier l'exclusion.

DO $$
DECLARE
  v_id_client_1 INTEGER := (SELECT id FROM client WHERE numero_telephone = '0340000001' ORDER BY id LIMIT 1);
  v_id_client_2 INTEGER := (SELECT id FROM client WHERE numero_telephone = '0340000002' ORDER BY id LIMIT 1);
  v_id_mode INTEGER := (SELECT id FROM mode_paiement WHERE libelle = 'Espèces');
  v_id_valide INTEGER := (SELECT id FROM statut_vente WHERE libelle = 'Validée');
  v_id_annulee INTEGER := (SELECT id FROM statut_vente WHERE libelle = 'Annulée');
  v_id_attente INTEGER := (SELECT id FROM statut_vente WHERE libelle = 'En attente');
  v_id_poussin INTEGER := (SELECT id FROM produit WHERE ref = 'PRD-001');
  v_id_croissance INTEGER := (SELECT id FROM produit WHERE ref = 'TST-REC-001');
  v_id_finition INTEGER := (SELECT id FROM produit WHERE ref = 'TST-REC-002');
  v_id_vente INTEGER;
BEGIN
  -- 8 ventes "Validée" réparties sur 4 semaines :
  -- Semaine 1 = 225 000 Ar
  -- Semaine 2 = 611 000 Ar
  -- Semaine 3 = 361 000 Ar
  -- Semaine 4 = 604 000 Ar
  -- Cela permet d'obtenir une courbe plus lisible dans l'interface.

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_1, TIMESTAMP '2026-06-16 09:15:00', 100000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_poussin, 4, 25000, 100000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_2, TIMESTAMP '2026-06-19 11:30:00', 125000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_croissance, 1, 125000, 125000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_1, TIMESTAMP '2026-06-24 14:00:00', 236000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_finition, 2, 118000, 236000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_2, TIMESTAMP '2026-06-27 10:45:00', 375000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_croissance, 3, 125000, 375000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_1, TIMESTAMP '2026-07-01 08:30:00', 125000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_poussin, 5, 25000, 125000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_2, TIMESTAMP '2026-07-03 15:10:00', 236000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_finition, 2, 118000, 236000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_1, TIMESTAMP '2026-07-07 09:50:00', 250000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_croissance, 2, 125000, 250000);

  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_2, TIMESTAMP '2026-07-08 16:20:00', 354000, v_id_mode, v_id_valide)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_finition, 3, 118000, 354000);

  -- 1 vente Annulée (ne doit PAS apparaître dans les stats)
  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_1, TIMESTAMP '2026-06-25 12:00:00', 125000, v_id_mode, v_id_annulee)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_croissance, 1, 125000, 125000);

  -- 1 vente En attente de paiement (ne doit PAS apparaître non plus)
  INSERT INTO vente (id_client, date_vente, montant_total, id_mode_paiement, id_statut_vente)
  VALUES (v_id_client_2, TIMESTAMP '2026-07-02 17:15:00', 50000, v_id_mode, v_id_attente)
  RETURNING id INTO v_id_vente;
  INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire, montant)
  VALUES (v_id_vente, v_id_poussin, 2, 25000, 50000);
END $$;
