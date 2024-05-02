package capstone.eYakmoYak.auth.domain;

import capstone.eYakmoYak.medicine.domain.Prescription;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String username;

    @CreationTimestamp
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    private String status = "A";

    private int preCount = 0;

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();

    public void addPrescription(Prescription prescription){
        prescriptions.add(prescription);
        prescription.setUser(this);
        preCount++;
    }

    public void removePrescription(Prescription prescription){
        prescriptions.remove(prescription);
        prescription.setUser(null);
        preCount++;
    }

}
