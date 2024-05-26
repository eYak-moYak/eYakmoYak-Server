package capstone.eYakmoYak.medicine.domain;

import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.GenerationType.*;

@Getter
@Entity
public class Contraindication {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "con_id")
    private Long id;

    @Column(name = "med_a")
    private String medA;

    @Column(name = "med_b")
    private String medB;

    @Column(name = "reason")
    private String reason;
}
