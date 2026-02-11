-- ==============================================
-- Script de correction des notifications dupliquées
-- À exécuter sur la base de données: freelance_notifications (port 5433)
-- ==============================================

-- 1. Ajouter la colonne kafka_event_id si elle n'existe pas
ALTER TABLE notifications
ADD COLUMN IF NOT EXISTS kafka_event_id VARCHAR(255);

-- 2. Créer un index UNIQUE pour garantir l'idempotence
-- Cet index empêche l'insertion de deux notifications avec le même kafka_event_id
CREATE UNIQUE INDEX IF NOT EXISTS idx_notifications_kafka_event_id
ON notifications(kafka_event_id)
WHERE kafka_event_id IS NOT NULL;

-- 3. Créer un index pour améliorer les performances des requêtes
CREATE INDEX IF NOT EXISTS idx_notifications_created_at
ON notifications(created_at DESC);

-- ==============================================
-- Commande pour exécuter ce script:
-- ==============================================
-- Option 1 (via Docker):
-- docker exec -i <container_postgres_notifications> psql -U postgres -d freelance_notifications < fix-notifications-duplicates.sql
--
-- Option 2 (via psql direct):
-- psql -h localhost -p 5433 -U postgres -d freelance_notifications -f fix-notifications-duplicates.sql
--
-- Option 3 (via pgAdmin):
-- Ouvrir ce fichier et exécuter dans le Query Tool
-- ==============================================

-- Vérifier que la modification a été appliquée:
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'kafka_event_id';
-- SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'notifications' AND indexname = 'idx_notifications_kafka_event_id';
