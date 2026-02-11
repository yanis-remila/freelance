import React from 'react';
import { useNotifications } from '../../context/NotificationContext';
import NotificationItem from './NotificationItem';

interface NotificationPanelProps {
    isOpen: boolean;
    onClose: () => void;
}

const NotificationPanel: React.FC<NotificationPanelProps> = ({ isOpen, onClose }) => {
    const { notifications, loading, markAsRead, markAllAsRead, unreadCount } = useNotifications();

    if (!isOpen) return null;

    return (
        <>
            {/* Overlay */}
            <div
                className="fixed inset-0 z-40"
                onClick={onClose}
            />

            {/* Panel */}
            <div className="absolute right-0 top-full mt-2 w-96 max-h-[500px] bg-white rounded-lg shadow-xl border border-gray-200 z-50 overflow-hidden">
                {/* Header */}
                <div className="px-4 py-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
                    <h3 className="font-semibold text-gray-800">
                        Notifications
                        {unreadCount > 0 && (
                            <span className="ml-2 text-sm text-blue-600">
                                ({unreadCount} non lue{unreadCount > 1 ? 's' : ''})
                            </span>
                        )}
                    </h3>
                    {unreadCount > 0 && (
                        <button
                            onClick={markAllAsRead}
                            className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                        >
                            Tout marquer comme lu
                        </button>
                    )}
                </div>

                {/* Content */}
                <div className="overflow-y-auto max-h-[400px]">
                    {loading ? (
                        <div className="flex items-center justify-center py-8">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                        </div>
                    ) : notifications.length === 0 ? (
                        <div className="py-12 text-center">
                            <span className="text-4xl mb-3 block">🔔</span>
                            <p className="text-gray-500">Aucune notification</p>
                        </div>
                    ) : (
                        notifications.map(notification => (
                            <NotificationItem
                                key={notification.id}
                                notification={notification}
                                onMarkAsRead={markAsRead}
                                onClose={onClose}
                            />
                        ))
                    )}
                </div>

                {/* Footer */}
                {notifications.length > 0 && (
                    <div className="px-4 py-3 bg-gray-50 border-t border-gray-200 text-center">
                        <span className="text-sm text-gray-500">
                            {notifications.length} notification{notifications.length > 1 ? 's' : ''}
                        </span>
                    </div>
                )}
            </div>
        </>
    );
};

export default NotificationPanel;
