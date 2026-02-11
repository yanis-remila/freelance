import axios from 'axios';
import { Notification, UnreadCountResponse } from '../types/notification';

// URL du microservice notification (séparé du backend principal)
const NOTIFICATION_API_URL = process.env.REACT_APP_NOTIFICATION_API_URL || 'http://localhost:9092';

const api = axios.create({
    baseURL: `${NOTIFICATION_API_URL}/api/notifications`,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const notificationService = {
    /**
     * Récupère toutes les notifications d'un freelancer
     */
    async getNotifications(freelancerId: number): Promise<Notification[]> {
        const response = await api.get<Notification[]>(`/freelancer/${freelancerId}`);
        return response.data;
    },

    /**
     * Récupère uniquement les notifications non lues
     */
    async getUnreadNotifications(freelancerId: number): Promise<Notification[]> {
        const response = await api.get<Notification[]>(`/freelancer/${freelancerId}/unread`);
        return response.data;
    },

    /**
     * Récupère le nombre de notifications non lues
     */
    async getUnreadCount(freelancerId: number): Promise<number> {
        const response = await api.get<UnreadCountResponse>(`/freelancer/${freelancerId}/count`);
        return response.data.unreadCount;
    },

    /**
     * Marque une notification comme lue
     */
    async markAsRead(notificationId: number): Promise<Notification> {
        const response = await api.put<Notification>(`/${notificationId}/read`);
        return response.data;
    },

    /**
     * Marque toutes les notifications comme lues
     */
    async markAllAsRead(freelancerId: number): Promise<void> {
        await api.put(`/freelancer/${freelancerId}/read-all`);
    },
};

export default notificationService;
