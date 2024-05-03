package capstone.eYakmoYak.medicine.repository;

import capstone.eYakmoYak.medicine.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByUser_Id(Long id);
}
