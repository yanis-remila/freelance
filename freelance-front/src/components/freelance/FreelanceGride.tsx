import React from 'react';
import { FreelancerRecommendationDTO } from '../../api';

//
interface FreelanceGrideProps {
    freelances: FreelancerRecommendationDTO[];
}
//{ freelances }) extrait directement la liste de freelances passée en prop
const FreelanceGride: React.FC<FreelanceGrideProps> = ({ freelances}) => {
    return (
        <div className="overflow-x-auto shadow-lg rounded-lg bg-white p-4">
            <table className="min-w-full table-auto text-left">
                <thead className="bg-indigo-600 text-white">
                <tr>
                    <th className="px-6 py-3 text-sm font-semibold">Nom</th>
                    <th className="px-6 py-3 text-sm font-semibold">Prénom</th>
                    <th className="px-6 py-3 text-sm font-semibold">Score</th>
                    <th className="px-6 py-3 text-sm font-semibold">profil</th>

                </tr>
                </thead>
                <tbody className="text-gray-700">
                {freelances.map((freelance) => (
                    <tr
                        key={freelance.freelancerId}
                        className="border-b hover:bg-gray-50 transition duration-300 ease-in-out"
                    >
                        <td className="px-6 py-4">{freelance.nom}</td>
                        <td className="px-6 py-4">{freelance.prenom}</td>
                        <td className="px-6 py-4">{freelance.score}</td>
                        <td className="px-6 py-4">{freelance.profil}</td>
                
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default FreelanceGride;