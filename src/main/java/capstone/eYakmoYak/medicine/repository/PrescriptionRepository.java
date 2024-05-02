package capstone.eYakmoYak.medicine.repository;

import capstone.eYakmoYak.medicine.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
