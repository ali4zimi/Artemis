package de.tum.in.www1.artemis.repository.metis.conversation;

import static de.tum.in.www1.artemis.config.Constants.PROFILE_CORE;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.tum.in.www1.artemis.domain.notification.ConversationNotification;

@Profile(PROFILE_CORE)
@Repository
public interface ConversationNotificationRepository extends JpaRepository<ConversationNotification, Long> {

}
