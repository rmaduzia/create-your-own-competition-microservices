package pl.createcompetition.notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.createcompetition.model.websockets.UserNotification;

@Repository
public interface NotificationRepository extends MongoRepository<UserNotification, String> {
}
