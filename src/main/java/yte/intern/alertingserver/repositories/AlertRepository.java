package yte.intern.alertingserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import yte.intern.alertingserver.models.Alert;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByOrderByIdAsc();
}
