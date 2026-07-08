# Cahier de Test - F13 Statistiques Produits & Ventes

**Module :** F13 - Statistiques Produits & Ventes  
**Responsable :** Mpiaro  
**Contexte :** Dashboard du responsable commercial  
**Page cible :** `GET /ventes/statistiques`  
**Jeu de données :** `V_31_reset_statistiques_ventes.sql + V_32_donnee_test_statistiques.sql`

---

## 1. Parcours d'accès à tester

Le test doit être réalisé comme un utilisateur du rôle **Responsable Commercial**.

1. Se connecter via le dashboard du responsable commercial.
2. Arriver sur le **Dashboard Commercial**.
3. Cliquer dans le `layout` sur le lien **Produits & Ventes**.
4. Vérifier l'ouverture de la page de statistiques produits et ventes.

**Lien attendu dans le menu :**
- `Produits & Ventes`

**Route attendue :**
- `/ventes/statistiques`

---

## 2. Données de test attendues

Le script `V_32_donnee_test_statistiques.sql` fournit un jeu de données calibré pour tester :

- l'affichage du **Top 10 produits**
- le calcul des **quantités vendues**
- le calcul des **pourcentages des ventes**
- le calcul du **nombre de ventes**
- l'affichage du **Top catégories**
- l'évolution des ventes en **jour**, **semaine** et **mois**

### Jeu de données utile

| Élément | Valeur attendue |
|---|---:|
| Ventes validées comptabilisées | 8 |
| Ventes exclues des statistiques | 2 |
| Produits concernés | 3 |
| Catégories concernées | 3 |
| Total quantités vendues | 22 |
| Total montant ventes | 1 801 000 Ar |

### Répartition attendue par produit

| Produit | Quantité vendue | Montant total | Nombre de ventes | Part quantités | Part montant |
|---|---:|---:|---:|---:|---:|
| **Aliment Poussin 10kg** | 9 | 225 000 Ar | 2 | 40,91 % | 12,49 % |
| **Test Aliment Finition 50kg** | 7 | 826 000 Ar | 3 | 31,82 % | 45,86 % |
| **Test Aliment Croissance 50kg** | 6 | 750 000 Ar | 3 | 27,27 % | 41,64 % |

### Répartition attendue par catégorie

| Catégorie | Quantité vendue | Montant total | Nombre de ventes |
|---|---:|---:|---:|
| **Poussin** | 9 | 225 000 Ar | 2 |
| **Test Recette Finition** | 7 | 826 000 Ar | 3 |
| **Test Recette Croissance** | 6 | 750 000 Ar | 3 |

---

## 3. Cas de test

### TEST-01 - Accès à la page statistiques depuis le dashboard commercial

| Champ | Détail |
|---|---|
| **Pré-requis** | Être connecté avec un compte du rôle **Responsable Commercial** |
| **Étapes** | 1. Ouvrir le dashboard commercial. 2. Cliquer sur **Produits & Ventes** dans le layout. |
| **Résultat attendu** | La page `/ventes/statistiques` s'affiche sans erreur. Le titre **Produits & Ventes** est visible. |
| **Statut** | ☐ À vérifier |

---

### TEST-02 - Affichage initial des filtres

| Champ | Détail |
|---|---|
| **Pré-requis** | Page `/ventes/statistiques` ouverte |
| **Étapes** | Observer les filtres au chargement initial. |
| **Résultat attendu** | Les filtres sont visibles et préremplis avec :<br>- **Date début** = 30 jours avant la date courante<br>- **Date fin** = date courante<br>- **Catégorie** = toutes les catégories<br>- **Trier Top produits par** = quantité vendue<br>- **Granularité évolution** = jour |
| **Statut** | ☐ À vérifier |

---

### TEST-03 - Affichage du Top 10 produits

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Lire le tableau **Top 10 Produits les plus vendus**. |
| **Résultat attendu** | Les produits sont triés par quantité vendue décroissante et les colonnes suivantes sont visibles :<br>- Produit<br>- Catégorie<br>- Quantité vendue<br>- % Quantité<br>- Montant<br>- % Montant<br>- Nb ventes |
| **Vérification rapide** | Le premier produit doit être **Aliment Poussin 10kg** avec **9** unités vendues. |
| **Statut** | ☐ À vérifier |

---

### TEST-04 - Vérification des pourcentages produits

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Contrôler les lignes du tableau produits. |
| **Résultat attendu** | Les pourcentages affichés doivent correspondre aux parts suivantes :<br>- **9 / 22 = 40,91 %**<br>- **7 / 22 = 31,82 %**<br>- **6 / 22 = 27,27 %** |
| **Statut** | ☐ À vérifier |

---

### TEST-05 - Affichage du Top catégories

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Lire le tableau **Top Catégories les plus vendues**. |
| **Résultat attendu** | Les catégories sont triées par quantité vendue décroissante et les colonnes suivantes sont visibles :<br>- Catégorie<br>- Quantité vendue<br>- % Quantité<br>- Montant<br>- % Montant<br>- Nb ventes |
| **Vérification rapide** | La première catégorie doit être **Poussin** avec **9** unités vendues en tri quantité. |
| **Statut** | ☐ À vérifier |

---

### TEST-06 - Graphique barres du Top produits

| Champ | Détail |
|---|---|
| **Pré-requis** | Page statistique ouverte |
| **Étapes** | Observer le graphique du Top produits. |
| **Résultat attendu** | Le graphique s'affiche en **barres** et correspond au classement du tableau. |
| **Statut** | ☐ À vérifier |

---

### TEST-07 - Changement du tri des produits par montant

| Champ | Détail |
|---|---|
| **Pré-requis** | Page statistique ouverte |
| **Étapes** | Sélectionner **Montant des ventes** dans le filtre de tri des produits. |
| **Résultat attendu** | Le tableau et le graphique Top produits se recalculent. Le classement devient cohérent avec les montants, avec **Test Aliment Finition 50kg** en tête. |
| **Statut** | ☐ À vérifier |

---

### TEST-08 - Évolution des ventes par jour

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Sélectionner **Jour** dans la granularité d'évolution. |
| **Résultat attendu** | Le graphique s'affiche en **courbe** avec des points journaliers. Les ventes annulées et en attente ne doivent pas apparaître dans la série. |
| **Statut** | ☐ À vérifier |

---

### TEST-09 - Évolution des ventes par semaine

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Sélectionner **Semaine** dans la granularité d'évolution. |
| **Résultat attendu** | La courbe s'agrège par semaine et doit afficher 4 points, avec une forme non monotone :<br>- Semaine du 15/06/2026 : 225 000 Ar<br>- Semaine du 22/06/2026 : 611 000 Ar<br>- Semaine du 29/06/2026 : 361 000 Ar<br>- Semaine du 06/07/2026 : 604 000 Ar |
| **Statut** | ☐ À vérifier |

---

### TEST-10 - Évolution des ventes par mois

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Sélectionner **Mois** dans la granularité d'évolution. |
| **Résultat attendu** | La courbe affiche les montants regroupés par mois. Avec le jeu `V_32`, il doit y avoir **2 points** visibles :<br>- **06/2026 : 836 000 Ar**<br>- **07/2026 : 965 000 Ar** |
| **Statut** | ☐ À vérifier |

---

### TEST-11 - Filtre par catégorie

| Champ | Détail |
|---|---|
| **Pré-requis** | Page statistique ouverte |
| **Étapes** | Choisir une catégorie, par exemple **Test Recette Croissance**. |
| **Résultat attendu** | Les tableaux et graphiques se recalculent uniquement sur la catégorie sélectionnée. Les produits et catégories non concernés ne doivent plus apparaître. |
| **Statut** | ☐ À vérifier |

---

### TEST-12 - Exclusion des ventes annulées et en attente

| Champ | Détail |
|---|---|
| **Pré-requis** | Données de `V_32_donnee_test_statistiques.sql` chargées après nettoyage |
| **Étapes** | Comparer les totaux affichés avec les ventes insérées par le script. |
| **Résultat attendu** | Les ventes au statut **Annulée** et **En attente de paiement** ne sont pas comptabilisées dans le Top produits, le Top catégories ni l'évolution. |
| **Statut** | ☐ À vérifier |

---

## 4. Vérifications SQL utiles

```sql
-- Total des ventes comptabilisées
SELECT COUNT(*)
FROM vente v
JOIN statut_vente sv ON sv.id = v.id_statut_vente
WHERE LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement');

-- Totaux attendus sur V32
-- COUNT(*) = 8
-- SUM(montant_total) = 1801000

-- Top produits sur la période courante
SELECT p.nom, SUM(lv.quantite) AS quantite, SUM(lv.montant) AS montant
FROM ligne_vente lv
JOIN vente v ON v.id = lv.id_vente
JOIN produit p ON p.id = lv.id_produit
JOIN statut_vente sv ON sv.id = v.id_statut_vente
WHERE LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
GROUP BY p.nom
ORDER BY quantite DESC;

-- Attendu :
-- Aliment Poussin 10kg       | 9 | 225000
-- Test Aliment Finition 50kg | 7 | 826000
-- Test Aliment Croissance 50kg | 6 | 750000

-- Évolution sur les ventes valides
SELECT date_trunc('month', v.date_vente) AS mois, SUM(v.montant_total)
FROM vente v
JOIN statut_vente sv ON sv.id = v.id_statut_vente
WHERE LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
GROUP BY 1
ORDER BY 1;

-- Attendu :
-- 2026-06-01 | 836000
-- 2026-07-01 | 965000
```

---

## 5. Résumé

| Total tests | À valider |
|---|---|
| 12 | Vérifier dans l'interface commerciale |

**Conclusion :** ce cahier de test permet de contrôler la page de statistiques du dashboard commercial, depuis l'accès par le menu jusqu'aux graphiques et aux filtres. Le script `V_31_reset_statistiques_ventes.sql` doit être exécuté avant `V_32_donnee_test_statistiques.sql` pour obtenir les valeurs attendues.
