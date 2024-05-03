package capstone.eYakmoYak.medicine.domain;

import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.GenerationType.*;

@Getter
@Entity
@Table(name = "contraindication")
public class Contraindication {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "con_id")
    private Long id;

    @Column(name = "med_a")
    private String med_a;

    @Column(name = "med_b")
    private int med_b;

    @Column(name = "reason")
    private String reason;
}
