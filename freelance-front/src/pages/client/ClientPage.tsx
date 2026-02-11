import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { Client, ClientControllerService } from "../../api"; // Import service
import { useParams } from 'react-router-dom';

const ClientDashboard: React.FC = () => {
    //extraire le nom dans url
    const { nom } = useParams<{ nom: string }>();
    //declarer les variable usestat pour declarer tous changement apres 
    const [client, setClient] = useState<Client | null>(null);
    const [loading, setLoading] = useState(true);
    const [clientId, setClientId] = useState<number | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            if (!nom) {
                toast.error("Nom du client non spécifié.");
                setLoading(false);
                return;
            }
            try {
                const response = await ClientControllerService.findClientByNom({ nom });
                setClient(response);
                if (response.id !== undefined) {
                    setClientId(response.id);
                }
            } catch (error) {
                toast.error("Erreur lors de la récupération des informations client.");
            } finally {
                setLoading(false);
            }
        };
    
        fetchData();
    }, [nom]); 
    

    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <aside className="w-64 bg-blue-600 text-white p-5">
                <h2 className="text-2xl font-bold">Dashboard</h2>
                <nav className="mt-5 space-y-3">
                    {clientId ? (
                        <a 
                            href={`/clients/${clientId}/missions`} 
                            className="block px-3 py-2 rounded hover:bg-blue-700"
                            data-testid="mes-missions-link"
                        >
                            Mes Missions
                        </a>
                    ) : (
                        <span className="block px-3 py-2 rounded text-gray-400 cursor-not-allowed">
                            Mes Missions
                        </span>
                    )}
               </nav>
            </aside>

            {/* Main Contenu */}
            <div className="flex-1 p-8">
                {/* Header */}
                <header className="mb-8 flex justify-between items-center">
                    <h1 className="text-3xl font-semibold">Bienvenue sur votre espace client</h1>
                </header>

                {/* information de client */}
                <div className="bg-white p-6 rounded-lg shadow-lg">
                    <h2 className="text-2xl font-semibold mb-4">Informations Client</h2>
                    {loading ? (
                        <p className="text-center">Chargement...</p>
                    ) : client ? (
                        <div className="space-y-2">
                            <p><strong>Nom:</strong> {client.nom}</p>
                            <p><strong>Email:</strong> {client.email}</p>
                        </div>
                    ) : (
                        <p className="text-center text-red-500">Aucune donnée trouvée.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ClientDashboard;
