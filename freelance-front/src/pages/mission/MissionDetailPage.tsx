import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import type { Mission } from '../../api/models/Mission';
import { NotificationBell } from '../../components/notifications';
import axios from 'axios';

const API_URL = process.env.REACT_APP_BACK_API_URL || 'http://localhost:9091';

const MissionDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [mission, setMission] = useState<Mission | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMission = async () => {
            if (!id) return;
            try {
                const response = await axios.get<Mission>(`${API_URL}/api/missions/${id}`);
                setMission(response.data);
            } catch (error) {
                toast.error('Mission introuvable.');
            } finally {
                setLoading(false);
            }
        };
        fetchMission();
    }, [id]);

    const getStatutStyle = (statut?: string) => {
        switch (statut) {
            case 'ACCEPTEE': return 'bg-green-100 text-green-700';
            case 'EN_ATTENTE': return 'bg-yellow-100 text-yellow-700';
            case 'TERMINEE': return 'bg-blue-100 text-blue-700';
            default: return 'bg-red-100 text-red-700';
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    if (!mission) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <p className="text-red-500 text-xl">Mission introuvable.</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-3xl mx-auto">
                <div className="flex justify-between items-center mb-6">
                    <button
                        onClick={() => navigate(-1)}
                        className="text-blue-600 hover:text-blue-800 font-medium"
                    >
                        &larr; Retour
                    </button>
                    <NotificationBell />
                </div>

                <div className="bg-white rounded-lg shadow-lg p-8">
                    <div className="flex items-center justify-between mb-6">
                        <h1 className="text-3xl font-bold text-gray-900">{mission.titre}</h1>
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatutStyle(mission.statut)}`}>
                            {mission.statut}
                        </span>
                    </div>

                    <p className="text-gray-700 text-lg leading-relaxed mb-6">{mission.description}</p>

                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div className="bg-gray-50 p-4 rounded-lg">
                            <p className="text-sm text-gray-500">Budget</p>
                            <p className="text-xl font-semibold text-gray-900">{mission.budget} &euro;</p>
                        </div>
                        <div className="bg-gray-50 p-4 rounded-lg">
                            <p className="text-sm text-gray-500">Dur&eacute;e</p>
                            <p className="text-xl font-semibold text-gray-900">{mission.duree}</p>
                        </div>
                    </div>

                    {mission.client && (
                        <div className="bg-gray-50 p-4 rounded-lg mb-6">
                            <p className="text-sm text-gray-500">Client</p>
                            <p className="text-lg font-semibold text-gray-900">
                                {mission.client.prenom} {mission.client.nom}
                            </p>
                        </div>
                    )}

                    {mission.competences && mission.competences.length > 0 && (
                        <div>
                            <p className="text-sm text-gray-500 mb-2">Comp&eacute;tences requises</p>
                            <div className="flex flex-wrap gap-2">
                                {mission.competences.map((comp) => (
                                    <span
                                        key={comp.id}
                                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm font-medium"
                                    >
                                        {comp.nom}
                                    </span>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default MissionDetailPage;
