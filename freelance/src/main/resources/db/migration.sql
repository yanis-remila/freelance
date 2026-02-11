-- ==============================================
-- Script de migration pour le système de notifications
-- À exécuter sur PostgreSQL
-- ==============================================

-- 1. Ajouter les nouvelles colonnes à mission_freelance
ALTER TABLE mission_freelance
ADD COLUMN IF NOT EXISTS date_debut DATE;

ALTER TABLE mission_freelance
ADD COLUMN IF NOT EXISTS date_fin DATE;

ALTER TABLE mission_freelance
ADD COLUMN IF NOT EXISTS notification_envoyee BOOLEAN DEFAULT false;

-- 2. Créer la table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    freelancer_id BIGINT NOT NULL,
    mission_id BIGINT,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT false,
    sent_via_websocket BOOLEAN DEFAULT false
);

-- 3. Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_notifications_freelancer_id
ON notifications(freelancer_id);

CREATE INDEX IF NOT EXISTS idx_notifications_is_read
ON notifications(freelancer_id, is_read);

CREATE INDEX IF NOT EXISTS idx_mission_freelance_date_fin
ON mission_freelance(date_fin);

-- 4. Données de test (optionnel)
-- Insérer une mission de test qui se termine dans 3 jours
-- INSERT INTO mission_freelance (mission_id, freelancer_id, date_debut, date_fin, notification_envoyee)
-- VALUES (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days', false);
