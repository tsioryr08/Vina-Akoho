# Taches d'Armando dans les sprints

Ce document resume les taches attribuees a Armando dans les fichiers de sprint du projet, puis explique pour chaque tache :

- la fonctionnalite attendue ;
- le code utilise dans le projet ;
- les tables de base de donnees utilisees ;
- la logique metier principale.

Il est base sur :

- `Sprint1.md`
- `Sprint2.2.md`
- le code Java/HTML present dans le projet
- le cahier de test `docs/cahiers-de-test/F5_Armando.md`

Important :

- Dans les sprints consultes, Armando apparait explicitement sur `F5 Clients` au Sprint 1.
- Armando apparait aussi explicitement sur `F7 Recettes` au Sprint 2.2.
- Aucun bloc de `Sprint3.md` n'attribue explicitement une nouvelle tache a Armando.

---

## Vue d'ensemble

### Sprint 1

- Module : `F5 Clients`
- Responsable : `Armando`
- Taches : `T5.1, T5.2, T5.3, T5.4`

### Sprint 2.2

- Module : `F7 Recettes`
- Responsable : `Armando`
- Taches :
  - calcul automatique des recettes
  - affichage des recettes par periode

### Sprint 3

- Pas de nouvelle attribution explicite a Armando dans `Sprint3.md`

---

## 1. Sprint 1 - F5 Clients

### Ce que demandait le sprint

Dans `Sprint1.md`, Armando est responsable du module `F5 Clients`.

Le but de ce module est de gerer les clients du responsable commercial :

- creer un client ;
- consulter les clients ;
- rechercher et filtrer les clients ;
- modifier un client ;
- supprimer un client de facon logique ;
- reutiliser ces clients dans les ventes.

### Fonctionnalites couvertes

Les fonctionnalites visibles dans le code et confirmees par le cahier de test sont :

- inscription / creation d'un client ;
- validation des champs obligatoires ;
- prevention des doublons par numero de telephone ;
- affichage de la liste des clients ;
- recherche rapide par numero de telephone ;
- fiche detail d'un client ;
- modification des informations ;
- suppression logique avec conservation des donnees ;
- affichage de l'historique d'achats sur la fiche client.

### Fichiers de code principaux

#### Backend

- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/Client.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/ServiceClient.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/entity/clients/TypeClient.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/service/clients/ClientService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/clients/ClientController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/clients/ClientViewController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/ClientRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/ServiceClientRepository.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/clients/TypeClientRepository.java`

#### DTO utilises

- `dto/clients/ClientRequestDTO.java`
- `dto/clients/ClientGestionDTO.java`
- `dto/clients/ClientConnexionDTO.java`
- `dto/clients/ClientResumeDTO.java`
- `dto/clients/ClientHistoriqueAchatsDTO.java`
- `dto/clients/ClientInscriptionDTO.java`

#### Pages HTML

- `templates/clients/responsable-commercial-clients-nouveau.html`
- `templates/clients/responsable-commercial-clients-liste.html`
- `templates/clients/clients-detail.html`
- `templates/clients/clients-detail-edit.html`

### Tables utilisees

#### Tables directes du module client

- `client`
- `service`
- `type_client`

#### Tables utilisees indirectement via l'historique d'achats

- `vente`
- `ligne_vente`
- `facture`

### Explication des tables

#### `client`

Table principale du module.

Elle contient :

- l'identite du client : `nom`, `prenom`
- la date d'inscription
- le telephone
- l'adresse
- la localite
- la zone de livraison
- des notes
- le type de client
- le service rattache
- la taille du cheptel
- l'etat `actif`
- l'etat `est_supprimer`

Cette table est representee par l'entite `Client`.

#### `service`

Permet de rattacher le client a un service metier.

Dans le code, un client possede un champ `service` via une relation `@ManyToOne`.

#### `type_client`

Permet de classifier le client, par exemple selon son profil.

Dans le code, un client possede aussi une relation `@ManyToOne` vers `TypeClient`.

#### `vente`, `ligne_vente`, `facture`

Ces tables ne servent pas a creer le client, mais elles sont utilisees pour afficher son historique commercial.

Quand on ouvre la fiche detail d'un client, le projet recupere :

- ses ventes ;
- le total de ses achats ;
- le total regle ;
- le solde restant.

### Logique metier du code

#### a. Creation d'un client

Le coeur de cette logique est dans `ClientService.createClient(...)`.

Ce que fait la methode :

1. verifie qu'un autre client non supprime n'a pas deja le meme numero ;
2. charge le service du client ;
3. charge le type du client ;
4. construit l'entite `Client` ;
5. enregistre le client.

Ce choix evite les doublons et garantit que le client est toujours relie a des references valides.

#### b. Connexion / recherche d'un client

`ClientService.connecter(...)` permet de retrouver un client :

- soit par `id + numeroTelephone`
- soit seulement par `numeroTelephone`

`ClientService.rechercherParTelephone(...)` permet aussi de verifier rapidement si un client existe deja.

#### c. Consultation

`ClientService.getAllClients()` ne retourne que les clients non supprimes.

`ClientService.getClientById(...)` recupere un client actif/non supprime et le transforme en DTO d'affichage.

#### d. Modification

`ClientService.updateClient(...)` :

1. verifie que le client existe encore ;
2. verifie que le numero n'entre pas en conflit avec un autre client ;
3. met a jour les champs modifiables ;
4. met a jour `updatedAt`.

#### e. Suppression logique

`ClientService.deleteClient(...)` ne supprime pas physiquement la ligne SQL.

A la place, le code met :

- `estSupprimer = true`

Cette approche est importante parce qu'un client peut deja etre relie a des ventes anciennes. On garde donc l'historique.

#### f. Fiche detail et historique d'achats

Dans `ClientViewController.detail(...)`, la page detail appelle :

- `clientService.getClientById(id)`
- `venteService.obtenirHistoriqueClient(id)`

Cela permet d'afficher sur une seule page :

- les informations du client ;
- ses achats ;
- son total de ventes ;
- ce qu'il a regle ;
- son solde.

### Ce qu'Armando a livre sur ce module

Le module `F5 Clients` couvre bien les taches du sprint :

- `T5.1` inscription / creation ;
- `T5.2` consultation / recherche / filtrage ;
- `T5.3` detail / modification ;
- `T5.4` suppression logique / protection des donnees.

Le cahier de test `docs/cahiers-de-test/F5_Armando.md` confirme ce perimetre.

---

## 2. Sprint 2.2 - F7 Recettes

### Ce que demandait le sprint

Dans `Sprint2.2.md`, Armando est responsable de la partie `Recettes` du module `F7 Gestion Financiere`.

Les taches demandees sont :

- calcul automatique des recettes ;
- affichage des recettes par periode.

Les regles donnees dans le sprint sont :

- chaque vente validee genere automatiquement une recette ;
- les ventes annulees ne sont pas comptabilisees.

### Fonctionnalites presentes dans le code

Dans le projet, cette tache correspond au module d'affichage des recettes lie aux ventes :

- calcul des recettes entre deux dates ;
- total de recette sur une periode ;
- quantite totale vendue ;
- affichage detaille par date et par produit ;
- exclusion des ventes annulees ;
- filtrage par periode.

### Fichiers de code principaux

#### Backend

- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/RecetteVenteService.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/controller/ventes/VenteController.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/LigneVenteRepository.java`

#### DTO utilises

- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/RecetteVenteDTO.java`
- `src/main/java/mg/vinaAkoho/vina_akoho/dto/ventes/RecetteVenteProjection.java`

#### Vue HTML

- `src/main/resources/templates/ventes/responsable-commercial-recettes.html`

### Tables utilisees

#### Tables directement interrogees

- `ligne_vente`
- `vente`
- `produit`
- `statut_vente`

#### Tables mentionnees dans le sprint

- `vente`
- `produit`

Remarque :

Le sprint ne cite que `vente` et `produit`, mais le code utilise logiquement aussi `ligne_vente` et `statut_vente` pour faire le calcul proprement.

### Explication des tables

#### `vente`

Contient l'en-tete de la vente :

- date
- client
- montant total
- mode de paiement
- statut

La date de vente sert au filtrage par periode.

#### `ligne_vente`

Contient le detail de chaque produit vendu.

C'est la table la plus importante pour le calcul des recettes, car elle donne :

- quel produit a ete vendu ;
- en quelle quantite ;
- pour quelle vente.

#### `produit`

Permet de recuperer :

- le nom du produit ;
- son prix de vente.

Dans la requete de calcul, le montant de recette est derive du prix du produit et de la quantite vendue.

#### `statut_vente`

Permet d'exclure les ventes qui ne doivent pas entrer dans la recette.

Le code filtre explicitement les statuts comme :

- `annulee`
- `annulée`
- `en attente de paiement`

### Logique metier du code

#### a. Calcul des recettes par periode

Le service principal est `RecetteVenteService.listerParPeriode(...)`.

Il fait :

1. choix d'une periode par defaut si aucune date n'est fournie ;
2. verification que la date de fin n'est pas avant la date de debut ;
3. appel a `LigneVenteRepository.calculerRecettesParPeriode(...)` ;
4. transformation du resultat SQL en `RecetteVenteDTO`.

Le but est de retourner une vue exploitable pour l'ecran des recettes.

#### b. Requete SQL principale

La methode `calculerRecettesParPeriode(...)` dans `LigneVenteRepository` fait une aggregation SQL.

Elle groupe les donnees :

- par jour ;
- par produit.

Elle calcule :

- la date ;
- l'identifiant produit ;
- le nom du produit ;
- le prix de vente ;
- la quantite vendue ;
- le montant de recette.

La recette est calculee a partir de :

- `SUM(lv.quantite * p.prix_vente)`

Le filtre sur `statut_vente` applique la regle metier du sprint : les ventes annulees ou non valides ne doivent pas compter.

#### c. Totaux affiches

Le service contient aussi :

- `calculerTotal(...)`
- `calculerQuantiteTotale(...)`

Ces methodes additionnent les montants et les quantites retournes par la liste detaillee.

#### d. Affichage web

Dans `VenteController.recettes(...)`, le controleur :

1. lit les dates du formulaire ;
2. appelle `recetteVenteService.listerParPeriode(...)` ;
3. ajoute au modele :
   - la liste des recettes ;
   - la recette totale ;
   - la quantite totale ;
   - le nombre de lignes ;
   - la periode selectionnee ;
4. renvoie la page `ventes/responsable-commercial-recettes`.

#### e. Interface utilisateur

La page `responsable-commercial-recettes.html` affiche :

- un filtre par date debut / date fin ;
- une carte de recette totale ;
- une carte de quantite vendue ;
- une carte du nombre de lignes ;
- un tableau detaille.

Le message de la page rappelle aussi la regle metier :

- les ventes annulees sont exclues.

### Ce qu'Armando a livre sur ce module

Le code present montre que la tache Sprint 2.2 a ete traduite en fonctionnalite exploitable :

- calcul automatique des recettes a partir des ventes ;
- affichage des recettes par periode ;
- exclusion des ventes annulees ;
- presentation dans une page dediee pour le responsable commercial.

---

## 3. Ce qu'il faut retenir sur les taches d'Armando

Armando intervient sur deux sujets differents mais relies au metier commercial :

- les `clients` ;
- les `recettes des ventes`.

### Idee generale de ses contributions

#### F5 Clients

Armando gere la base de la relation commerciale :

- qui est le client ;
- comment on l'enregistre ;
- comment on le retrouve ;
- comment on garde son historique ;
- comment on evite les doublons.

#### F7 Recettes

Armando exploite ensuite les ventes pour produire une lecture financiere simple :

- combien a ete vendu ;
- sur quelle periode ;
- pour quels produits ;
- avec quel montant total.

### Lien entre les deux modules

Ces deux taches sont logiquement connectees :

- le module `Clients` permet d'identifier les acheteurs ;
- le module `Recettes` exploite ensuite les ventes creees pour ces clients.

Autrement dit :

- `Clients` prepare la relation commerciale ;
- `Recettes` mesure ce que cette relation rapporte.

---

## 4. Liste compacte des tables utilisees par Armando

### Module F5 Clients

- `client`
- `service`
- `type_client`
- `vente`
- `ligne_vente`
- `facture`

### Module F7 Recettes

- `vente`
- `ligne_vente`
- `produit`
- `statut_vente`

---

## 5. Conclusion

En regardant les sprints et le code, les taches d'Armando dans ce projet sont :

1. `Sprint 1 - F5 Clients`
2. `Sprint 2.2 - F7 Recettes`

Le premier module construit la gestion des clients pour le responsable commercial.

Le second module calcule et affiche les recettes generees par les ventes sur une periode donnee.

Ces deux contributions sont importantes parce qu'elles relient :

- la gestion des personnes qui achetent ;
- la mesure des revenus apportes par ces achats.
