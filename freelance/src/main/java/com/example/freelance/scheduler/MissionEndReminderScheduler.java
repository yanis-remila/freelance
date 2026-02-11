package com.example.freelance.scheduler;

import com.example.freelance.dto.MissionEndingEvent;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.MissionFreelance;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionFreelanceRepository;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.service.MissionEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Scheduler qui détecte les missions arrivant à leur fin et publie des événements sur Kafka.
 *
 * ARCHITECTURE MICROSERVICES:
 * - Ce scheduler ne crée plus les notifications directement
 * - Il publie des événements sur Kafka (topic: mission-ending)
 * - Le microservice notification-service consomme ces événements
 * - Cette séparation permet une meilleure scalabilité
 *
 * SCALABILITE avec 400 missions:
 * - Les 400 événements sont publiés rapidement sur Kafka (~quelques ms chacun)
 * - Le notification-service peut avoir plusieurs instances qui consomment en parallèle
 * - Kafka garantit la livraison même si notification-service est temporairement down
 */
@Component
public class MissionEndReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MissionEndReminderScheduler.class);

    private final MissionFreelanceRepository missionFreelanceRepository;
    private final MissionRepository missionRepository;
    private final FreelancerRepository freelancerRepository;
    private final MissionEventProducer missionEventProducer;

    public MissionEndReminderScheduler(MissionFreelanceRepository missionFreelanceRepository,
                                        MissionRepository missionRepository,
                                        FreelancerRepository freelancerRepository,
                                        MissionEventProducer missionEventProducer) {
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.missionRepository = missionRepository;
        this.freelancerRepository = freelancerRepository;
        this.missionEventProducer = missionEventProducer;
    }

    /**
     * S'exécute tous les jours à 9h00 (en production).
     * Expression cron actuelle: toutes les 2 minutes (pour les tests).
     *
     * Détecte les missions qui se terminent dans 3 jours et publie
     * un événement Kafka pour chaque mission.
     */
    @Scheduled(cron = "0 */2 * * * *")  // Toutes les 2 minutes (pour les tests)
    // @Scheduled(cron = "0 0 9 * * *")  // En production: tous les jours à 9h00
    public void sendMissionEndReminders() {
        logger.info("Starting mission end reminder scheduler...");

        LocalDate targetDate = LocalDate.now().plusDays(3);

        List<MissionFreelance> missionsEndingSoon = missionFreelanceRepository.findMissionsEndingOn(targetDate);

        logger.info("Found {} missions ending on {}", missionsEndingSoon.size(), targetDate);

        int publishedCount = 0;
        int errorCount = 0;

        for (MissionFreelance missionFreelance : missionsEndingSoon) {
            try {
                // Récupérer les informations complètes
                Optional<Mission> missionOpt = missionRepository.findById(missionFreelance.getMissionId());
                Optional<Freelancer> freelancerOpt = freelancerRepository.findById(missionFreelance.getFreelancerId());

                if (missionOpt.isEmpty() || freelancerOpt.isEmpty()) {
                    logger.warn("Mission or Freelancer not found for missionId={}, freelancerId={}",
                            missionFreelance.getMissionId(), missionFreelance.getFreelancerId());
                    continue;
                }

                Mission mission = missionOpt.get();
                Freelancer freelancer = freelancerOpt.get();

                // IMPORTANT: Marquer comme notifié AVANT de publier sur Kafka
                // pour éviter que le scheduler ne trouve à nouveau cette mission
                missionFreelance.setNotificationEnvoyee(true);
                missionFreelanceRepository.saveAndFlush(missionFreelance);
                logger.info("Marked mission {} as notified for freelancer {}",
                        mission.getId(), freelancer.getId());

                // Créer l'événement Kafka
                MissionEndingEvent event = new MissionEndingEvent(
                        mission.getId(),
                        freelancer.getId(),
                        freelancer.getEmail(),
                        mission.getTitre(),
                        missionFreelance.getDateFin(),
                        3  // jours restants
                );

                // Publier sur Kafka
                missionEventProducer.publishMissionEndingEvent(event);

                publishedCount++;
                logger.info("Published event for mission {} to freelancer {}",
                        mission.getId(), freelancer.getId());

            } catch (Exception e) {
                errorCount++;
                logger.error("Failed to publish event for mission {}: {}",
                        missionFreelance.getMissionId(), e.getMessage());
            }
        }

        logger.info("Mission end reminder scheduler completed. Published: {}, Errors: {}",
                publishedCount, errorCount);
    }

    /**
     * Méthode pour déclencher manuellement le scheduler (pour les tests).
     */
    public void triggerManually() {
        sendMissionEndReminders();
    }
}
