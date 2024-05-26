package capstone.eYakmoYak.medicine.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pre_id")
    private Prescription prescription;

//    @Column(nullable = false)
    private String name;

    private String dose_time;

    private int meal_time;

    private String imgUrl;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    @Builder.Default
    private String status = "A";

}
