package capstone.eYakmoYak.medicine.repository;

import capstone.eYakmoYak.medicine.domain.Contraindication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContraindicationRepository extends JpaRepository<Contraindication, Long> {
    List<Contraindication> findByMedAContaining(String med_a);

    List<Contraindication> findByMedBContaining(String med_b);
}
