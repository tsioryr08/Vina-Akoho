# Rapport Technique - F4 Historique des Ventes

**Module :** F4 — Ventes (Historique)  
**Responsable :** Ny Antema  
**Sprint :** 3  
**Date :** 06/07/2026

---

## 1. Description du module

Ce module permet d'afficher et de gérer l'historique complet des ventes avec des fonctionnalités avancées de recherche, filtrage, tri et pagination. Il inclut également la gestion de la livraison, l'annulation de commande et la validation de paiement.

### Objectifs

- Afficher la liste complète des ventes avec pagination
- Rechercher des ventes par client, produit ou numéro de facture
- Filtrer les ventes par date, mode de paiement, statut et livraison
- Trier les ventes par date, montant ou client
- Afficher les détails complets d'une vente
- Gérer les livraisons associées aux ventes
- Annuler les commandes en attente de paiement
- Valider les paiements

---

## 2. Structure des fichiers

### 2.1. Entités

**Fichiers existants (Sprint 2.1) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/Vente.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/LigneVente.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/Facture.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/ModePaiement.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/ventes/StatutVente.java`

**Fichiers ajoutés (Sprint 3) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/livraison/Livraison.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/livraison/StatutLivraison.java`

**Relations :**
- `Vente` → `Livraison` (one-to-one, optionnelle)
- `Livraison` → `StatutLivraison`
- `Livraison` → `Employe` (livreur)

### 2.2. DTOs

**Fichiers existants (Sprint 2.1) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/VenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/LigneVenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/FactureDTO.java`

**Fichiers ajoutés (Sprint 3) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/livraison/LivraisonDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/livraison/LivraisonFormDTO.java`

**Modifications apportées à `VenteDTO.java` :**
- Ajout du champ `livraison` de type `LivraisonDTO` pour inclure les informations de livraison

### 2.3. Repositories

**Fichiers existants (Sprint 2.1) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/VenteRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/FactureRepository.java`

**Fichiers ajoutés (Sprint 3) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/livraison/LivraisonRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/livraison/StatutLivraisonRepository.java`

**Méthodes ajoutées dans `LivraisonRepository` :**
- `Optional<Livraison> findByVenteId(Long venteId)` : Récupère la livraison associée à une vente

### 2.4. Services

**Fichiers existants (Sprint 2.1) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/VenteService.java`

**Fichiers ajoutés (Sprint 3) :**
- `src/main/java/mg/vinaAkoho/vina_akoho/service/livraison/LivraisonService.java`

**Modifications apportées à `VenteService.java` :**

1. **Ajout de dépendances :**
   - `LivraisonService`
   - `LivraisonRepository`
   - `StatutLivraisonRepository`

2. **Modification de la méthode `versDTO(Vente vente)` :**
   - Récupération des informations de livraison si elles existent
   - Gestion des erreurs avec try-catch pour éviter les crashes
   - Création de DTO minimal en cas d'échec de `versDTO`

3. **Modification de la méthode `creer()` :**
   - Création automatique d'une livraison si `livraisonRequise` est true
   - Association de la livraison à la vente créée

4. **Modification de la méthode `validerPaiement()` :**
   - Changement de statut vers "En livraison" si une livraison est requise
   - Synchronisation avec le statut de livraison

5. **Ajout de la méthode `annulerVente()` :**
   - Annulation autorisée uniquement si statut = "En attente de paiement"
   - Changement de statut vers "Annulée"

**Modifications apportées à `LivraisonService.java` :**
- Rendre la méthode `versDTO()` publique pour être appelée depuis `VenteService`

### 2.5. Controller

**Fichier :** `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/VenteController.java`

**Modifications apportées à la méthode `listerTous()` :**

1. **Ajout de paramètres :**
   - `recherche` : Recherche textuelle (client, produit, facture)
   - `modePaiement` : Filtre par mode de paiement
   - `statut` : Filtre par statut
   - `avecLivraison` : Filtre par livraison (true/false)
   - `dateDebut` / `dateFin` : Filtre par période
   - `triPar` : Critère de tri (dateVente, montantTotal, clientNom)
   - `ordreTri` : Ordre de tri (asc/desc)

2. **Logique de traitement en 3 étapes indépendantes :**
   - Étape 1 : Recherche textuelle (indépendante des filtres)
   - Étape 2 : Filtres (indépendants de la recherche et du tri)
   - Étape 3 : Tri (indépendant de la recherche et des filtres)

3. **Tri par défaut :**
   - Tri automatique par date décroissante si aucun critère de tri spécifié

**Fonctionnalités existantes :**
- Gestion du panier via HttpSession
- Création de vente avec validation
- Liste des ventes avec filtres
- Validation de paiement
- Annulation de commande
- Affichage des factures et bons de livraison

### 2.6. Templates

**Fichiers existants (Sprint 2.1) :**
- `src/main/resources/templates/ventes/responsable-commercial-ventes.html`
- `src/main/resources/templates/ventes/responsable-commercial-ventes-nouvelles.html`
- `src/main/resources/templates/ventes/responsable-commercial-ventes-detail.html`

**Modifications apportées à `responsable-commercial-ventes.html` :**

1. **Restructuration de l'interface en 3 sections indépendantes :**
   - Section Recherche : Champ de recherche textuelle
   - Section Filtres : Mode de paiement, Statut, Livraison, Période
   - Section Tri : Critère de tri, Ordre de tri

2. **Ajout de champs cachés :**
   - Préservation des paramètres entre les sections
   - Chaque formulaire contient les paramètres des autres sections

3. **Ajout du filtre Livraison :**
   - Select avec options : Toutes, Avec livraison, Sans livraison

**Modifications apportées à `responsable-commercial-ventes-detail.html` :**

1. **Ajout de la section Livraison :**
   - Affichage conditionnel si `vente.livraison != null`
   - Informations : Adresse, Contact, Date souhaitée, Livreur, Statut
   - Badge de couleur selon le statut de livraison

2. **Correction de l'affichage des unités :**
   - Affichage de l'unité réelle du produit
   - Pas de valeur par défaut incorrecte
   - Affichage conditionnel (seulement si non vide)

3. **Boutons d'action conditionnels :**
   - "Valider le paiement" : visible uniquement si statut = "En attente de paiement"
   - "Annuler la commande" : visible uniquement si statut = "En attente de paiement"

### 2.7. Migrations SQL

**Fichier ajouté :** `src/main/resources/db/migration/V24__statuts_vente_et_livraison.sql`

**Contenu :**
- Ajout de nouveaux statuts de vente : "En préparation", "En livraison", "Livrée"
- Seed de la table `statut_livraison` avec les statuts : "En attente d'affectation", "En cours de livraison", "Livrée", "Échec de livraison"

---

## 3. Règles métier implémentées

### 3.1. Historique des ventes

1. **Pagination** : 10 éléments par défaut, configurable (5, 10, 20, 50)
2. **Recherche** : Recherche textuelle approximative dans client, produit, numéro de facture
3. **Filtres** : Filtres indépendants par date, mode de paiement, statut, livraison
4. **Tri** : Tri indépendant par date, montant, client avec ordre croissant/décroissant
5. **Tri par défaut** : Tri automatique par date décroissante

### 3.2. Gestion de la livraison

1. **Création automatique** : Livraison créée automatiquement si `livraisonRequise` est true
2. **Affichage conditionnel** : Informations de livraison affichées uniquement si elles existent
3. **Gestion des erreurs** : Try-catch pour éviter les crashes lors de la récupération de livraison
4. **DTO minimal** : Création de DTO minimal en cas d'échec de `versDTO`

### 3.3. Annulation de commande

1. **Condition d'annulation** : Annulation autorisée uniquement si statut = "En attente de paiement"
2. **Changement de statut** : Statut passe à "Annulée"
3. **Bouton conditionnel** : Bouton d'annulation visible uniquement pour les commandes annulables
4. **Confirmation** : Confirmation avant annulation

### 3.4. Validation de paiement

1. **Changement de statut** : Statut passe à "Validée" ou "En livraison" si livraison requise
2. **Synchronisation livraison** : Statut de livraison synchronisé si applicable
3. **Bouton conditionnel** : Bouton de validation visible uniquement pour les commandes en attente

### 3.5. Calcul du chiffre d'affaires

1. **Exclusion des commandes annulées** : Les commandes "Annulée" ne sont pas comptées
2. **Exclusion des commandes en attente** : Les commandes "En attente de paiement" ne sont pas comptées
3. **Implémentation** : Règle implémentée dans `VenteController.estVenteRealisee()` et `VenteRepository`

---

## 4. Conformité aux règles backend

###  Points respectés

- **Structure des dossiers** : Respect de la structure par module (`ventes/`, `livraison/`)
- **Conventions de nommage** : PascalCase pour les classes, snake_case pour les tables et colonnes
- **DTOs obligatoires** : Tous les DTOs créés et utilisés (`VenteDTO`, `LivraisonDTO`, etc.)
- **Exceptions personnalisées** : `VenteNotFoundException` existante
- **Tests** : Cahier de test complet avec 19 tests
- **Documentation** : Rapport technique complet
- **Gestion des erreurs** : Try-catch robuste pour éviter les crashes
- **Indépendance des fonctionnalités** : Recherche, filtres et tri indépendants

### Points non applicables

- **Format de réponse API** : Le module utilise Thymeleaf (vues HTML) et non des API JSON

---

## 5. Architecture de l'interface

### 5.1. Structure en 3 sections indépendantes

**Section 1 - Recherche :**
- Champ de recherche textuelle
- Recherche dans : Client, Produit, Numéro de facture
- Recherche approximative (contient le terme)
- Formulaire avec champ caché `avecLivraison`

**Section 2 - Filtres :**
- Filtre par Mode de paiement (sélection exacte)
- Filtre par Statut (Validée, En attente, Annulée)
- Filtre par Livraison (Avec/Sans)
- Filtre par Période (date début/fin)
- Champs cachés : `recherche`, `triPar`, `ordreTri`, `avecLivraison`

**Section 3 - Tri :**
- Trier par : Date, Montant, Client
- Ordre : Croissant, Décroissant
- Champs cachés : `recherche`, `modePaiement`, `statut`, `dateDebut`, `dateFin`, `avecLivraison`

### 5.2. Logique de traitement

**Étape 1 - Recherche :**
- Appliquée sur toutes les ventes
- Filtre par client OU produit OU facture
- Résultat : `ventesRecherchees`

**Étape 2 - Filtres :**
- Appliquée sur `ventesRecherchees`
- Filtres exacts : modePaiement, statut, avecLivraison, période
- Résultat : `ventesFiltrees`

**Étape 3 - Tri :**
- Appliqué sur `ventesFiltrees`
- Tri par critère spécifié ou par défaut (date décroissante)
- Résultat : Liste finale affichée

---

## 6. Documentation

**Cahier de test :** `docs/cahiers-de-test/F4-NyAntema-HistoriqueVentes-Test.md`

**Rapport technique :** `docs/rapports-techniques/F4-NyAntema-HistoriqueVentes-Rapport.md`

---

## 7. Conclusion

Le module F4 Historique des Ventes est entièrement fonctionnel et respecte les conventions de développement définies. Toutes les fonctionnalités de recherche, filtrage, tri et pagination sont opérationnelles et indépendantes. La gestion de la livraison, l'annulation de commande et la validation de paiement fonctionnent correctement avec une gestion robuste des erreurs.

### Corrections apportées pour la conformité

1. **Indépendance Recherche/Filtres/Tri** : Restructuration de l'interface en 3 sections indépendantes
2. **Gestion des erreurs** : Ajout de try-catch pour éviter les crashes lors de la récupération de livraison
3. **Affichage des unités** : Correction pour ne pas utiliser de valeur par défaut incorrecte
4. **Filtre livraison** : Ajout du filtre avec/sans livraison
5. **Documentation** : Création du cahier de test et du rapport technique

### État final

-  Règles métier Sprint 3 respectées
-  Conventions backend respectées
-  Tests complets (19 tests)
-  Documentation complète
-  Gestion des erreurs robuste
-  Interface utilisateur intuitive
-  Fonctionnalités indépendantes

### Points forts

1. **Architecture robuste** : Séparation claire des responsabilités
2. **Gestion des erreurs** : Try-catch pour éviter les crashes
3. **Interface intuitive** : 3 sections indépendantes avec préservation des paramètres
4. **Flexibilité** : Recherche, filtres et tri peuvent être utilisés séparément ou combinés
5. **Performance** : Traitement séquentiel optimisé