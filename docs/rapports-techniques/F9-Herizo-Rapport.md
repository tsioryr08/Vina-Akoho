# RAPPORT TECHNIQUE — F9
## Module : Historique des achats / créances et solde client (`/clients`)

---

## 1. Objectif
`GET /clients/{id}` affiche :
- l’historique des ventes (lignes + facture + statut + montant)
- la synthèse : **Prix total**, **Montant réglé**, **Solde restant**


---

## 2. Structure des fichiers (couches)

### 2.1 Controller

- `src/main/java/mg/vinaAkoho/vina_akoho/controller/clients/ClientViewController.java`
  - Route `GET /clients/{id}` : charge le client + appelle le service d’historique

### 2.2 Service métier

- `src/main/java/mg/vinaAkoho/vina_akoho/service/ventes/VenteService.java`
  - `obtenirHistoriqueClient(Integer clientId)` : calcule totaux et solde

### 2.3 Repository (accès BDD)

- `src/main/java/mg/vinaAkoho/vina_akoho/repository/ventes/VenteRepository.java`
  - méthodes SQL/JPQL pour agréger achats et “règlements”

### 2.4 Template

- `src/main/resources/templates/clients/clients-detail.html`
  - Affiche cartes de synthèse + tableaux d’historique

---

## 3. Fonctions principales (signatures & rôle)

### 3.1 `ClientViewController.detail()`

```java
@GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
public String detail(@PathVariable Integer id, Model model) {
    ClientGestionDTO client = clientService.getClientById(id);
    ClientHistoriqueAchatsDTO historiqueAchats = venteService.obtenirHistoriqueClient(id);

    model.addAttribute("client", client);
    model.addAttribute("historiqueAchats", historiqueAchats);
    return "clients/clients-detail";
}
```

Rôle : orchestrer la page de détail client (client + historique/solde).

---

### 3.2 `VenteService.obtenirHistoriqueClient(Integer clientId)`

```java
@Transactional(readOnly = true)
public ClientHistoriqueAchatsDTO obtenirHistoriqueClient(Integer clientId) {
    clientRepository.findByIdAndEstSupprimerFalse(clientId)
        .orElseThrow(() -> ClientNotFoundException.parId(clientId));

    List<VenteDTO> ventes = venteRepository.findByClientIdOrderByDateVenteDesc(clientId)
        .stream()
        .map(this::versDTO)
        .collect(Collectors.toList());

    BigDecimal totalAchats = venteRepository.sommeAchatsClient(clientId);
    BigDecimal totalRegle = venteRepository.sommeReglementsClient(clientId);

    return ClientHistoriqueAchatsDTO.builder()
        .ventes(ventes)
        .totalAchats(totalAchats)
        .totalRegle(totalRegle)
        .soldeRestant(totalAchats.subtract(totalRegle))
        .build();
}
```

Rôle :
- valide l’existence du client
- charge ses ventes triées par date décroissante
- calcule :
  - **totalAchats** = somme des ventes
  - **totalRegle** = somme des ventes considérées “réglées”
  - **soldeRestant** = `totalAchats - totalRegle`

---

### 3.3 Repository — agrégats

#### a) `VenteRepository.findByClientIdOrderByDateVenteDesc(Integer clientId)`

Récupère les ventes du client, triées par date décroissante.

#### b) `VenteRepository.sommeAchatsClient(Integer clientId)`

Somme des `montantTotal` des ventes du client, en excluant les ventes annulées :
- filtre `NOT IN ('annulée', 'annulee')`

#### c) `VenteRepository.sommeReglementsClient(Integer clientId)`

Somme des `montantTotal` des ventes du client dont le statut est :
- `IN ('validée', 'validee')`

> Remarque : en l’absence de table de paiements séparée, l’implémentation considère le statut **Validée** comme représentant un paiement/règlement.

---

## 4. Logique métier expliquée

### 4.1 Règles de calcul du solde

- Une vente appartient obligatoirement à un client.
- **Montant des créances** = somme des ventes - somme des paiements.
- **Solde** recalculé automatiquement à chaque affichage (données lues depuis BDD et agrégées à la volée).

Formules utilisées :

- `totalAchats = sommeAchatsClient(clientId)`
- `totalRegle = sommeReglementsClient(clientId)`
- `soldeRestant = totalAchats - totalRegle`

---

## 5. Templates et rendu (UI)

### 5.1 `clients-detail.html`

Le template affiche :

- 3 cartes de synthèse :
  - **Total achats** (`historiqueAchats.totalAchats`)
  - **Montant réglé** (`historiqueAchats.totalRegle`)
  - **Solde restant** (`historiqueAchats.soldeRestant`)

- Tableau “Historique des achats” :
  - colonnes : Vente, Date, Articles, Facture, Statut, Montant
  - + colonne **Reste à payer** calculée côté vue avec une logique basée sur le statut (ex. `En attente de paiement`) et montant (facture TTC si disponible sinon `montantTotal`).

- Second tableau “Toutes les ventes du client” :
  - affiche aussi facture, statut, montant

---

## 6. Tests effectués (référence cahier)

Voir : `docs/cahiers-de-test/F9-Herizo-Test.md`

- Test 1 : affichage complet historique achats et ventes (2 tableaux)
- Test 2 : calcul automatique du montant restant à payer dans la synthèse et le tableau
- Test 3 : affichage du solde du client dans la zone de synthèse

---

## 7. Remarques techniques

- Les écrans utilisent des **DTO** (ex. `ClientHistoriqueAchatsDTO`, `VenteDTO`) pour éviter d’exposer directement les entités.
- `VenteService.obtenirHistoriqueClient` est annoté en lecture seule côté transaction.
- Les calculs d’agrégats sont délégués au repository (requêtes JPQL) : moins de calcul en mémoire, meilleur rendu pour la vue.
- Le “paiement” est actuellement inféré via le statut **Validée** (absence de table de paiements séparée à ce stade).

