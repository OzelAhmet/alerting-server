package yte.intern.alertingserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.models.Response;
import yte.intern.alertingserver.services.AlertService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSchedulerTest {

    @Mock
    AlertService alertService;

    @InjectMocks
    @Spy
    AlertScheduler alertScheduler;

    private List<Alert> alertList;
    @BeforeEach
    void setUp() {
        Set<Response> responseSet= Set.of(
                new Response(3L, 111, null),
                new Response(4L, 112, null)
        );
        alertList = List.of(
                new Alert(1L,
                        "google",
                        "http://google.com",
                        "GET",
                        10L,
                        LocalDateTime.MIN, // connect always
                        new HashSet<>(responseSet)),
                new Alert(2L,
                        "facebook",
                        "http://facebook.com",
                        "POST",
                        44L,
                        LocalDateTime.MIN,
                        new HashSet<>())
                /*
                ,
                new Alert(141L,
                        "twitter",
                        "http://twitter.com",
                        "GET", 11383L,
                        LocalDateTime.MIN,
                        new HashSet<>())
                 */
        );
    }

    @Test
    void testConnectionShouldReturnZeroBecauseOfURL() {
        int respCode = alertScheduler.testConnection("malformedURL", "POST");

        assertEquals(0, respCode);
    }

    @Test
    void testConnectionShouldReturnZeroBecauseOfMethod() {
        int respCode = alertScheduler.testConnection("http://google.com", "NOTAMETHOD");

        assertEquals(0, respCode);
    }

    @Test
    void schedule() {
        Alert alert0 = alertList.get(0);
        Alert alert1 = alertList.get(1);

        // given
        when(alertService.getAllAlerts()).thenReturn(alertList);
        // These response codes must not be inside of alert0 and alert1
        doReturn(200).when(alertScheduler).testConnection(alert0.getUrl(), alert0.getMethod());
        doReturn(301).when(alertScheduler).testConnection(alert1.getUrl(), alert1.getMethod());

        // when
        alertScheduler.schedule();

        // then
        // Get list of old response codes
        List<Integer> alert0res = alert0.getResults().stream().map(Response::getResponseCode).collect(Collectors.toList());
        List<Integer> alert1res = alert1.getResults().stream().map(Response::getResponseCode).collect(Collectors.toList());

        // Check if they contain new response codes
        assertTrue( alert0res.contains(200) );
        assertTrue( alert1res.contains(301) );

        // verify
        verify(alertService, times(1)).getAllAlerts();
    }
}