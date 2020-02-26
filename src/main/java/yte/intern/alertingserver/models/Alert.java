package yte.intern.alertingserver.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String url;
    private String method;
    private Long period;
    private LocalDateTime timeToCheck = LocalDateTime.now();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "alert_id")
    @OrderBy("id ASC")
    private Set<Response> results;

    // Use this constructor for test purposes only
    /*
    public Alert(String name, String url, String method, Long period, LocalDateTime timeToCheck, Set<Response> results) {
        this.name = name;
        this.url = url;
        this.method = method;
        this.period = period;
        this.timeToCheck = timeToCheck;
        this.results = results;
    }
    */

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", period=" + period +
                ", timeToCheck=" + timeToCheck +
                ", results=" + results +
                '}';
    }
}
