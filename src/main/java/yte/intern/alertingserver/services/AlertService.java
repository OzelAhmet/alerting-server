package yte.intern.alertingserver.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.repositories.AlertRepository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;

    public List<Alert> getAllAlerts(){
        return alertRepository.findAllByOrderByIdAsc();
    }

    public Alert getAlertById(Long id){
        return alertRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with ID: " + id)
        );
    }

    public Alert addAlert(final Alert alert){
        try {
            // Try url if it is OK
            new URL(alert.getUrl());
            return alertRepository.save(alert);
        } catch (Exception e){
            throw new PersistenceException("URL is wrong!");
        }
    }

    public Alert updateAlert(Alert alert){
        // Try to find id first or throw exception
        Alert oldAlert = getAlertById(alert.getId());

        try {
            new URL(alert.getUrl());
            // don't lose the results and time
            Alert updateAlert = new Alert(
                    // Get new data from the alert parameter (request body)
                    alert.getId(),
                    alert.getName(),
                    alert.getUrl(),
                    alert.getMethod(),
                    alert.getPeriod(),
                    // Get response data and timeToCheck from old version
                    oldAlert.getTimeToCheck(),
                    oldAlert.getResults()
            );

            return alertRepository.save(updateAlert);
        } catch (MalformedURLException e){
            throw new PersistenceException("URL is wrong!");
        }
    }

    public void deleteAlertById(Long id){
        alertRepository.deleteById(id);
    }
}
