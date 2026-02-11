import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Notification, NotificationType } from '../../types/notification';

interface NotificationItemProps {
    notification: Notification;
    onMarkAsRead: (id: number) => void;
    onClose?: () => void;
}

const NotificationItem: React.FC<NotificationItemProps> = ({ notification, onMarkAsRead, onClose }) => {
    const navigate = useNavigate();
    const formatDate = (dateString: string): string => {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now.getTime() - date.getTime();
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 1) return "À l'instant";
        if (diffMins < 60) return `Il y a ${diffMins} min`;
        if (diffHours < 24) return `Il y a ${diffHours}h`;
        if (diffDays < 7) return `Il y a ${diffDays}j`;
        return date.toLocaleDateString('fr-FR');
    };

    const getIcon = (type: NotificationType): string => {
        switch (type) {
            case NotificationType.RAPPEL_FIN_MISSION:
                return '⏰';
            case NotificationType.MISSION_ASSIGNEE:
                return '📋';
            default:
                return '🔔';
        }
    };

    const getTypeLabel = (type: NotificationType): string => {
        switch (type) {
            case NotificationType.RAPPEL_FIN_MISSION:
                return 'Rappel';
            case NotificationType.MISSION_ASSIGNEE:
                return 'Nouvelle mission';
            default:
                return 'Notification';
        }
    };

    const handleClick = () => {
        if (!notification.isRead) {
            onMarkAsRead(notification.id);
        }
        if (notification.missionId && onClose) {
            onClose();
            navigate(`/missions/${notification.missionId}`);
        }
    };

    return (
        <div
            onClick={handleClick}
            className={`p-4 border-b border-gray-100 cursor-pointer transition-colors duration-200 hover:bg-gray-50 ${
                !notification.isRead ? 'bg-blue-50' : 'bg-white'
            }`}
        >
            <div className="flex items-start gap-3">
                <span className="text-2xl flex-shrink-0">
                    {getIcon(notification.type)}
                </span>
                <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between mb-1">
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                            notification.type === NotificationType.RAPPEL_FIN_MISSION
                                ? 'bg-orange-100 text-orange-700'
                                : 'bg-green-100 text-green-700'
                        }`}>
                            {getTypeLabel(notification.type)}
                        </span>
                        <span className="text-xs text-gray-400">
                            {formatDate(notification.createdAt)}
                        </span>
                    </div>
                    <p className={`text-sm leading-snug ${
                        !notification.isRead ? 'text-gray-900 font-medium' : 'text-gray-600'
                    }`}>
                        {notification.message}
                    </p>
                    {!notification.isRead && (
                        <div className="mt-2">
                            <span className="inline-block w-2 h-2 bg-blue-500 rounded-full"></span>
                            <span className="text-xs text-blue-500 ml-1">Non lu</span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default NotificationItem;
