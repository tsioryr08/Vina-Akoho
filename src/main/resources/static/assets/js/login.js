window.VinaAkohoAuth = {
    login: async function (email, mdp) {
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email, mdp: mdp })
            });

            const body = await response.json();

            if (!response.ok || !body.success) {
                return {
                    ok: false,
                    message: body.message || 'Erreur de connexion',
                };
            }

            if (body.data && body.data.token) {
                localStorage.setItem('vinaAkohoToken', body.data.token);
                localStorage.setItem('vinaAkohoUser', JSON.stringify({
                    idEmploye: body.data.idEmploye,
                    nom: body.data.nom,
                    prenom: body.data.prenom,
                    email: body.data.email,
                    role: body.data.role
                }));
            }

            return {
                ok: true,
                message: body.message || 'Connexion réussie',
                data: body.data
            };
        } catch (error) {
            return {
                ok: false,
                message: 'Impossible de contacter le serveur',
            };
        }
    },

    getLandingUrlForRole: function (role) {
        const normalizedRole = (role || '')
            .trim()
            .toUpperCase()
            .replace(/\s+/g, '_')
            .replace(/[-]/g, '_');
        // Role -> landing route mapping. Update routes as modules are implemented.
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

        // Fallback to a safe default page when role is unknown
        return routes[normalizedRole] || '/matieres-premieres';
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
        window.location.href = '/';
    }
};
