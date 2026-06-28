# Cahier de test — F5 Clients

Testeur : Armando
Date : 2026-06-26
Module : F5 — Gestion des clients (T5.1 a T5.4)
Donnees de test : base locale Vina Akoho

---

## T5.1 — Inscription et creation d'un client

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/nouveau`
Registration : responsable commercial
Resultat attendu : Le formulaire d'enregistrement client s'affiche avec les listes "Profil du client" et "Service rattache" chargees.
Resultat obtenu : Formulaire affiche, champs d'identification, logistique, service et notes disponibles.
Statut :
☑ Succes
☐ Echec
Commentaire : —

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/nouveau`
Registration : responsable commercial
Resultat attendu : Creation d'un client valide (nom, prenom, telephone, type, service, zone, adresse, cheptel) puis affichage du message "Compte client enregistre".
Resultat obtenu : Client cree, identifiant client retourne et visible dans la liste `/clients/espace`.
Statut :
☑ Succes
☐ Echec
Commentaire : Le code client est affiche au format `CLT-XXX` dans la liste et la fiche detail.

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/nouveau`
Registration : responsable commercial
Resultat attendu : Creation avec nom vide -> message "Le nom est obligatoire".
Resultat obtenu : Formulaire conserve, message d'erreur affiche.
Statut :
☑ Succes
☐ Echec
Commentaire : Validation Jakarta appliquee via `ClientRequestDTO`.

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/nouveau`
Registration : responsable commercial
Resultat attendu : Creation avec telephone vide -> message "Le numero telephone est obligatoire".
Resultat obtenu : Formulaire conserve, message d'erreur affiche.
Statut :
☑ Succes
☐ Echec
Commentaire : Le telephone sert aussi a verifier si le client est deja inscrit.

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/nouveau`
Registration : responsable commercial
Resultat attendu : Creation avec un numero telephone deja present en base -> blocage et message "Ce client est deja inscrit avec ce numero telephone".
Resultat obtenu : Aucun doublon cree, formulaire conserve avec le message d'erreur.
Statut :
☑ Succes
☐ Echec
Commentaire : Verification faite cote service avec les clients non supprimes.

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/api/clients`
Registration : —
Resultat attendu : Creation API avec numero telephone deja inscrit -> HTTP 409 Conflict.
Resultat obtenu : Requete refusee par la regle metier anti-doublon.
Statut :
☑ Succes
☐ Echec
Commentaire : La protection est commune au formulaire web et a l'API JSON.

---

## T5.2 — Consultation, recherche et filtrage des clients

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/espace`
Registration : responsable commercial
Resultat attendu : La liste affiche uniquement les clients non supprimes avec code, nom, telephone, type, cheptel, service et action "Voir".
Resultat obtenu : Liste chargee avec les colonnes attendues.
Statut :
☑ Succes
☐ Echec
Commentaire : Les clients soft-supprimes sont exclus par `findByEstSupprimerFalse`.

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/espace`
Registration : responsable commercial
Resultat attendu : Recherche par nom, code, telephone ou zone -> seules les lignes correspondantes restent visibles.
Resultat obtenu : Filtrage instantane cote navigateur.
Statut :
☑ Succes
☐ Echec
Commentaire : Le filtre lit l'attribut `data-search` des lignes du tableau.

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/espace`
Registration : responsable commercial
Resultat attendu : Filtrage par type, zone, taille de cheptel et service -> affichage des clients correspondants.
Resultat obtenu : Filtres fonctionnels et combinables.
Statut :
☑ Succes
☐ Echec
Commentaire : Les tailles sont separees en moins de 100, 100 a 999, et 1000 tetes et plus.

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/recherche?numeroTelephone=0341111111`
Registration : —
Resultat attendu : Si le numero existe et que le client est actif, retour `success=true` avec les informations resumees.
Resultat obtenu : Client trouve quand le numero correspond a un client actif non supprime.
Statut :
☑ Succes
☐ Echec
Commentaire : Endpoint utile pour controler rapidement une inscription existante.

---

## T5.3 — Detail et modification d'un client

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/{id}`
Registration : responsable commercial
Resultat attendu : La fiche detail affiche l'identite, le statut actif/inactif, le code, le type, le cheptel, les coordonnees, la zone, l'adresse et les notes.
Resultat obtenu : Fiche detail complete affichee.
Statut :
☑ Succes
☐ Echec
Commentaire : Actions disponibles : retour, modification, suppression.

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/{id}/modifier`
Registration : responsable commercial
Resultat attendu : Le formulaire de modification est pre-rempli avec les donnees du client.
Resultat obtenu : Donnees chargees dans tous les champs modifiables.
Statut :
☑ Succes
☐ Echec
Commentaire : Le telephone est obligatoire dans le formulaire et dans le DTO.

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/{id}/modifier`
Registration : responsable commercial
Resultat attendu : Modification d'un client existant -> fiche detail mise a jour et message "Client modifie avec succes."
Resultat obtenu : Modification enregistree, retour sur la fiche detail.
Statut :
☑ Succes
☐ Echec
Commentaire : —

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/{id}/modifier`
Registration : responsable commercial
Resultat attendu : Modification du telephone avec un numero utilise par un autre client -> blocage et message "Ce client est deja inscrit avec ce numero telephone".
Resultat obtenu : Modification refusee, aucun doublon de telephone cree.
Statut :
☑ Succes
☐ Echec
Commentaire : Le client en cours peut conserver son propre numero sans erreur.

---

Date : 2026-06-26
Testeur : Armando
Page : PUT `/api/clients/{id}`
Registration : —
Resultat attendu : Modification d'un client inexistant -> HTTP 404 avec "Client introuvable".
Resultat obtenu : Requete refusee si l'identifiant ne correspond a aucun client non supprime.
Statut :
☑ Succes
☐ Echec
Commentaire : —

---

## T5.4 — Suppression logique et protection des donnees

---

Date : 2026-06-26
Testeur : Armando
Page : POST `/clients/{id}/supprimer`
Registration : responsable commercial
Resultat attendu : Suppression logique du client, redirection vers `/clients/espace`, message "Client supprime de la liste."
Resultat obtenu : Client marque supprime et retire de la liste.
Statut :
☑ Succes
☐ Echec
Commentaire : La suppression met `estSupprimer=true`.

---

Date : 2026-06-26
Testeur : Armando
Page : GET `/clients/espace` apres suppression
Registration : responsable commercial
Resultat attendu : Le client supprime ne s'affiche plus dans la liste.
Resultat obtenu : Client absent de la liste principale.
Statut :
☑ Succes
☐ Echec
Commentaire : —

---

Date : 2026-06-26
Testeur : Armando
Page : DELETE `/api/clients/{id}`
Registration : —
Resultat attendu : Suppression API -> HTTP 204 No Content.
Resultat obtenu : Suppression logique effectuee.
Statut :
☑ Succes
☐ Echec
Commentaire : Endpoint aligne avec la suppression web.

---

## Bugs identifies

Aucun bug bloquant apres ajout de la verification anti-doublon par numero telephone.

## Observations

La verification d'inscription existante utilise le numero telephone comme identifiant principal du client. Les clients supprimes logiquement ne bloquent pas une nouvelle inscription avec le meme numero.
