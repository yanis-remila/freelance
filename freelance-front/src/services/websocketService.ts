import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notification } from '../types/notification';

// URL du microservice notification pour WebSocket
const WS_URL = process.env.REACT_APP_NOTIFICATION_API_URL || 'http://localhost:9092';

type NotificationCallback = (notification: Notification) => void;

class WebSocketService {
    private client: Client | null = null;
    private isConnected: boolean = false;
    private callbacks: NotificationCallback[] = [];
    private freelancerId: number | null = null;
    private reconnectAttempts: number = 0;
    private maxReconnectAttempts: number = 5;

    /**
     * Connecte au serveur WebSocket et s'abonne aux notifications
     */
    connect(freelancerId: number): void {
        if (this.isConnected && this.freelancerId === freelancerId) {
            console.log('WebSocket already connected for this freelancer');
            return;
        }

        this.freelancerId = freelancerId;
        this.disconnect();

        this.client = new Client({
            webSocketFactory: () => new SockJS(`${WS_URL}/ws`),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            debug: (str) => {
                console.log('[WebSocket Debug]', str);
            },
            onConnect: () => {
                console.log('WebSocket connected successfully');
                this.isConnected = true;
                this.reconnectAttempts = 0;
                this.subscribeToNotifications(freelancerId);
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame.headers['message']);
                this.isConnected = false;
            },
            onDisconnect: () => {
                console.log('WebSocket disconnected');
                this.isConnected = false;
            },
            onWebSocketClose: () => {
                console.log('WebSocket connection closed');
                this.isConnected = false;
                this.handleReconnect();
            },
        });

        this.client.activate();
    }

    /**
     * S'abonne au topic des notifications pour un freelancer
     */
    private subscribeToNotifications(freelancerId: number): void {
        if (!this.client || !this.isConnected) return;

        const destination = `/topic/notifications/${freelancerId}`;

        this.client.subscribe(destination, (message: IMessage) => {
            try {
                const notification: Notification = JSON.parse(message.body);
                console.log('Notification received:', notification);
                this.notifyCallbacks(notification);
            } catch (error) {
                console.error('Error parsing notification:', error);
            }
        });

        console.log(`Subscribed to ${destination}`);
    }

    /**
     * Gère la reconnexion automatique
     */
    private handleReconnect(): void {
        if (this.reconnectAttempts < this.maxReconnectAttempts && this.freelancerId) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            setTimeout(() => {
                if (this.freelancerId) {
                    this.connect(this.freelancerId);
                }
            }, 5000);
        }
    }

    /**
     * Déconnecte du serveur WebSocket
     */
    disconnect(): void {
        if (this.client) {
            this.client.deactivate();
            this.client = null;
            this.isConnected = false;
            console.log('WebSocket disconnected');
        }
    }

    /**
     * Ajoute un callback pour recevoir les notifications
     * Vérifie qu'il n'existe pas déjà pour éviter les doublons
     */
    addCallback(callback: NotificationCallback): void {
        if (!this.callbacks.includes(callback)) {
            this.callbacks.push(callback);
            console.log(`[WebSocket] Callback added. Total callbacks: ${this.callbacks.length}`);
        } else {
            console.log('[WebSocket] Callback already exists, skipping');
        }
    }

    /**
     * Supprime un callback
     */
    removeCallback(callback: NotificationCallback): void {
        this.callbacks = this.callbacks.filter(cb => cb !== callback);
    }

    /**
     * Notifie tous les callbacks d'une nouvelle notification
     */
    private notifyCallbacks(notification: Notification): void {
        this.callbacks.forEach(callback => callback(notification));
    }

    /**
     * Vérifie si la connexion est active
     */
    isActive(): boolean {
        return this.isConnected;
    }
}

export const websocketService = new WebSocketService();
export default websocketService;
