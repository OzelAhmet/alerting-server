package yte.intern.alertingserver;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.models.Response;
import yte.intern.alertingserver.services.AlertService;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertScheduler {

    private final AlertService alertService;

    // make private
    int testConnection(String urlString, String method){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method);
            connection.connect();

            return connection.getResponseCode();

        } catch (Exception e){
            System.err.println("Connection Error!");
            e.printStackTrace();

            return 0;
        }
    }

    @Scheduled(fixedRate = 1000)
    public void schedule() {

        List<Alert> alertList = alertService.getAllAlerts();

        for (Alert alert: alertList){
            LocalDateTime ldt = LocalDateTime.now();
            if ( alert.getTimeToCheck().isBefore(ldt) ){
                // Set next time to check
                alert.setTimeToCheck( ldt.plusSeconds(alert.getPeriod()) );

                int respCode = testConnection(alert.getUrl(), alert.getMethod());
                System.out.println(alert.getName() + " responded " + respCode);
                alert.getResults().add(new Response(respCode, ldt));
                alertService.addAlert(alert);
            }
        }

        System.out.println("---");
    }

}
