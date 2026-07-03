# Rapport Technique — F8-Maude-Rapport.md
## Module : Livraison (`livraison`)

---

## Structure des fichiers

```
controller/livraison/LivraisonController.java
controller/livraison/LivraisonViewController.java
service/livraison/LivraisonService.java
repository/livraison/LivraisonRepository.java
repository/livraison/LivreurRepository.java
repository/livraison/StatutLivraisonRepository.java
repository/livraison/HistoriqueChangementRepository.java
entity/livraison/livraison.java
entity/livraison/livreur.java
entity/livraison/statutLivraison.java
entity/livraison/historique_statut_livraison.java
dto/livraison/LivraisonDTO.java
dto/livraison/LivraisonFormDTO.java
dto/livraison/HistoriqueChangementDTO.java
exception/livraison/LivraisonNotFoundException.java
exception/livraison/LivreurNotFoundException.java
exception/livraison/VenteNotFoundException.java
```

---

## Fonctions principales

### `creer(LivraisonFormDTO form, Integer idUtilisateur) → LivraisonDTO`
Crée une livraison à partir d'une vente existante. Le service vérifie que la vente existe, charge le livreur et le statut, puis enregistre la livraison. Une première ligne d'historique de statut est aussi créée.

### `modifierStatut(Long idLivraison, String nouveauStatutLibelle, Integer idUtilisateur) → LivraisonDTO`
Met à jour le statut d'une livraison existante et enregistre automatiquement le changement dans `historique_statut_livraison`.

### `listerToutes() → List<LivraisonDTO>`
Retourne toutes les livraisons avec les informations nécessaires à l'affichage de la liste.

### `trouverParId(Long id) → LivraisonDTO`
Retourne le détail d'une livraison.

### `listerHistorique() → List<HistoriqueChangementDTO>`
Retourne l'ensemble des changements de statut triés du plus récent au plus ancien.

### `listerHistoriquePourLivraison(Long idLivraison) → List<HistoriqueChangementDTO>`
Retourne l'historique d'une livraison précise.

---

## Logique métier

Le module livraison repose sur les règles suivantes :

1. Une livraison est liée à une vente, pas à une commande.
2. Une livraison ne peut pas être créée si la vente n'existe pas.
3. Le statut initial est enregistré au moment de la création.
4. Tout changement de statut doit être tracé dans l'historique.
5. Les écrans Thymeleaf affichent la liste, le détail, la création et l'historique.

---

## Correction de base de données

Le schéma initial contenait encore `id_commande` dans la table `livraison`, alors que le code métier travaille avec `id_vente`.

Une migration dédiée a été ajoutée pour corriger cela :

- [V13__livraison_id_vente.sql](/home/maude/S4/Projet_Mme_Baovola/VINA_AKOHO/src/main/resources/db/migration/V13__livraison_id_vente.sql)

Elle renomme la colonne `id_commande` en `id_vente` et aligne l'index associé.

---

## Templates liés au module

- [livraison-nouvelle.html](/home/maude/S4/Projet_Mme_Baovola/VINA_AKOHO/src/main/resources/templates/livraison/livraison-nouvelle.html)
- [livraisons.html](/home/maude/S4/Projet_Mme_Baovola/VINA_AKOHO/src/main/resources/templates/livraison/livraisons.html)
- [livraison-detail.html](/home/maude/S4/Projet_Mme_Baovola/VINA_AKOHO/src/main/resources/templates/livraison/livraison-detail.html)
- [historique.html](/home/maude/S4/Projet_Mme_Baovola/VINA_AKOHO/src/main/resources/templates/livraison/historique.html)

---

## Remarques techniques

- Les vues utilisent des DTOs pour ne pas exposer directement les entités.
- La fiche détail embarque le changement de statut et l'historique.
- Le module est compatible avec la structure métier actuelle basée sur `Vente -> Client`.
- La compilation a été validée après correction des incohérences de mapping.