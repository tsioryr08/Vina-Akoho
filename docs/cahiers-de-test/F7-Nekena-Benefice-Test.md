# CAHIER DE TESTS — MODULE CALCUL DES BÉNÉFICES (VINA AKOHO)

Ce document récapitule les scénarios de tests essentiels pour valider la gestion, l'exactitude des calculs, et la robustesse des filtres du tableau de bord de rentabilité financière.

---

## 1. FILTRES ET GESTION DES PÉRIODES

| ID | Description | Étapes de test | Résultat Attendu | Statut |
| :--- | :--- | :--- | :--- | :--- |
| **TEST-01** | Initialisation automatique des dates | Accéder à la page `/api/benefices` pour la première fois (sans paramètres). | * Date début = 1er jour du mois en cours.<br>* Date fin = Date du jour. | **OK** |
| **TEST-02** | Inversion des dates (Sécurité) | Saisir une **Date de début** ultérieure à la **Date de fin** (ex: Début: `05/07/2026`, Fin: `01/07/2026`) et cliquer sur *Recalculer*. | * L'application ne plante pas (Pas d'Erreur 500).<br>* Un message d'alerte rouge s'affiche : *"La date de début ne peut pas être ultérieure à la date de fin."* | **OK** |
| **TEST-03** | Prise en compte de la plage horaire | Enregistrer une vente à 16h30 le dernier jour de la période (ex: `03/07/2026`) et filtrer jusqu'au `03/07/2026`. | La vente est incluse à 100% dans le calcul (Validation de la gestion de l'heure max de fin). | **OK** |

---

## 2. EXACTITUDE DES CALCULS FINANCIERS

*Les données attendues se basent sur le jeu de données de test validé pour la période du **01/07/2026 au 03/07/2026** (disponible en Annexe).*

| ID | Description | Étapes de test | Résultat Attendu | Statut |
| :--- | :--- | :--- | :--- | :--- |
| **TEST-04** | Calcul du Total des Recettes | Appliquer le filtre du `01/07/2026` au `03/07/2026` (Toutes catégories). | Affiche exactement **`+ 2 500 000.00 Ar`** (Texte en vert, uniquement les ventes avec le statut *Validée*). | **OK** |
| **TEST-05** | Calcul du Total des Dépenses | Appliquer le filtre du `01/07/2026` au `03/07/2026` (Toutes catégories). | Affiche exactement **`- 1 150 000.00 Ar`** (Texte en rouge). | **OK** |
| **TEST-06** | Calcul du Bénéfice Net (Solde) | Recalculer la même période. | Affiche **`1 350 000.00 Ar`** (Style au vert car positif : Recettes > Dépenses). | **OK** |

---

## 3. FILTRAGE ANALYTIQUE (PAR CATÉGORIE)

| ID | Description | Étapes de test | Résultat Attendu | Statut |
| :--- | :--- | :--- | :--- | :--- |
| **TEST-07** | Isolement d'une catégorie active | Sélectionner la catégorie **"Logistique & Transport"** dans la liste déroulante et valider. | * Recettes : `2 500 000.00 Ar` (Inchangé)<br>* Dépenses : `150 000.00 Ar` (Seul le carburant est isolé)<br>* Bénéfice net recalculé : `2 350 000.00 Ar` | **OK** |
| **TEST-08** | Catégorie sans dépenses | Sélectionner la catégorie **"Frais Généraux"** (Aucun mouvement enregistré en base). | * Recettes : `2 500 000.00 Ar`<br>* Dépenses : `0.00 Ar`<br>* Bénéfice net : `2 500 000.00 Ar` | **OK** |

---

## NOTES DE SÉCURITÉ ET RÈGLES DE GESTION

1. **Statut des Ventes :** Seules les ventes au statut `Validée` (ID = 1) entrent dans le calcul des recettes. Les ventes `Annulée` ou `En attente` sont rigoureusement ignorées.
2. **Robustesse UI :** Le modèle conserve toujours une structure `RapportBeneficeDTO` valide, même lors du déclenchement d'erreurs de saisie (évite les crashs HTTP 500 sur Thymeleaf).

---

##  ANNEXE : JEU DE DONNÉES DE TEST (SQL)

Pour exécuter les scénarios ci-dessus, injectez ce script dans votre base de données `vinakoho` afin d'obtenir les volumes attendus pour la période du **01/07/2026 au 03/07/2026**.

```sql
-- ====================================================================
-- 1. PRÉ-REQUIS POUR CLIENT & COMMANDE (Si non présents via d'autres scripts)
-- ====================================================================
INSERT INTO service (libelle, description) 
VALUES ('Service Commercial', 'Gestion des ventes et clients') 
ON CONFLICT (libelle) DO NOTHING;

INSERT INTO type_client (libelle) 
VALUES ('Grossiste') 
ON CONFLICT (libelle) DO NOTHING;

-- ====================================================================
-- 2. INSERTION DU CLIENT
-- ====================================================================
INSERT INTO client (id, nom, prenom, id_service, id_typeClient, is_actif, est_supprimer) 
VALUES (
    1, 
    'Test', 
    'Client', 
    (SELECT id FROM service WHERE libelle = 'Service Commercial' LIMIT 1), 
    (SELECT id FROM type_client WHERE libelle = 'Grossiste' LIMIT 1), 
    true, 
    false
) 
ON CONFLICT (id) DO NOTHING;

-- ====================================================================
-- 3. INSERTION DES COMMANDES
-- ====================================================================
INSERT INTO commande (id, id_client, id_statut_commande, date_commande, commentaire) 
VALUES 
(1, 1, 1, '2026-07-01 10:00:00', 'Commande test juillet A'),
(2, 1, 1, '2026-07-02 11:30:00', 'Commande test juillet B'),
(3, 1, 1, '2026-07-03 09:15:00', 'Commande test juillet C')
ON CONFLICT (id) DO NOTHING;

-- ====================================================================
-- 4. INSERTION DES VENTES (RECETTES) -> TOTAL : 2 500 000.00 Ar
-- ====================================================================
INSERT INTO vente (id, id_commande, date_vente, montant_total, id_mode_paiement, id_statut_vente) 
VALUES 
(1, 1, '2026-07-01 14:30:00', 500000.00, 1, 1),
(2, 2, '2026-07-02 16:00:00', 1250000.00, 3, 1),
(3, 3, '2026-07-03 10:00:00', 750000.00, 1, 1)
ON CONFLICT (id) DO NOTHING;

-- ====================================================================
-- 5. PRÉ-REQUIS POUR DÉPENSES & ENFIN LES DÉPENSES -> TOTAL : 1 150 000.00 Ar
-- ====================================================================
INSERT INTO categorie_depense (id, libelle) VALUES 
(1, 'Achat Matières Premières'),
(2, 'Logistique & Transport'),
(3, 'Main d''oeuvre')
ON CONFLICT (id) DO NOTHING;

INSERT INTO phase (id, libelle, description) 
VALUES (1, 'Phase Initiale', 'Lancement du cycle de production')
ON CONFLICT (id) DO NOTHING;

INSERT INTO statut_depense (id, libelle) 
VALUES (1, 'Payé')
ON CONFLICT (id) DO NOTHING;

INSERT INTO depense (id, date, designation, id_categorie_depense, id_phase, montant, id_statut_depense) 
VALUES 
(1, '2026-07-01', 'Achat de sacs d''aliments', 1, 1, 800000.00, 1), 
(2, '2026-07-02', 'Carburant pour livraison Mahitsy', 2, 1, 150000.00, 1), 
(3, '2026-07-03', 'Rémunération journalière ouvriers', 3, 1, 200000.00, 1)
ON CONFLICT (id) DO NOTHING;

-- ====================================================================
-- 6. SYNCHRONISATION DES COMPTEURS DE SÉQUENCES
-- ====================================================================
SELECT setval(pg_get_serial_sequence('service', 'id'), COALESCE(MAX(id), 1)) FROM service;
SELECT setval(pg_get_serial_sequence('type_client', 'id'), COALESCE(MAX(id), 1)) FROM type_client;
SELECT setval(pg_get_serial_sequence('client', 'id'), COALESCE(MAX(id), 1)) FROM client;
SELECT setval(pg_get_serial_sequence('commande', 'id'), COALESCE(MAX(id), 1)) FROM commande;
SELECT setval(pg_get_serial_sequence('vente', 'id'), COALESCE(MAX(id), 1)) FROM vente;
SELECT setval(pg_get_serial_sequence('categorie_depense', 'id'), COALESCE(MAX(id), 1)) FROM categorie_depense;
SELECT setval(pg_get_serial_sequence('phase', 'id'), COALESCE(MAX(id), 1)) FROM phase;
SELECT setval(pg_get_serial_sequence('statut_depense', 'id'), COALESCE(MAX(id), 1)) FROM statut_depense;
SELECT setval(pg_get_serial_sequence('depense', 'id'), COALESCE(MAX(id), 1)) FROM depense;