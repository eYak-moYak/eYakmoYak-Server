package capstone.eYakmoYak.medicine.domain;

import capstone.eYakmoYak.auth.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String pre_name;

    private String hospital;

    private String pharmacy;

    @CreationTimestamp
    private LocalDate pre_date;

    @CreationTimestamp
    private LocalDate start_date;

    @CreationTimestamp
    private LocalDate end_date;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    @Builder.Default
    private String status = "A";

    @Builder.Default
    @OneToMany(mappedBy = "prescription", cascade = ALL, orphanRemoval = true)
    private List<Medicine> medicines = new ArrayList<>();


    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
        medicine.setPrescription(this);
    }

    public void removeMedicine(Medicine medicine) {
        medicines.remove(medicine);
        medicine.setPrescription(null);
    }

    @PrePersist
    private void setDefaultName(){
        if(pre_name == null || pre_name.isBlank()){
            pre_name = "처방전" + user.getPreCount();
        }
    }
}
