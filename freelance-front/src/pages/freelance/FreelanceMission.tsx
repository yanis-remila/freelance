import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { RecommendationControllerService } from "../../api";
import type { Mission } from "../../api/models/Mission";
import { NotificationBell } from "../../components/notifications";
import { useNotifications } from "../../context/NotificationContext";

const FreelanceMissions: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [missions, setMissions] = useState<Mission[]>([]);
    const [loading, setLoading] = useState(true);
    const { setFreelancerId } = useNotifications();

    useEffect(() => {
        if (id) {
            setFreelancerId(Number(id));
        }
    }, [id, setFreelancerId]);

    useEffect(() => {
        const fetchMissions = async () => {
            if (!id) {
                toast.error("ID du client non spécifié.");
                setLoading(false);
                return;
            }
            try {
                const response = await RecommendationControllerService.getMissionRecommendations({ freelanceId: Number(id) });
                setMissions(response);
            } catch (error) {
                toast.error("Erreur lors de la récupération des missions.");
            } finally {
                setLoading(false);
            }
        };

        fetchMissions();
    }, [id]);

    return (
        <div className="container mx-auto p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-semibold">Missions recommandées</h1>
                <NotificationBell />
            </div>
            {loading ? (
                <p className="text-center">Chargement...</p>
            ) : missions.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {missions.map((mission) => (
                        <div 
                            key={mission.id} 
                            className="bg-white p-6 rounded-lg shadow-lg cursor-pointer hover:shadow-xl transition" 
                            
                            data-testid={`mission-${mission.id}`}
                        >
                            <h2 className="text-xl font-bold mb-2">{mission.titre}</h2>
                            <p className="text-gray-600">{mission.description}</p>
                            <p className="text-sm text-gray-500 mt-2"><strong>Budget:</strong> {mission.budget} €</p>
                            <p className="text-sm text-gray-500"><strong>Durée:</strong> {mission.duree}</p>
                            <p className={`text-sm mt-2 font-semibold ${mission.statut === 'ACCEPTEE' ? 'text-green-500' : mission.statut === 'EN_ATTENTE' ? 'text-yellow-500' : mission.statut === 'TERMINEE' ? 'text-blue-500' : 'text-red-500'}`}>
                                Statut: {mission.statut}
                            </p>
                        </div>
                    ))}
                </div>
            ) : (
                <p className="text-center text-red-500">Aucune mission trouvée.</p>
            )}
        </div>
    );
};

export default FreelanceMissions;
