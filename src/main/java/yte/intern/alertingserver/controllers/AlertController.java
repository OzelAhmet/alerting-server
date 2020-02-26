package yte.intern.alertingserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.models.Response;
import yte.intern.alertingserver.services.AlertService;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/all")
    public List<Alert> getAllAlerts(){
        return alertService.getAllAlerts();
    }

    @GetMapping("/{alertId}")
    public Alert getAlert(@PathVariable Long alertId){
        return alertService.getAlertById(alertId);
        /*
        try {
            return new ResponseEntity<>(alertService.getAlertById(alertId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Not Found.\nError: "+e.getMessage(), HttpStatus.NOT_FOUND);
        }
         */
    }

    @GetMapping("/{alertId}/results")
    public Set<Response> getResultsOfAlert(@PathVariable Long alertId){
        return alertService.getAlertById(alertId).getResults();
    }

    @PostMapping("/add")
    public Alert addAlert(@RequestBody final Alert alert){
        return alertService.addAlert(alert);
    }

    @PutMapping("/update")
    public Alert updateAlert(@RequestBody final Alert alert){
        return alertService.updateAlert(alert);
    }

    @DeleteMapping("/delete/{alertId}")
    public void deleteAlert(@PathVariable Long alertId){
        alertService.deleteAlertById(alertId);
    }
}
