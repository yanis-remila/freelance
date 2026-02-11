import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { FreelanceControllerService, Freelancer } from "../../api";
import { useParams } from 'react-router-dom';
import { NotificationBell } from "../../components/notifications";
import { useNotifications } from "../../context/NotificationContext";

const FreelanceDashboard: React.FC = () => {
    const { nom } = useParams<{ nom: string }>();
    const [freelance, setFreelance] = useState<Freelancer | null>(null);
    const [loading, setLoading] = useState(true);
    const [freelanceId, setFreelanceId] = useState<number | null>(null);
    const { setFreelancerId } = useNotifications();

    useEffect(() => {
        const fetchData = async () => {
            if (!nom) {
                toast.error("Nom du freelance non spécifié.");
                setLoading(false);
                return;
            }
            try {
                const response = await FreelanceControllerService.getFreelanceByEmail({ email: nom });
                setFreelance(response);
                if (response.id !== undefined) {
                    setFreelanceId(response.id);
                    setFreelancerId(response.id);
                }
            } catch (error) {
                toast.error("Erreur lors de la récupération des informations freelance.");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [nom, setFreelancerId]);
    
    return (
        <div className="flex h-screen bg-gray-100">
            <aside className="w-64 bg-blue-600 text-white p-5">
                <h2 className="text-2xl font-bold">Dashboard Freelance</h2>
                <nav className="mt-5 space-y-3">
                    {freelanceId ? (
                        <a 
                            href={`/freelances/${freelanceId}/missions`} 
                            className="block px-3 py-2 rounded hover:bg-blue-700"
                            data-testid="mes-missions-link"
                        >
                            Trouver une mission
                        </a>
                    ) : (
                        <span className="block px-3 py-2 rounded text-gray-400 cursor-not-allowed">
                            Trouver une mission
                            </span>
                    )}
                </nav>
            </aside>

            <div className="flex-1 p-8">
                <header className="mb-8 flex justify-between items-center">
                    <h1 className="text-3xl font-semibold">Bienvenue sur votre espace freelance</h1>
                    <NotificationBell />
                </header>

                <div className="bg-white p-6 rounded-lg shadow-lg">
                    <h2 className="text-2xl font-semibold mb-4">Informations Freelance</h2>
                    {loading ? (
                        <p className="text-center">Chargement...</p>
                    ) : freelance ? (
                        <div className="space-y-2">
                            <p><strong>Nom:</strong> {freelance.nom}</p>
                            <p><strong>Prénom:</strong> {freelance.prenom}</p>
                            <p><strong>Email:</strong> {freelance.email}</p>
                            <p><strong>Expérience:</strong> {freelance.experience} ans</p>
                            <p><strong>Âge:</strong> {freelance.age} ans</p>
                            <p><strong>Genre:</strong> {freelance.gender}</p>
                            <p><strong>Profil:</strong> {freelance.profil}</p>
                            <p><strong>Compétences:</strong></p>
                            <ul className="list-disc pl-5">
                                {freelance.competences?.map((comp, index) => (
                                    <li key={index}>{comp.nom}</li>
                                ))}
                            </ul>
                        </div>
                    ) : (
                        <p className="text-center text-red-500">Aucune donnée trouvée.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default FreelanceDashboard;
