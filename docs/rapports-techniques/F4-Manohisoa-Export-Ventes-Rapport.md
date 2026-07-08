# Rapport technique - F4 Export des ventes, produits vendus et facture

Module : Export des ventes pour le responsable commercial
Auteur : Manohisoa
Derniere mise a jour : 2026-07-08

---

## 1. Objectif du module

Cette evolution ajoute les exports de donnees commerciales depuis l'espace responsable commercial. Elle permet :

- d'exporter la liste courante des ventes en Excel et en PDF ;
- d'exporter les produits les plus vendus en Excel et en PDF ;
- d'exporter une facture de vente au format PDF depuis la page detail ;
- de conserver les filtres visibles dans les pages lors du telechargement.

Les exports sont produits cote backend et renvoyes directement dans la reponse HTTP avec un en-tete `Content-Disposition` en piece jointe.

---

## 2. Structure des fichiers du module

```text
src/main/java/mg/vinaAkoho/vina_akoho/
|
├── controller/ventes/
│   └── VenteController.java
|
├── dto/ventes/
│   ├── ProduitVenduExportDTO.java
│   └── VenteListeExportDTO.java
|
└── service/ventes/
    └── ExportVenteService.java

src/main/resources/
|
└── templates/ventes/
    ├── responsable-commercial-ventes.html
    ├── responsable-commercial-ventes-historique.html
    └── responsable-commercial-ventes-detail.html

docs/
├── cahiers-de-test/F4-Manohisoa-Export-Ventes-Test.md
└── rapports-techniques/F4-Manohisoa-Export-Ventes-Rapport.md
```

Dependances ajoutees dans `pom.xml` :

| Dependence                    | Role                          |
| ----------------------------- | ----------------------------- |
| `org.apache.poi:poi`          | Generation des fichiers Excel |
| `org.apache.poi:poi-ooxml`    | Support du format `.xlsx`     |
| `com.github.librepdf:openpdf` | Generation des fichiers PDF   |

---

## 3. Routes d'export

| Methode | URL                               | Format  | Role                                                         |
| ------- | --------------------------------- | ------- | ------------------------------------------------------------ |
| GET     | `/ventes/liste/export/excel`      | `.xlsx` | Exporte la liste des ventes affichee dans la page principale |
| GET     | `/ventes/liste/export/pdf`        | `.pdf`  | Exporte la liste des ventes affichee dans la page principale |
| GET     | `/ventes/historique/export/excel` | `.xlsx` | Exporte le tableau des produits les plus vendus              |
| GET     | `/ventes/historique/export/pdf`   | `.pdf`  | Exporte le tableau des produits les plus vendus              |
| GET     | `/ventes/export/excel`            | `.xlsx` | Exporte les ventes realisees selon periode et zone           |
| GET     | `/ventes/export/pdf`              | `.pdf`  | Exporte les ventes realisees selon periode et zone           |
| GET     | `/ventes/{id}/facture/pdf`        | `.pdf`  | Exporte la facture d'une vente precise                       |

Les routes retournent des fichiers binaires. Elles ne retournent donc pas le format JSON `ApiResponse`, car elles servent au telechargement direct depuis Thymeleaf.

---

## 4. Filtres pris en charge

### Liste des ventes

Les routes `/ventes/liste/export/excel` et `/ventes/liste/export/pdf` reutilisent les parametres de la page principale :

- `recherche` : client, produit ou numero de facture ;
- `modePaiement` ;
- `statut` ;
- `avecLivraison` ;
- `dateDebut` ;
- `dateFin` ;
- `triPar` ;
- `ordreTri`.

Le DTO `VenteListeExportDTO` contient uniquement les donnees utiles au fichier exporte :

| Champ          | Role                                   |
| -------------- | -------------------------------------- |
| `id`           | Identifiant de la vente                |
| `client`       | Nom et prenom du client                |
| `date`         | Date et heure de vente                 |
| `produits`     | Liste lisible des produits de la vente |
| `modePaiement` | Mode de paiement                       |
| `total`        | Montant total                          |
| `statut`       | Statut de vente                        |

### Historique des produits vendus

Les routes `/ventes/historique/export/excel` et `/ventes/historique/export/pdf` prennent en charge :

- `periode` : `Ce mois`, `Ce trimestre`, `6 derniers mois` ;
- `zone` : zone de livraison client.

Seules les ventes realisees sont prises en compte. Une vente est consideree realisee si son statut n'est ni `En attente de paiement`, ni `Annulee`.

Le DTO `ProduitVenduExportDTO` contient :

| Champ             | Role                                    |
| ----------------- | --------------------------------------- |
| `produit`         | Nom du produit                          |
| `quantite`        | Quantite vendue                         |
| `chiffreAffaires` | Chiffre d'affaires du produit           |
| `partDuCA`        | Pourcentage du chiffre d'affaires total |

---

## 5. Service d'export

Le service `ExportVenteService` centralise la generation des fichiers.

### Exports Excel

Les fichiers Excel sont crees avec `XSSFWorkbook`. Chaque export :

- cree une feuille dediee ;
- ajoute une ligne d'en-tete en gras ;
- remplit les lignes avec les DTO prepares par le controleur ;
- ajuste automatiquement la largeur des colonnes ;
- retourne le contenu sous forme de `byte[]`.

Methodes principales :

```java
byte[] exporterVentesExcel(List<VenteDTO> ventes)
byte[] exporterProduitsExcel(List<ProduitVenduExportDTO> produits)
byte[] exporterVentesListeExcel(List<VenteListeExportDTO> ventes)
```

### Exports PDF

Les fichiers PDF sont crees avec `OpenPDF`. Chaque document contient un titre et un tableau lisible avec les colonnes adaptees au type d'export.

Methodes principales :

```java
byte[] exporterVentesPdf(List<VenteDTO> ventes)
byte[] exporterProduitsPdf(List<ProduitVenduExportDTO> produits)
byte[] exporterVentesListePdf(List<VenteListeExportDTO> ventes)
byte[] exporterFactureVentePdf(VenteDTO vente)
```

---

## 6. Export de facture PDF

La route `/ventes/{id}/facture/pdf` recupere la vente avec `venteService.trouverParId(id)`, puis genere une facture PDF.

Le document contient :

- le titre `VINA AKOHO - Facture` ;
- les informations de vente : identifiant, client, date, mode de paiement et statut ;
- les informations de facture : numero, date d'emission et total TTC ;
- le tableau des articles vendus : produit, quantite, prix unitaire et total ;
- le total final de la facture.

Le lien de telechargement est ajoute dans `responsable-commercial-ventes-detail.html` avec l'URL `/ventes/{id}/facture/pdf`.

---

## 7. Integration dans les vues

### Page `responsable-commercial-ventes.html`

Deux boutons ont ete ajoutes :

- `Exporter Excel` ;
- `Export PDF`.

Les fonctions JavaScript `exporterExcel()` et `exporterPdf()` redirigent vers les routes `/ventes/liste/export/excel` et `/ventes/liste/export/pdf` en conservant les filtres du formulaire.

### Page `responsable-commercial-ventes-historique.html`

Deux boutons ont ete ajoutes :

- `Export Excel` ;
- `Export PDF`.

Les fonctions JavaScript redirigent vers `/ventes/historique/export/excel` et `/ventes/historique/export/pdf` avec les filtres `periode` et `zone`.

### Page `responsable-commercial-ventes-detail.html`

Le bouton de facture pointe vers `/ventes/{id}/facture/pdf` pour telecharger directement le PDF de la facture.

---

## 8. Points de conformite backend

- Les classes respectent les dossiers par module `ventes`.
- Les DTOs evitent d'exposer directement les entites dans les exports.
- Les URLs utilisent le kebab-case et restent sous le prefixe `/ventes`.
- La logique de generation des fichiers est isolee dans un service.
- Les erreurs de vente inexistante restent gerees par le service de vente existant.
- Les fichiers binaires utilisent les types MIME adaptes :
  - `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` pour Excel ;
  - `application/pdf` pour PDF.

---

## 9. Limites connues

- Les exports sont generes en memoire avec `ByteArrayOutputStream`. Cette approche convient aux listes courantes, mais devra etre revue si le volume de ventes devient tres important.
- Les exports ne sont pas des endpoints JSON, donc ils ne suivent pas le format `ApiResponse`.
- Les tests automatises unitaires ne sont pas encore ajoutes dans ces commits ; la validation documentee ici correspond aux tests fonctionnels manuels.
