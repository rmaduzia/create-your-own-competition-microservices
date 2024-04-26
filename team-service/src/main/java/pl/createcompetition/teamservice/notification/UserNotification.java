package pl.createcompetition.teamservice.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "usernotification")
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipient;
    private String content;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }
}
