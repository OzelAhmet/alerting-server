package yte.intern.alertingserver.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import yte.intern.alertingserver.models.Alert;

import java.util.List;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase
class AlertRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    AlertRepository alertRepository;

    @BeforeEach
    void setUp() {
        Alert alert = new Alert();
        alert.setName("google");
        alert.setUrl("http://google.com");
        alert.setMethod("GET");
        alert.setPeriod(10L);
        testEntityManager.persist(alert);

        Alert alert2 = new Alert();
        alert.setName("facebook");
        alert.setUrl("http://facebook.com");
        alert.setMethod("GET");
        alert.setPeriod(10L);
        testEntityManager.persist(alert2);

        Alert alert3 = new Alert();
        alert.setName("twitter");
        alert.setUrl("http://twitter.com");
        alert.setMethod("GET");
        alert.setPeriod(10L);
        testEntityManager.persist(alert3);
        testEntityManager.flush();
    }

    @Test
    void findAllByOrderByIdAsc() {
        List<Alert> alertList = alertRepository.findAllByOrderByIdAsc();

        assertThat(alertList.get(0).getId(), lessThan(alertList.get(1).getId()));
        assertThat(alertList.get(1).getId(), lessThan(alertList.get(2).getId()));
    }
}