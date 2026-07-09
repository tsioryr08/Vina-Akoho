window.VinaAkohoAuth = {
    login: function (role, mdp) {
        if (!role) {
            return { ok: false, message: 'Veuillez choisir un profil.' };
        }

        if (mdp !== '123') {
            return { ok: false, message: 'Mot de passe incorrect.' };
        }

        const mapping = {
            admin: { role: 'ADMIN', nom: 'Admin', prenom: 'Sys', idEmploye: 1 },
            responsableAchat: { role: 'RESPONSABLE_ACHATS', nom: 'Achat', prenom: 'User', idEmploye: 2 },
            responsableProduction: { role: 'RESPONSABLE_PRODUCTION', nom: 'Production', prenom: 'User', idEmploye: 3 },
            gestionnaireStock: { role: 'GESTIONNAIRE_DE_STOCK', nom: 'Stock', prenom: 'User', idEmploye: 4 },
            responsableCommercial: { role: 'RESPONSABLE_COMMERCIAL', nom: 'Commercial', prenom: 'User', idEmploye: 5 },
            comptable: { role: 'COMPTABLE', nom: 'Compta', prenom: 'User', idEmploye: 6 }
        };

        const user = mapping[role];
        if (!user) {
            return { ok: false, message: 'Profil utilisateur invalide.' };
        }

        const fakeToken = 'demo.' + btoa(JSON.stringify({ sub: user.idEmploye, role: user.role, nom: user.nom, prenom: user.prenom })) + '.sig';

        localStorage.setItem('vinaAkohoToken', fakeToken);
        localStorage.setItem('vinaAkohoUser', JSON.stringify({
            idEmploye: user.idEmploye,
            nom: user.nom,
            prenom: user.prenom,
            email: role + '@demo.local',
            role: user.role
        }));

        return {
            ok: true,
            message: 'Connexion réussie',
            data: {
                token: fakeToken,
                idEmploye: user.idEmploye,
                nom: user.nom,
                prenom: user.prenom,
                email: role + '@demo.local',
                role: user.role
            }
        };
    },

    getLandingUrlForRole: function (role) {
        const normalizedRole = (role || '')
            .trim()
            .toUpperCase()
            .replace(/\s+/g, '_')
            .replace(/[-]/g, '_');

        const routes = {
            'ADMIN': '/admin',
            'ADMINISTRATEUR': '/admin',
            'RESPONSABLE_ACHATS': '/achats',
            'RESPONSABLE_PRODUCTION': '/production',
            'GESTIONNAIRE_DE_STOCK': '/stock',
            'RESPONSABLE_COMMERCIAL': '/commercial',
            'COMPTABLE': '/comptabilite',
            'LIVREUR': '/livraison'
        };

        return routes[normalizedRole] || '/api/matieres-premieres';
    },

    redirectIfAuthenticated: function () {
        const token = localStorage.getItem('vinaAkohoToken');
        const user = JSON.parse(localStorage.getItem('vinaAkohoUser') || 'null');

        if (token) {
            const destination = this.getLandingUrlForRole(user?.role);
            window.location.href = destination;
        }
    },

    logout: function () {
        localStorage.removeItem('vinaAkohoToken');
        localStorage.removeItem('vinaAkohoUser');

        fetch('/api/logout', {
            method: 'POST',
            credentials: 'same-origin'
        }).catch(function () {
            return null;
        }).finally(function () {
            window.location.href = '/';
        });
    }
};
