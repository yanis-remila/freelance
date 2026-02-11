import React, { useState } from 'react';
import { useNotifications } from '../../context/NotificationContext';
import NotificationPanel from './NotificationPanel';

const NotificationBell: React.FC = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { unreadCount, freelancerId } = useNotifications();

    // Ne pas afficher si pas de freelancerId
    if (!freelancerId) return null;

    const togglePanel = () => {
        setIsOpen(!isOpen);
    };

    const closePanel = () => {
        setIsOpen(false);
    };

    return (
        <div className="relative">
            <button
                onClick={togglePanel}
                className="relative p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                aria-label="Notifications"
            >
                {/* Bell Icon */}
                <svg
                    className="w-6 h-6"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                    />
                </svg>

                {/* Badge */}
                {unreadCount > 0 && (
                    <span className="absolute -top-1 -right-1 flex items-center justify-center min-w-[20px] h-5 px-1 text-xs font-bold text-white bg-red-500 rounded-full animate-pulse">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            <NotificationPanel isOpen={isOpen} onClose={closePanel} />
        </div>
    );
};

export default NotificationBell;
