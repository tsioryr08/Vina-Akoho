# Taches d'Armando dans les sprints Vina Akoho

## Objectif du document

Ce document resume toutes les taches attribuees a **Armando** dans les fichiers de sprint du projet, puis explique pour chaque tache :

- la fonctionnalite attendue ;
- les classes et fichiers de code utilises ;
- la logique metier implementee ;
- les tables de base de donnees concernees.

## Vue d'ensemble des sprints

D'apres les fichiers `Sprint1.md`, `Sprint2.1.md`, `Sprint2.2.md` et `Sprint3.md`, Armando a eu les taches suivantes :

1. **Sprint 1**
   Module **F5 Clients**
   Taches **T5.1, T5.2, T5.3, T5.4**

2. **Sprint 2.1**
   **Aucune tache directe assignee a Armando**

3. **Sprint 2.2**
   Module **F7 Finances - Recettes**
   Taches :
   - calcul automatique des recettes ;
   - affichage des recettes par periode.

4. **Sprint 3**
   **Aucune tache directe assignee a Armando**

---

## 1. Sprint 1 - F5 Clients

### Taches donnees a Armando

Dans `Sprint1.md`, Armando est responsable du module **F5 Clients** avec les sous-taches :

- **T5.1** Inscription et creation d'un client
- **T5.2** Consultation, recherche et filtrage des clients
- **T5.3** Detail et modification d'un client
- **T5.4** Suppression logique d'un client

Le cahier de test `docs/cahiers-de-test/F5_Armando.md` confirme bien ces 4 sous-fonctionnalites.

### Fonctionnalites realisees

Le module client permet de :

- creer un client depuis un formulaire HTML ou une API JSON ;
- verifier qu'un numero de telephone n'est pas deja utilise ;
- afficher la liste des clients non supprimes ;
- filtrer les clients par nom, type, zone, service et taille de cheptel ;
- consulter la fiche detail d'un client ;
- modifier les informations d'un client ;
- supprimer un client sans effacer physiquement sa ligne en base.

### Fichiers de code principaux

#### Backend

- `src/main/java/mg/vinaAkoho/vina_akoho/service/clients/ClientService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/clients/ClientController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/clients/ClientViewController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/ClientRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/ServiceClientRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/TypeClientRepository.java`

#### DTO utilises

- `src/main/java/mg/vinaAkoho/vina_akoho/dto/clients/ClientRequestDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/clients/ClientGestionDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/clients/ClientResumeDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/clients/ClientConnexionDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/clients/ClientInscriptionDTO.java`

#### Entites

- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/Client.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/ServiceClient.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/TypeClient.java`

#### Templates HTML

- `src/main/resources/templates/clients/responsable-commercial-clients-nouveau.html`
- `src/main/resources/templates/clients/responsable-commercial-clients-liste.html`
- `src/main/resources/templates/clients/clients-detail.html`
- `src/main/resources/templates/clients/clients-detail-edit.html`

### Explication du code

#### a. `ClientService`

`ClientService` contient la logique metier principale du module client.

Les methodes les plus importantes sont :

- `createClient(ClientRequestDTO dto)`
  Cree un nouveau client apres verification du numero de telephone, chargement du service et du type client, puis sauvegarde en base.

- `getAllClients()`
  Retourne uniquement les clients dont `estSupprimer = false`.

- `getClientById(Integer id)`
  Charge un client actif et non supprime.

- `updateClient(Integer id, ClientRequestDTO dto)`
  Met a jour les informations du client en conservant les controles metier.

- `deleteClient(Integer id)`
  Fait une **suppression logique** en mettant `estSupprimer = true`.

- `verifierClientDejaInscrit(String numeroTelephone, Integer idClientAutorise)`
  Empeche les doublons de numero de telephone.

#### b. `ClientController`

`ClientController` expose l'API REST JSON sur `/api/clients` :

- `POST /api/clients`
- `GET /api/clients`
- `GET /api/clients/{id}`
- `PUT /api/clients/{id}`
- `DELETE /api/clients/{id}`

Ce controller sert surtout pour les traitements backend et les echanges JSON.

#### c. `ClientViewController`

`ClientViewController` gere l'affichage des pages HTML :

- formulaire de creation ;
- liste des clients ;
- fiche detail ;
- formulaire de modification ;
- recherche rapide par numero.

Il relie le frontend Thymeleaf avec `ClientService`.

#### d. `ClientRequestDTO`

Ce DTO protege les donnees entrantes avec les validations Jakarta :

- `@NotBlank` pour le nom, prenom et telephone ;
- `@NotNull` et `@Positive` pour le service et le type client ;
- `@PositiveOrZero` pour la taille du cheptel.

Donc une partie des regles metier est deja appliquee avant meme d'entrer dans le service.

### Tables utilisees

#### Table principale : `client`

Cette table stocke les informations du client :

- `id`
- `nom`
- `prenom`
- `date_inscription`
- `is_actif`
- `numero_telephone`
- `adresse`
- `id_localite`
- `id_zone_livraison`
- `notes`
- `id_service`
- `id_typeClient`
- `taille_cheptel`
- `est_supprimer`

Elle est definie dans `V1__schema_initial.sql`, puis completee par `V4__clients_soft_delete.sql` pour la suppression logique.

#### Tables de reference

- `service`
  Contient les services associes aux clients.

- `type_client`
  Contient le profil du client, par exemple eleveur, particulier, partenaire, etc.

### Relations entre les tables

- `client.id_service -> service.id`
- `client.id_typeClient -> type_client.id`

### Logique metier importante

1. Un client ne doit pas etre duplique par numero de telephone.
2. Un client supprime n'est pas efface de la base, il est seulement masque.
3. Les listes de clients n'affichent que les clients non supprimes.
4. Le formulaire et l'API reutilisent la meme logique metier dans `ClientService`.

### Ce qu'Armando a concretement apporte sur ce module

- un CRUD client exploitable ;
- la validation des donnees d'entree ;
- la protection contre les doublons de telephone ;
- la suppression logique ;
- les ecrans principaux de gestion client ;
- les tests documentes dans `docs/cahiers-de-test/F5_Armando.md`.

---

## 2. Sprint 2.1 - Pas de tache directe pour Armando

Dans `Sprint2.1.md`, les responsabilites principales etaient :

- Rary : entree MP
- Tsiory : sortie MP
- Mpiaro : entree produit
- Ny Antema : sortie produit + vente + facturation

Armando n'apparait pas comme responsable direct d'une fonctionnalite dans ce sprint.

---

## 3. Sprint 2.2 - F7 Finances - Recettes

### Tache donnee a Armando

Dans `Sprint2.2.md`, Armando est responsable de la partie **Recettes** avec :

- calcul automatique des recettes ;
- affichage des recettes par periode.

### Fonctionnalite attendue

L'idee metier du sprint est simple :

- chaque vente validee genere une recette ;
- la recette depend du prix de vente du produit ;
- les ventes annulees ne doivent pas etre comptabilisees.

### Fichiers de code principaux

#### Backend

- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/RecetteVenteService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/VenteController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteRepository.java`

#### DTO / Projection

- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/RecetteVenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/RecetteVenteProjection.java`

#### Template HTML

- `src/main/resources/templates/ventes/responsable-commercial-recettes.html`

### Explication du code

#### a. `RecetteVenteService`

Cette classe centralise le calcul des recettes.

Methodes principales :

- `listerParPeriode(LocalDate debut, LocalDate fin)`
  Charge les recettes entre deux dates.
  Si aucune date n'est fournie :
  - debut = premier jour du mois courant ;
  - fin = date du jour.

- `calculerTotal(List<RecetteVenteDTO> recettes)`
  Additionne tous les montants de recette.

- `calculerQuantiteTotale(List<RecetteVenteDTO> recettes)`
  Additionne toutes les quantites vendues.

Cette classe ne calcule pas directement en Java ligne par ligne a partir de toutes les ventes. Elle s'appuie sur une requete SQL du repository, puis transforme le resultat en DTO propre a l'affichage.

#### b. `LigneVenteRepository`

La methode importante est :

- `calculerRecettesParPeriode(LocalDateTime debut, LocalDateTime fin)`

Cette requete :

- joint `ligne_vente`, `vente`, `produit` et `statut_vente` ;
- groupe les resultats par **date de vente** et **produit** ;
- calcule :
  - la quantite vendue ;
  - le prix de vente ;
  - le montant de recette ;
- exclut les ventes annulees et les ventes en attente de paiement.

Le calcul principal est :

`SUM(lv.quantite * p.prix_vente)`

Autrement dit, la recette affichee est basee sur la quantite vendue multipliee par le prix de vente du produit.

#### c. `VenteController`

La route importante est :

- `GET /api/ventes/recettes`

Cette route :

1. recupere les dates de filtre ;
2. appelle `recetteVenteService.listerParPeriode(...)` ;
3. calcule les totaux ;
4. envoie les donnees au template Thymeleaf.

#### d. `responsable-commercial-recettes.html`

Cette page affiche :

- un filtre par date debut / date fin ;
- la recette totale ;
- la quantite totale vendue ;
- le nombre de lignes calculees ;
- un tableau des recettes par jour et par produit.

### Tables utilisees

Le sprint mentionne `vente` et `produit`, mais dans le code reel la fonctionnalite utilise en pratique **quatre tables principales** :

#### 1. `vente`

Table des ventes :

- `id`
- `date_vente`
- `montant_total`
- `id_mode_paiement`
- `id_statut_vente`

Aujourd'hui, l'entite Java `Vente` reference directement un `Client`, meme si le schema initial passait d'abord par `commande`.

#### 2. `ligne_vente`

Table detail des produits vendus dans une vente :

- `id_vente`
- `id_produit`
- `quantite`
- `prix_unitaire`
- `montant`

Cette table est indispensable, car une vente peut contenir plusieurs produits.

#### 3. `produit`

Table du catalogue produit :

- `id`
- `nom`
- `prix_vente`
- `id_categorie`

Le prix de vente est reutilise pour calculer les recettes.

#### 4. `statut_vente`

Table de reference des statuts de vente.

Elle sert a exclure les ventes qui ne doivent pas entrer dans le calcul, par exemple :

- `annulee`
- `en attente de paiement`

### Relations entre les tables

- `ligne_vente.id_vente -> vente.id`
- `ligne_vente.id_produit -> produit.id`
- `vente.id_statut_vente -> statut_vente.id`

### Logique metier importante

1. Les recettes sont calculees par periode.
2. Les ventes annulees ne sont pas comptabilisees.
3. Les ventes en attente de paiement sont aussi exclues dans le code actuel.
4. Le calcul est regroupe par produit et par jour.
5. La vue affiche a la fois le detail et les totaux.

### Ce qu'Armando a concretement apporte sur ce module

- un ecran de consultation des recettes ;
- un filtrage par periode ;
- une aggregation automatique des recettes ;
- un total general des montants et des quantites ;
- une base exploitable pour les dashboards financiers et commerciaux.

---

## 4. Resume global des taches d'Armando

Au total, d'apres les sprints, Armando a surtout travaille sur **deux blocs metier** :

### Bloc 1 - Gestion des clients

- creation client ;
- liste client ;
- recherche et filtrage ;
- detail ;
- modification ;
- suppression logique.

### Bloc 2 - Recettes commerciales

- calcul automatique a partir des ventes ;
- consultation des recettes par periode ;
- affichage des totaux.

---

## 5. Tables principalement utilisees par Armando

### Pour F5 Clients

- `client`
- `service`
- `type_client`

### Pour F7 Recettes

- `vente`
- `ligne_vente`
- `produit`
- `statut_vente`

---

## 6. Conclusion

En regardant les sprints et le code reel, Armando a eu un role important sur :

- la **gestion commerciale des clients** ;
- la **lecture financiere des ventes via les recettes**.

Son travail touche a la fois :

- l'interface utilisateur Thymeleaf ;
- les controllers Spring MVC et REST ;
- la logique metier dans les services ;
- l'acces aux donnees avec JPA ;
- et la structure relationnelle de la base PostgreSQL.

Si on veut presenter les taches d'Armando dans un rapport ou a l'oral, on peut resumer son apport ainsi :

**Armando a construit le module client du projet, puis la partie consultation des recettes issues des ventes.**
