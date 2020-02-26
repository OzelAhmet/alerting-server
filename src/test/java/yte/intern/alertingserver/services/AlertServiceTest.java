package yte.intern.alertingserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.models.Response;
import yte.intern.alertingserver.repositories.AlertRepository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    AlertRepository alertRepository;

    @InjectMocks
    AlertService alertService;


    private List<Alert> alertList;

    @BeforeEach
    void setUp() {
        alertList = List.of(
                new Alert(1L, "google", "http://google,com", "GET", 10L, null, null),
                new Alert(2L, "facebook", "http://facebook,com", "POST", 44L, null, null),
                new Alert(141L, "twitter", "http://twitter,com", "GET", 11383L, null, null)
        );
    }

    @Test
    void getAllAlertsTest() {
        // given(alertRepository.findAllByOrderByIdAsc()).willReturn(List.of(any(Alert.class)));
        when(alertRepository.findAllByOrderByIdAsc()).thenReturn(alertList);

        // when
        List<Alert> returnedAlertList = alertService.getAllAlerts();

        // then
        verify(alertRepository).findAllByOrderByIdAsc();

        assertAll("getAllAlerts Test Cases",
                () -> assertNotNull(returnedAlertList),
                () -> assertEquals(alertList.size(), returnedAlertList.size())
        );

        /*
        // it doesn't make sense to control order here
        assertAll("getAllAlerts Order Test",
                () -> assertEquals(1L, returnedAlertList.get(0).getId().longValue() ),
                () -> assertEquals(2L, returnedAlertList.get(1).getId().longValue() ),
                () -> assertEquals(141L, returnedAlertList.get(2).getId().longValue() )
        );
        */

    }

    @Test
    void getAlertById() {
        final Long TEST_ID = 1L;
        when(alertRepository.findById(TEST_ID)).thenReturn(Optional.of(alertList.get(0)));

        // when
        Alert returnedAlert = alertService.getAlertById(TEST_ID);

        // then
        verify(alertRepository).findById(anyLong());
        assertEquals("google", returnedAlert.getName());
    }

    @Test
    void getAlertByIdShouldThrowException() {
        final Long TEST_ID = 10L;
        when(alertRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // then
        Exception returnedException = assertThrows(EntityNotFoundException.class, () -> {
            // when
            alertService.getAlertById(TEST_ID);
        });
        assertEquals("User not found with ID: "+TEST_ID, returnedException.getMessage());

        // verify
        verify(alertRepository).findById(anyLong());
    }

    @Test
    void addAlertShouldThrowExceptionBecauseOfUrl() {
        Alert alertWithMalformedUrl = new Alert(1L, "google", "google.com", "GET", 10L, null, null);

        Exception returnedException = assertThrows(PersistenceException.class, () -> {
            // when
            alertService.addAlert(alertWithMalformedUrl);
        });
        assertEquals("URL is wrong!", returnedException.getMessage());

        // verify
        verify(alertRepository, never()).save(any(Alert.class));
    }

    // Same above. Just for update method
    @Test
    void updateAlertShouldThrowExceptionBecauseOfUrl() {
        Alert alertWithMalformedUrl = new Alert(1L, "google", "google.com", "GET", 10L, null, null);
        // given
        when(alertRepository.findById(alertWithMalformedUrl.getId())).thenReturn(Optional.of(alertList.get(0)));

        Exception returnedException = assertThrows(PersistenceException.class, () -> {
            // when
            alertService.updateAlert(alertWithMalformedUrl);
        });
        assertEquals("URL is wrong!", returnedException.getMessage());

        // verify
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void updateAlertShouldThrowExceptionBecauseItCouldNotFindAlert() {
        Alert alert = new Alert(1L, "google", "http://google.com", "GET", 10L, null, null);
        //given
        when(alertRepository.findById(alert.getId())).thenReturn(Optional.empty());

        Exception returnedException = assertThrows(EntityNotFoundException.class, () -> {
            // when
            alertService.updateAlert(alert);
        });
        assertEquals("User not found with ID: "+alert.getId(), returnedException.getMessage());

        // verify
        verify(alertRepository, never()).save(any(Alert.class));
    }

    // TODO: control this test
    @Test
    void updateAlert() {
        LocalDateTime time = LocalDateTime.now();
        Set<Response> responseSet = Set.of(new Response(888L, 200, time));
        Alert oldAlert     = new Alert(1L, "google", "http://google.com", "GET", 10L, time, responseSet);
        Alert requestAlert = new Alert(1L, "facebook", "http://facebook.com", "GET", 10L, null, null);
        Alert updatedAlert = new Alert(1L, "facebook", "http://facebook.com", "GET", 10L, time, responseSet);

        // given
        when(alertRepository.findById(requestAlert.getId())).thenReturn(Optional.of(oldAlert));

        // This makes save function return its argument
        // Already, save function should return its argument after save operation
        when(alertRepository.save(any(Alert.class))).then(AdditionalAnswers.returnsFirstArg());

        // when
        Alert returnedAlert = alertService.updateAlert(requestAlert);

        // then
        assertAll("updateAlert Test Cases",
                () -> assertEquals(returnedAlert.getId(), requestAlert.getId()),
                () -> assertEquals(returnedAlert.getId(), updatedAlert.getId()),
                () -> assertEquals(returnedAlert.getName(), updatedAlert.getName()),
                () -> assertEquals(returnedAlert.getUrl(), updatedAlert.getUrl()),
                () -> assertEquals(returnedAlert.getMethod(), updatedAlert.getMethod()),
                () -> assertEquals(returnedAlert.getPeriod(), updatedAlert.getPeriod())
        );

        // verify
        verify(alertRepository, times(1)).save(any(Alert.class));
    }
}