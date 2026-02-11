import React, { createContext, useContext, useState, useEffect, useCallback, useRef, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { Notification } from '../types/notification';
import notificationService from '../services/notificationService';
import websocketService from '../services/websocketService';
import { toast } from 'react-toastify';

interface NotificationContextType {
    notifications: Notification[];
    unreadCount: number;
    loading: boolean;
    freelancerId: number | null;
    setFreelancerId: (id: number | null) => void;
    markAsRead: (notificationId: number) => Promise<void>;
    markAllAsRead: () => Promise<void>;
    refreshNotifications: () => Promise<void>;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

interface NotificationProviderProps {
    children: ReactNode;
}

export const NotificationProvider: React.FC<NotificationProviderProps> = ({ children }) => {
    const navigate = useNavigate();
    const navigateRef = useRef(navigate);
    navigateRef.current = navigate;

    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [unreadCount, setUnreadCount] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(false);
    const [freelancerId, setFreelancerId] = useState<number | null>(null);

    /**
     * Rafraîchit la liste des notifications
     */
    const refreshNotifications = useCallback(async () => {
        if (!freelancerId) return;

        setLoading(true);
        try {
            const [notifs, count] = await Promise.all([
                notificationService.getNotifications(freelancerId),
                notificationService.getUnreadCount(freelancerId)
            ]);
            setNotifications(notifs);
            setUnreadCount(count);
        } catch (error) {
            console.error('Error fetching notifications:', error);
        } finally {
            setLoading(false);
        }
    }, [freelancerId]);

    /**
     * Gère l'arrivée d'une nouvelle notification via WebSocket
     */
    const handleNewNotification = useCallback((notification: Notification) => {
        setNotifications(prev => [notification, ...prev]);
        setUnreadCount(prev => prev + 1);

        toast.info(notification.message, {
            position: 'top-right',
            autoClose: 5000,
            onClick: () => {
                if (notification.missionId) {
                    navigateRef.current(`/missions/${notification.missionId}`);
                }
            }
        });
    }, []);

    /**
     * Marque une notification comme lue
     */
    const markAsRead = useCallback(async (notificationId: number) => {
        try {
            await notificationService.markAsRead(notificationId);
            setNotifications(prev =>
                prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
            );
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch (error) {
            console.error('Error marking notification as read:', error);
            toast.error('Erreur lors de la mise à jour de la notification');
        }
    }, []);

    /**
     * Marque toutes les notifications comme lues
     */
    const markAllAsRead = useCallback(async () => {
        if (!freelancerId) return;

        try {
            await notificationService.markAllAsRead(freelancerId);
            setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
            setUnreadCount(0);
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
            toast.error('Erreur lors de la mise à jour des notifications');
        }
    }, [freelancerId]);

    /**
     * Effet pour connecter au WebSocket quand freelancerId change
     * Note: On ne met que freelancerId en dépendance pour éviter les reconnexions multiples
     */
    useEffect(() => {
        if (freelancerId) {
            refreshNotifications();
            websocketService.connect(freelancerId);
            websocketService.addCallback(handleNewNotification);
        }

        return () => {
            websocketService.removeCallback(handleNewNotification);
            websocketService.disconnect();
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [freelancerId]);

    const value: NotificationContextType = {
        notifications,
        unreadCount,
        loading,
        freelancerId,
        setFreelancerId,
        markAsRead,
        markAllAsRead,
        refreshNotifications
    };

    return (
        <NotificationContext.Provider value={value}>
            {children}
        </NotificationContext.Provider>
    );
};

export const useNotifications = (): NotificationContextType => {
    const context = useContext(NotificationContext);
    if (context === undefined) {
        throw new Error('useNotifications must be used within a NotificationProvider');
    }
    return context;
};

export default NotificationContext;
