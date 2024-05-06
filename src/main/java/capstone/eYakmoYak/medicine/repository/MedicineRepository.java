package capstone.eYakmoYak.medicine.repository;

import capstone.eYakmoYak.medicine.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByName(String name);
}
