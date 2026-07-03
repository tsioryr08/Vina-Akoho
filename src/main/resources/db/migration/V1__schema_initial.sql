


-- ============================================
-- 1. RÉFÉRENTIELS
-- ============================================

CREATE TABLE unite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    poste VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE type_client (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mode_paiement (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorie_depense (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE phase (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statut_commande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statut_vente (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statut_livraison (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statut_depense (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE type_mouvement (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE type_prix (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 2. EMPLOYÉS (avant les références)
-- ============================================

CREATE TABLE employe (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    contact VARCHAR(20),
    mdp VARCHAR(255) NOT NULL,
    id_role INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_role) REFERENCES role(id) ON DELETE RESTRICT
);
CREATE INDEX idx_employe_role ON employe(id_role);

-- ============================================
-- 3. PRODUITS & CATÉGORIES
-- ============================================

CREATE TABLE categorie (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    pourcentage_proteine DECIMAL(5, 2) CHECK (pourcentage_proteine BETWEEN 0 AND 100),
    pourcentage_matiere_grasses DECIMAL(5, 2) CHECK (pourcentage_matiere_grasses BETWEEN 0 AND 100),
    pourcentage_humidite_max DECIMAL(5, 2) CHECK (pourcentage_humidite_max BETWEEN 0 AND 100),
    marge_pourcentage DECIMAL(5, 2) CHECK (marge_pourcentage >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE produit (
    id SERIAL PRIMARY KEY,
    ref VARCHAR(100) NOT NULL UNIQUE,
    id_categorie INTEGER NOT NULL,
    nom VARCHAR(150) NOT NULL UNIQUE,
    prix_vente DECIMAL(10, 2) NOT NULL CHECK (prix_vente >= 0),
    seuil_alerte INTEGER CHECK (seuil_alerte IS NULL OR seuil_alerte >= 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categorie) REFERENCES categorie(id) ON DELETE RESTRICT
);
CREATE INDEX idx_produit_categorie ON produit(id_categorie);

CREATE TABLE historique_prix (
    id SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    id_type_prix INTEGER NOT NULL,
    ancien_prix DECIMAL(10, 2) NOT NULL CHECK (ancien_prix >= 0),
    nouveau_prix DECIMAL(10, 2) NOT NULL CHECK (nouveau_prix >= 0),
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_employe INTEGER,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE CASCADE,
    FOREIGN KEY (id_type_prix) REFERENCES type_prix(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE SET NULL
);
CREATE INDEX idx_historique_prix_produit ON historique_prix(id_produit);
CREATE INDEX idx_historique_prix_type_prix ON historique_prix(id_type_prix);
CREATE INDEX idx_historique_prix_employe ON historique_prix(id_employe);

-- ============================================
-- 4. MATIÈRE PREMIÈRE & FOURNISSEUR
-- ============================================

CREATE TABLE fournisseur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE matiere_premiere (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    id_fournisseur INTEGER NOT NULL,
    cout_unitaire DECIMAL(10, 2) NOT NULL CHECK (cout_unitaire >= 0),
    id_unite INTEGER NOT NULL,
    seuil_minimum DECIMAL(10, 2) CHECK (seuil_minimum IS NULL OR seuil_minimum >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE RESTRICT
);
CREATE INDEX idx_mp_fournisseur ON matiere_premiere(id_fournisseur);
CREATE INDEX idx_mp_unite ON matiere_premiere(id_unite);

CREATE TABLE lot_mp (
    id SERIAL PRIMARY KEY,
    id_mp INTEGER NOT NULL,
    quantite_initiale DECIMAL(10, 2) NOT NULL CHECK (quantite_initiale >= 0),
    quantite_restante DECIMAL(10, 2) NOT NULL CHECK (quantite_restante >= 0),
    date_achat DATE NOT NULL,
    date_peremption DATE, -- (correction #1) NULL autorise : pas toutes les MP ne perissent
    id_mouvement_entree INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mp) REFERENCES matiere_premiere(id) ON DELETE RESTRICT,
    CONSTRAINT chk_lot_mp_quantite CHECK (quantite_restante <= quantite_initiale),
    CONSTRAINT chk_lot_mp_peremption CHECK (date_peremption IS NULL OR date_peremption >= date_achat)
);
CREATE INDEX idx_lot_mp_mp ON lot_mp(id_mp);


CREATE TABLE recette_produit (
    id SERIAL PRIMARY KEY,
    id_categorie INTEGER NOT NULL,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    id_mp INTEGER NOT NULL,
    quantite_mp DECIMAL(10, 2) NOT NULL CHECK (quantite_mp > 0),
    id_unite INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_fin TIMESTAMP,
    id_employe_creation INTEGER,
    FOREIGN KEY (id_categorie) REFERENCES categorie(id) ON DELETE CASCADE,
    FOREIGN KEY (id_mp) REFERENCES matiere_premiere(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_employe_creation) REFERENCES employe(id) ON DELETE SET NULL,
    CONSTRAINT chk_recette_dates CHECK (date_fin IS NULL OR date_fin >= date_creation),
    CONSTRAINT uq_recette_categorie_version_mp UNIQUE (id_categorie, version, id_mp)
);
CREATE INDEX idx_recette_categorie ON recette_produit(id_categorie);
CREATE INDEX idx_recette_mp ON recette_produit(id_mp);
CREATE INDEX idx_recette_unite ON recette_produit(id_unite);
CREATE INDEX idx_recette_employe_creation ON recette_produit(id_employe_creation);
CREATE UNIQUE INDEX uq_recette_active_par_categorie
    ON recette_produit(id_categorie)
    WHERE is_active = TRUE;


CREATE TABLE mouvement_stock_mp (
    id SERIAL PRIMARY KEY,
    id_type_mouvement INTEGER NOT NULL,
    date_mouvement DATE DEFAULT CURRENT_DATE,
    id_lot_mp INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite <> 0),
    id_unite INTEGER NOT NULL,
    id_employe INTEGER NOT NULL,
    observation TEXT,
    reference_document VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_type_mouvement) REFERENCES type_mouvement(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_lot_mp) REFERENCES lot_mp(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE RESTRICT
);
CREATE INDEX idx_mvt_mp_type ON mouvement_stock_mp(id_type_mouvement);
CREATE INDEX idx_mvt_mp_lot ON mouvement_stock_mp(id_lot_mp);
CREATE INDEX idx_mvt_mp_unite ON mouvement_stock_mp(id_unite);
CREATE INDEX idx_mvt_mp_employe ON mouvement_stock_mp(id_employe);

CREATE TABLE mouvement_stock_produit (
    id SERIAL PRIMARY KEY,
    id_type_mouvement INTEGER NOT NULL,
    date_mouvement DATE DEFAULT CURRENT_DATE,
    id_lot_produit INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite <> 0),
    id_unite INTEGER NOT NULL,
    id_employe INTEGER NOT NULL,
    observation TEXT,
    reference_document VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_type_mouvement) REFERENCES type_mouvement(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE RESTRICT
);
CREATE INDEX idx_mvt_produit_type ON mouvement_stock_produit(id_type_mouvement);
CREATE INDEX idx_mvt_produit_unite ON mouvement_stock_produit(id_unite);
CREATE INDEX idx_mvt_produit_employe ON mouvement_stock_produit(id_employe);


CREATE TABLE lot_produit (
    id SERIAL PRIMARY KEY,
    id_produit INTEGER NOT NULL,
    quantite_initiale DECIMAL(10, 2) NOT NULL CHECK (quantite_initiale >= 0),
    quantite_restante DECIMAL(10, 2) NOT NULL CHECK (quantite_restante >= 0),
    date_fabrication DATE NOT NULL,
    date_peremption DATE, -- (correction #1) NULL autorise
    id_mouvement_entree INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE RESTRICT,
    CONSTRAINT chk_lot_produit_quantite CHECK (quantite_restante <= quantite_initiale),
    CONSTRAINT chk_lot_produit_peremption CHECK (date_peremption IS NULL OR date_peremption >= date_fabrication)
);
CREATE INDEX idx_lot_produit_produit ON lot_produit(id_produit);


ALTER TABLE lot_mp
ADD CONSTRAINT fk_lot_mp_mouvement_entree
    FOREIGN KEY (id_mouvement_entree) REFERENCES mouvement_stock_mp(id)
    ON DELETE SET NULL
    DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE mouvement_stock_produit
ADD CONSTRAINT fk_mouvement_stock_produit_lot
    FOREIGN KEY (id_lot_produit) REFERENCES lot_produit(id)
    ON DELETE RESTRICT
    DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX idx_mvt_produit_lot ON mouvement_stock_produit(id_lot_produit);

ALTER TABLE lot_produit
ADD CONSTRAINT fk_lot_produit_mouvement_entree
    FOREIGN KEY (id_mouvement_entree) REFERENCES mouvement_stock_produit(id)
    ON DELETE SET NULL
    DEFERRABLE INITIALLY DEFERRED;

-- ============================================
-- 6. FABRICATION
-- ============================================

CREATE TABLE fabrication (
    id SERIAL PRIMARY KEY,
    date_fabrication TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantite_produite DECIMAL(10, 2) NOT NULL CHECK (quantite_produite > 0),
    id_lot_produit INTEGER NOT NULL,
    id_employe INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_lot_produit) REFERENCES lot_produit(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE RESTRICT
);
CREATE INDEX idx_fabrication_lot_produit ON fabrication(id_lot_produit);
CREATE INDEX idx_fabrication_employe ON fabrication(id_employe);

CREATE TABLE fabrication_mp (
    id SERIAL PRIMARY KEY,
    id_fabrication INTEGER NOT NULL,
    id_lot_mp INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite > 0),
    id_unite INTEGER NOT NULL,
    FOREIGN KEY (id_fabrication) REFERENCES fabrication(id) ON DELETE CASCADE,
    FOREIGN KEY (id_lot_mp) REFERENCES lot_mp(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_unite) REFERENCES unite(id) ON DELETE RESTRICT,
    CONSTRAINT uq_fabrication_lot_mp UNIQUE (id_fabrication, id_lot_mp)
);
CREATE INDEX idx_fabrication_mp_fabrication ON fabrication_mp(id_fabrication);
CREATE INDEX idx_fabrication_mp_lot ON fabrication_mp(id_lot_mp);
CREATE INDEX idx_fabrication_mp_unite ON fabrication_mp(id_unite);

-- ============================================
-- 7. CLIENTS & LIVREURS
-- ============================================


CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_inscription DATE DEFAULT CURRENT_DATE,
    is_actif BOOLEAN DEFAULT TRUE,
    numero_telephone VARCHAR(20),
    adresse TEXT,
    id_localite VARCHAR(100),
    id_zone_livraison VARCHAR(100),
    notes TEXT,
    id_service INTEGER NOT NULL,
    id_typeClient INTEGER NOT NULL,
    taille_cheptel INTEGER CHECK (taille_cheptel IS NULL OR taille_cheptel >= 0),
    est_supprimer BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_service) REFERENCES service(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_typeClient) REFERENCES type_client(id) ON DELETE RESTRICT
);
CREATE INDEX idx_client_service ON client(id_service);
CREATE INDEX idx_client_typeclient ON client(id_typeClient);

CREATE TABLE livreur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    contact VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 8. COMMANDES
-- ============================================

CREATE TABLE commande (
    id SERIAL PRIMARY KEY,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_client INTEGER NOT NULL,
    id_statut_commande INTEGER NOT NULL,
    commentaire TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_client) REFERENCES client(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_statut_commande) REFERENCES statut_commande(id) ON DELETE RESTRICT
);
CREATE INDEX idx_commande_client ON commande(id_client);
CREATE INDEX idx_commande_statut ON commande(id_statut_commande);

CREATE TABLE ligne_commande (
    id SERIAL PRIMARY KEY,
    id_commande INTEGER NOT NULL,
    id_produit INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL CHECK (prix_unitaire >= 0),
    FOREIGN KEY (id_commande) REFERENCES commande(id) ON DELETE CASCADE,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ligne_commande UNIQUE (id_commande, id_produit)
);
CREATE INDEX idx_ligne_commande_commande ON ligne_commande(id_commande);
CREATE INDEX idx_ligne_commande_produit ON ligne_commande(id_produit);

-- ============================================
-- 9. VENTES
-- ============================================

CREATE TABLE vente (
    id SERIAL PRIMARY KEY,
    id_commande INTEGER NOT NULL,
    date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant_total DECIMAL(12, 2) NOT NULL CHECK (montant_total >= 0),
    id_mode_paiement INTEGER NOT NULL,
    id_statut_vente INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_commande) REFERENCES commande(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_mode_paiement) REFERENCES mode_paiement(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_statut_vente) REFERENCES statut_vente(id) ON DELETE RESTRICT
);
CREATE INDEX idx_vente_commande ON vente(id_commande);
CREATE INDEX idx_vente_mode_paiement ON vente(id_mode_paiement);
CREATE INDEX idx_vente_statut ON vente(id_statut_vente);

CREATE TABLE ligne_vente (
    id SERIAL PRIMARY KEY,
    id_vente INTEGER NOT NULL,
    id_produit INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL CHECK (prix_unitaire >= 0),
    montant DECIMAL(12, 2) NOT NULL CHECK (montant >= 0),
    FOREIGN KEY (id_vente) REFERENCES vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_produit) REFERENCES produit(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ligne_vente UNIQUE (id_vente, id_produit)
);
CREATE INDEX idx_ligne_vente_vente ON ligne_vente(id_vente);
CREATE INDEX idx_ligne_vente_produit ON ligne_vente(id_produit);

CREATE TABLE ligne_vente_lot (
    id SERIAL PRIMARY KEY,
    id_ligne_vente INTEGER NOT NULL,
    id_lot_produit INTEGER NOT NULL,
    quantite DECIMAL(10, 2) NOT NULL CHECK (quantite > 0),
    FOREIGN KEY (id_ligne_vente) REFERENCES ligne_vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_lot_produit) REFERENCES lot_produit(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ligne_vente_lot UNIQUE (id_ligne_vente, id_lot_produit)
);
CREATE INDEX idx_lvl_ligne_vente ON ligne_vente_lot(id_ligne_vente);
CREATE INDEX idx_lvl_lot_produit ON ligne_vente_lot(id_lot_produit);

-- ============================================
-- 10. FACTURES
-- ============================================

CREATE TABLE facture (
    id SERIAL PRIMARY KEY,
    id_vente INTEGER NOT NULL UNIQUE, 
    numero VARCHAR(50) NOT NULL UNIQUE,
    date_emission DATE DEFAULT CURRENT_DATE,
    montant_ht DECIMAL(12, 2) NOT NULL CHECK (montant_ht >= 0),
    taux_tva DECIMAL(5, 2) NOT NULL DEFAULT 0 CHECK (taux_tva BETWEEN 0 AND 100),
    montant_tva DECIMAL(12, 2) NOT NULL CHECK (montant_tva >= 0),
    montant_ttc DECIMAL(12, 2) NOT NULL CHECK (montant_ttc >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_vente) REFERENCES vente(id) ON DELETE RESTRICT,
    CONSTRAINT chk_facture_ttc CHECK (montant_ttc = montant_ht + montant_tva)
);

-- ============================================
-- 11. LIVRAISONS
-- ============================================

CREATE TABLE livraison (
    id SERIAL PRIMARY KEY,
    id_commande INTEGER NOT NULL,
    id_livreur INTEGER,
    lieu_exact TEXT,
    contact VARCHAR(20),
    date_livraison DATE,
    commentaire TEXT,
    id_statut_livraison INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_commande) REFERENCES commande(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_livreur) REFERENCES livreur(id) ON DELETE SET NULL,
    FOREIGN KEY (id_statut_livraison) REFERENCES statut_livraison(id) ON DELETE RESTRICT
);
CREATE INDEX idx_livraison_commande ON livraison(id_commande);
CREATE INDEX idx_livraison_livreur ON livraison(id_livreur);
CREATE INDEX idx_livraison_statut ON livraison(id_statut_livraison);

CREATE TABLE historique_statut_livraison (
    id SERIAL PRIMARY KEY,
    id_livraison INTEGER NOT NULL,
    ancien_statut INTEGER,
    nouveau_statut INTEGER NOT NULL,
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_livraison) REFERENCES livraison(id) ON DELETE CASCADE,
    FOREIGN KEY (ancien_statut) REFERENCES statut_livraison(id) ON DELETE SET NULL,
    FOREIGN KEY (nouveau_statut) REFERENCES statut_livraison(id) ON DELETE RESTRICT
);
CREATE INDEX idx_hist_livraison_livraison ON historique_statut_livraison(id_livraison);
CREATE INDEX idx_hist_livraison_ancien ON historique_statut_livraison(ancien_statut);
CREATE INDEX idx_hist_livraison_nouveau ON historique_statut_livraison(nouveau_statut);

-- ============================================
-- 12. DÉPENSES
-- ============================================

CREATE TABLE depense (
    id SERIAL PRIMARY KEY,
    date DATE DEFAULT CURRENT_DATE,
    designation VARCHAR(255) NOT NULL,
    id_categorie_depense INTEGER NOT NULL,
    id_phase INTEGER NOT NULL,
    montant DECIMAL(12, 2) NOT NULL CHECK (montant >= 0),
    id_statut_depense INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categorie_depense) REFERENCES categorie_depense(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_phase) REFERENCES phase(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_statut_depense) REFERENCES statut_depense(id) ON DELETE RESTRICT
);
CREATE INDEX idx_depense_categorie ON depense(id_categorie_depense);
CREATE INDEX idx_depense_phase ON depense(id_phase);
CREATE INDEX idx_depense_statut ON depense(id_statut_depense);

CREATE TABLE depense_lot (
    id SERIAL PRIMARY KEY,
    id_depense INTEGER NOT NULL,
    id_lot_produit INTEGER,
    id_lot_mp INTEGER,
    FOREIGN KEY (id_depense) REFERENCES depense(id) ON DELETE CASCADE,
    FOREIGN KEY (id_lot_produit) REFERENCES lot_produit(id) ON DELETE CASCADE,
    FOREIGN KEY (id_lot_mp) REFERENCES lot_mp(id) ON DELETE CASCADE,
    CHECK (
        (id_lot_produit IS NOT NULL AND id_lot_mp IS NULL) OR
        (id_lot_produit IS NULL AND id_lot_mp IS NOT NULL) OR
        (id_lot_produit IS NULL AND id_lot_mp IS NULL)
    )
);
CREATE INDEX idx_depense_lot_depense ON depense_lot(id_depense);
CREATE INDEX idx_depense_lot_lot_produit ON depense_lot(id_lot_produit);
CREATE INDEX idx_depense_lot_lot_mp ON depense_lot(id_lot_mp);
