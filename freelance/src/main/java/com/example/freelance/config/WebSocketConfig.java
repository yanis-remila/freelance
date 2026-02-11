package com.example.freelance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration du WebSocket avec le protocole STOMP.
 *
 * WebSocket : Protocole de communication bidirectionnelle entre client et serveur.
 * Contrairement à HTTP (requête/réponse), WebSocket maintient une connexion ouverte
 * permettant au serveur d'envoyer des données au client à tout moment (push).
 *
 * STOMP (Simple Text Oriented Messaging Protocol) :
 * Protocole de messagerie qui s'exécute au-dessus de WebSocket.
 * Il ajoute des concepts comme :
 * - Destinations (canaux) : ex: /topic/notifications/1
 * - Subscriptions : Le client s'abonne à une destination
 * - Messages : Le serveur envoie des messages sur une destination
 *
 * SockJS : Bibliothèque JavaScript qui fournit un fallback pour les navigateurs
 * qui ne supportent pas WebSocket nativement. Elle utilise des techniques alternatives
 * comme le long-polling ou les iframes.
 *
 * Flux de communication :
 *
 * CLIENT                                    SERVEUR
 *   │                                          │
 *   │─────── Connexion WebSocket (/ws) ───────►│
 *   │                                          │
 *   │─── SUBSCRIBE /topic/notifications/1 ────►│
 *   │                                          │
 *   │                (le client attend...)     │
 *   │                                          │
 *   │◄──── MESSAGE (notification JSON) ────────│  (quand le serveur a quelque chose à envoyer)
 *   │                                          │
 */
@Configuration  // Indique à Spring que c'est une classe de configuration
@EnableWebSocketMessageBroker  // Active le support WebSocket avec message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure le message broker (gestionnaire de messages).
     *
     * Le message broker est responsable de :
     * - Recevoir les messages du serveur
     * - Les router vers les clients abonnés aux bonnes destinations
     *
     * @param registry Registre de configuration du broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
         * Active un broker simple en mémoire pour les destinations
         * commençant par "/topic" ou "/queue".
         *
         * /topic : Utilisé pour le broadcast (1 message → plusieurs clients)
         *          Ex: /topic/notifications/1 → tous les onglets du freelancer 1
         *
         * /queue : Utilisé pour les messages point-à-point (1 message → 1 client)
         *
         * Note: En production, on utiliserait un broker externe (RabbitMQ, Redis)
         * pour supporter plusieurs instances de l'application.
         */
        registry.enableSimpleBroker("/topic", "/queue");

        /*
         * Préfixe pour les messages envoyés PAR le client VERS le serveur.
         * Ex: Le client envoie un message à /app/send
         *     → Le serveur reçoit le message dans un @MessageMapping("/send")
         *
         * Non utilisé dans notre cas car le client ne fait que recevoir.
         */
        registry.setApplicationDestinationPrefixes("/app");

        /*
         * Préfixe pour les destinations spécifiques à un utilisateur.
         * Ex: /user/queue/private → messages privés pour l'utilisateur connecté
         *
         * Utile avec Spring Security pour cibler un utilisateur authentifié.
         */
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Configure les endpoints WebSocket (points d'entrée).
     *
     * Un endpoint est l'URL à laquelle le client se connecte pour établir
     * la connexion WebSocket.
     *
     * @param registry Registre des endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * Enregistre l'endpoint "/ws" pour les connexions WebSocket.
         *
         * Le client se connecte avec :
         *   const socket = new SockJS('http://localhost:9091/ws');
         *
         * Configuration :
         * - addEndpoint("/ws") : URL de connexion
         * - setAllowedOriginPatterns("*") : Autorise toutes les origines (CORS)
         *   ⚠️ En production, restreindre aux domaines autorisés !
         * - withSockJS() : Active le fallback SockJS pour compatibilité navigateur
         */
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // Permet les connexions de n'importe quel domaine
                .withSockJS();  // Fallback pour navigateurs sans WebSocket natif
    }
}
