package yte.intern.alertingserver.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    @Id
    @GeneratedValue
    private Long id;

    private Integer responseCode;
    private LocalDateTime time;

    public Response(Integer responseCode, LocalDateTime time) {
        this.responseCode = responseCode;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", responseCode=" + responseCode +
                ", time=" + time +
                '}';
    }
}
