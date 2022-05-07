package pl.createcompetition.model.websockets;


import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
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
