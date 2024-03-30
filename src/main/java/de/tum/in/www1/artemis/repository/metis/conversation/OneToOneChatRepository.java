package de.tum.in.www1.artemis.repository.metis.conversation;

import static de.tum.in.www1.artemis.config.Constants.PROFILE_CORE;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.tum.in.www1.artemis.domain.metis.conversation.OneToOneChat;
import de.tum.in.www1.artemis.web.rest.errors.EntityNotFoundException;

@Profile(PROFILE_CORE)
@Repository
public interface OneToOneChatRepository extends JpaRepository<OneToOneChat, Long> {

    /**
     * Find all active one-to-one chats of a given user in a given course.
     * <p>
     * We join the conversionParticipants twice, because the first time we use it for filtering the chats and binding it to the user ID. The second time, we fetch all participants;
     * as it's a one-to-one chat, two.
     *
     * @param courseId the ID of the course to search in
     * @param userId   the ID of the user to search for
     * @return a list of one-to-one chats
     */
    @Query("""
            SELECT DISTINCT oneToOneChat
            FROM OneToOneChat oneToOneChat
                LEFT JOIN oneToOneChat.conversationParticipants matchingParticipant
                LEFT JOIN FETCH oneToOneChat.conversationParticipants allParticipants
                LEFT JOIN FETCH allParticipants.user user
                LEFT JOIN FETCH user.groups
            WHERE oneToOneChat.course.id = :courseId
                AND (oneToOneChat.lastMessageDate IS NOT NULL OR oneToOneChat.creator.id = :userId)
                AND matchingParticipant.user.id = :userId
            ORDER BY oneToOneChat.lastMessageDate DESC
            """)
    List<OneToOneChat> findAllWithParticipantsAndUserGroupsByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * Find a one-to-one chat between two users in a given course.
     * <p>
     * We join the conversationParticipants twice because we need two different participants to match the two users. If we would only join it once, we had only one participant and
     * multiple results.
     *
     * @param courseId the ID of the course to search in
     * @param userIdA  the ID of the first user
     * @param userIdB  the ID of the second user
     * @return an optional one-to-one chat
     */
    @Query("""
            SELECT DISTINCT o
            FROM OneToOneChat o
                LEFT JOIN FETCH o.conversationParticipants p1
                LEFT JOIN FETCH o.conversationParticipants p2
                LEFT JOIN FETCH p1.user u1
                LEFT JOIN FETCH p2.user u2
                LEFT JOIN FETCH u1.groups
                LEFT JOIN FETCH u2.groups
            WHERE o.course.id = :courseId
                AND u1.id = :userIdA
                AND u2.id = :userIdB
            """)
    Optional<OneToOneChat> findWithParticipantsAndUserGroupsInCourseBetweenUsers(@Param("courseId") Long courseId, @Param("userIdA") Long userIdA, @Param("userIdB") Long userIdB);

    @Query("""
            SELECT DISTINCT oneToOneChat
            FROM OneToOneChat oneToOneChat
                LEFT JOIN FETCH oneToOneChat.conversationParticipants p
                LEFT JOIN FETCH p.user u
                LEFT JOIN FETCH u.groups
            WHERE oneToOneChat.id = :oneToOneChatId
            """)
    Optional<OneToOneChat> findByIdWithConversationParticipantsAndUserGroups(@Param("oneToOneChatId") Long oneToOneChatId) throws EntityNotFoundException;

    Integer countByCreatorIdAndCourseId(Long creatorId, Long courseId);
}
