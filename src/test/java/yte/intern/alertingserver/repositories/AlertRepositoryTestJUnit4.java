package yte.intern.alertingserver.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import yte.intern.alertingserver.models.Alert;

import javax.persistence.PersistenceException;

import java.util.List;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;


/**
 * This is written with JUnit4.
 * Junit5 version is written.
 * Not needed to be run.
 */
@Ignore
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlertRepositoryTestJUnit4 {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AlertRepository alertRepository;

    Alert alert;

    @Before
    public void setUp() throws Exception {
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

    @Ignore
    @Test
    public void whenFindById_thenReturnAlert(){

        Alert found = alertRepository.findById(alert.getId()).orElseThrow(PersistenceException::new);

        System.out.println("id "+alert.getId());
        assertEquals(alert.getName(), found.getName());
    }


    @Test
    public void findAllByOrderByIdAsc() {

        List<Alert> alertList = alertRepository.findAllByOrderByIdAsc();

        System.out.println(alertList);

        assertThat(alertList.get(0).getId(), lessThan(alertList.get(1).getId()));
        assertThat(alertList.get(1).getId(), lessThan(alertList.get(2).getId()));
    }
}