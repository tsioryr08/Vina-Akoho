# Rapport Technique - F13 Statistiques Produits & Ventes

**Module :** F13 — Statistiques Produits & Ventes  
**Responsable :** Mpiaro  
**Sprint :** 3  
**Date :** 08/07/2026

---

## 1. Description du module

Ce module permet d’exploiter les ventes enregistrées afin d’afficher, dans le dashboard du responsable commercial, les indicateurs suivants :

- le Top 10 des produits les plus vendus
- le Top des catégories les plus vendues
- les quantités vendues
- les pourcentages de ventes
- le nombre de ventes
- l’évolution des ventes selon la granularité jour, semaine ou mois

L’accès métier se fait depuis le **Dashboard Commercial**, puis via le lien **Produits & Ventes** dans le `layout` du responsable commercial.

---

## 2. Parcours de validation fonctionnelle

### Étapes de test côté interface

1. Se connecter avec un compte du rôle **Responsable Commercial**
2. Arriver sur le **Dashboard Commercial**
3. Cliquer sur le lien **Produits & Ventes** dans la sidebar
4. Vérifier l’affichage de la page de statistiques
5. Contrôler les tableaux et les graphiques
6. Changer les filtres :
   - catégorie
   - tri des produits
   - granularité d’évolution

### Données de validation

Le jeu de données de test est préparé avec :

- `V_31_reset_statistiques_ventes.sql` pour nettoyer les anciennes ventes de test
- `V_32_donnee_test_statistiques.sql` pour réinjecter des ventes cohérentes et contrôlables

Ce nettoyage est nécessaire car des ventes de test antérieures pouvaient fausser :

- les totaux
- les classements
- les pourcentages
- les graphiques

---

## 3. Structure des fichiers

### 3.1. DTOs créés

**Fichiers :**

- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/TopProduitStatDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/TopCategorieStatDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/EvolutionVenteStatDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/StatistiqueVenteReponseDTO.java`

**Rôle :**

- `TopProduitStatDTO` : encapsule les indicateurs d’un produit vendu
- `TopCategorieStatDTO` : encapsule les indicateurs d’une catégorie
- `EvolutionVenteStatDTO` : transporte les données temporelles de l’évolution des ventes
- `StatistiqueVenteReponseDTO` : agrège les trois séries de données pour la page de statistiques

---

### 3.2. Repository modifié

#### `LigneVenteRepository`

**Fichier :**

- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteRepository.java`

**Ajouts :**

- `topProduitsParQuantite(...)`
- `topProduitsParMontant(...)`
- `topCategoriesParQuantite(...)`
- `topCategoriesParMontant(...)`
- `totauxPeriode(...)`

**Logique métier :**

- calcul du Top 10 produits via `Pageable`
- calcul du Top 10 catégories via `Pageable`
- exclusion des ventes au statut :
  - `Annulée`
  - `En attente de paiement`
- calcul des pourcentages à partir du total réel de la période filtrée

#### `VenteRepository`

**Fichier :**

- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/VenteRepository.java`

**Ajout :**

- `evolutionVentes(...)`

**Rôle :**

- calculer l’évolution des ventes selon une granularité dynamique :
  - `jour`
  - `semaine`
  - `mois`

La requête agrège :

- le montant total
- le nombre de ventes
- la quantité vendue

---

### 3.3. Service créé

**Fichier :**

- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/StatistiqueVenteService.java`

**Méthode principale :**

- `obtenirStatistiques(LocalDate dateDebut, LocalDate dateFin, Long idCategorie, String triProduits, String granularite)`

**Responsabilités :**

1. convertir les dates en `LocalDateTime`
2. récupérer le Top produits
3. récupérer le Top catégories
4. calculer les pourcentages sur le total réel de la période
5. construire la série d’évolution selon la granularité choisie
6. retourner un `StatistiqueVenteReponseDTO`

**Points importants :**

- la limitation à `Top 10`
- l’exclusion des statuts non comptabilisables
- l’utilisation de `RoundingMode.HALF_UP` pour les pourcentages
- la conversion de la granularité vers `day`, `week`, `month` pour la requête native

---

### 3.4. Controller créé

**Fichier :**

- `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/StatistiqueVenteController.java`

**Routes exposées :**

- `GET /ventes/statistiques`
- `GET /ventes/statistiques/data`

**Fonctionnement :**

- la route `GET /ventes/statistiques` charge la page Thymeleaf
- la route `GET /ventes/statistiques/data` expose les données JSON de statistique

**Paramètres pris en charge :**

- `dateDebut`
- `dateFin`
- `idCategorie`
- `triProduits`
- `granularite`

---

### 3.5. Template créé

**Fichier :**

- `src/main/resources/templates/ventes/statistiques-ventes.html`

**Contenu principal :**

- filtres de période
- sélecteur de catégorie
- tri du Top produits
- granularité de l’évolution
- tableau Top produits
- tableau Top catégories
- graphique en barres pour les tops
- graphique en courbe pour l’évolution

**Librairie utilisée :**

- `Chart.js`

---

### 3.6. Sidebar modifiée

**Fichier :**

- `src/main/resources/templates/layout/responsableCommercial.html`

**Modification apportée :**

- ajout du lien **Produits & Ventes** dans la section statistiques

**But :**

- permettre l’accès direct à la page de statistiques depuis le dashboard commercial

---

## 4. Règles métier implémentées

### 4.1. Statuts comptabilisés

Seules les ventes réellement réalisées sont prises en compte.

**Sont exclues :**

- `Annulée`
- `En attente de paiement`

### 4.2. Top produits

- classement par quantité vendue ou montant vendu
- limitation à 10 résultats
- affichage du nombre de ventes distinctes

### 4.3. Top catégories

- même logique que les produits, mais regroupée par catégorie

### 4.4. Évolution des ventes

- agrégation par jour, semaine ou mois
- affichage sous forme de courbe
- calcul uniquement sur les ventes valides

### 4.5. Calcul des pourcentages

Les pourcentages sont calculés à partir du total réel de la période filtrée, et non uniquement sur le Top 10 affiché.

---

## 5. Données de test et nettoyage

### 5.1. Script de nettoyage

**Fichier :**

- `V_31_reset_statistiques_ventes.sql`

**Rôle :**

- supprimer les données de vente de test déjà insérées
- retirer les lignes, factures et mouvements de stock associés
- réinitialiser les lots de produits de test

### 5.2. Script de réinjection

**Fichier :**

- `V_32_donnee_test_statistiques.sql`

**Rôle :**

- créer un jeu de ventes réparties sur plusieurs jours
- couvrir plusieurs produits et catégories
- ajouter une vente annulée et une vente en attente pour valider l’exclusion

### 5.3. Résultat attendu du jeu de test

- 8 ventes validées comptabilisées
- 2 ventes exclues
- 3 produits exploités
- 3 catégories exploitées
- graphique exploitable en jour, semaine et mois

---

## 6. Résultat métier attendu

Avec les scripts de test réinitialisés, l’interface doit permettre de vérifier que :

- le dashboard commercial ouvre bien la page des statistiques
- les tops produits et catégories sont corrects
- les graphiques sont lisibles et cohérents
- les filtres modifient bien les résultats
- les ventes non valides ne polluent plus les statistiques

---

## 7. Conformité et points de vigilance

### Points respectés

- séparation claire entre service, repository, controller et template
- pagination du Top 10
- filtres dynamiques
- compatibilité avec le dashboard commercial
- testabilité via un script de nettoyage dédié

### Point de vigilance

Les anciens jeux de données de vente peuvent fausser les résultats si le nettoyage n’est pas exécuté avant l’injection de `V_32_donnee_test_statistiques.sql`.

---

## 8. Documentation associée

**Cahier de test :**

- `docs/cahiers-de-test/F13-Mpiaro-Statistiques-Ventes-Test.md`

**Scripts SQL :**

- `V_31_reset_statistiques_ventes.sql`
- `V_32_donnee_test_statistiques.sql`

---

## 9. Conclusion

Le module **Statistiques Produits & Ventes** est intégré au dashboard commercial et répond aux besoins de pilotage du responsable commercial.

Il fournit :

- les tops produits
- les tops catégories
- l’évolution temporelle des ventes
- les filtres nécessaires pour les contrôles métiers

L’ajout d’un script de nettoyage ciblé et d’un jeu de données de test cohérent permet de valider l’interface sans biais provoqué par d’anciennes ventes de test.
