-- Suppression des rôles : Comptable, Responsable achat, Gestionnaire de stock
-- + leurs comptes employés associés.
--
-- Point de blocage : la FK employe.id_role est en ON DELETE RESTRICT
-- (V1__schema_initial.sql) et la colonne est NOT NULL. On ne peut donc PAS
-- supprimer un rôle tant qu'un employé y fait référence.
--
-- Ordre sûr, sans erreur de dépendance :
--   1) supprimer (ou réaffecter) les employés liés à ces rôles
--   2) supprimer les rôles
--
-- V2__data_initial.sql recrée ces comptes et rôles à chaque base neuve ;
-- comme V34 s'exécute APRÈS V2, les comptes sont d'abord insérés puis
-- retirés ici, ce qui garde le schéma cohérent sur base fraîche comme existante.

DO $$
DECLARE
    v_role_ids INTEGER[];
BEGIN
    SELECT ARRAY_AGG(id) INTO v_role_ids
    FROM role
    WHERE poste IN ('Comptable', 'Responsable achat', 'Gestionnaire de stock');

    IF v_role_ids IS NOT NULL AND array_length(v_role_ids, 1) > 0 THEN
        -- 1) Supprimer les employés rattachés (comptes de connexion supprimés)
        DELETE FROM employe WHERE id_role = ANY(v_role_ids);

        -- 2) Supprimer les rôles
        DELETE FROM role WHERE id = ANY(v_role_ids);
    END IF;
END $$;
