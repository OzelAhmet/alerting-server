package yte.intern.alertingserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import yte.intern.alertingserver.models.Alert;
import yte.intern.alertingserver.models.Response;
import yte.intern.alertingserver.services.AlertService;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    AlertService alertService;

    @InjectMocks
    AlertController alertController;

    private MockMvc mockMvc;

    private List<Alert> alertList;

    @BeforeEach
    void setUp() {
        Set<Response> responseSet= Set.of(
                new Response(3L, 200, null),
                new Response(4L, 404, null)
        );
        alertList = List.of(
                new Alert(1L, "google", "http://google.com", "GET", 10L, null, responseSet),
                new Alert(2L, "facebook", "http://facebook.com", "POST", 44L, null, null),
                new Alert(141L, "twitter", "http://twitter.com", "GET", 11383L, null, null)
        );

        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
    }

    @Test
    void getAllAlerts() throws Exception {
        //given
        when(alertService.getAllAlerts()).thenReturn(alertList);

        mockMvc.perform(get("/alert/all").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("google")))
                .andExpect(jsonPath("$[1].method", is("POST")))
                .andExpect(jsonPath("$[2].id", is(141)));

    }

    @Test
    void getAlert() {
        final Long TEST_ID = 1L;
        Alert alert = alertList.get(0);
        //given
        when(alertService.getAlertById(TEST_ID)).thenReturn(alert);

        assertDoesNotThrow(() -> {
            mockMvc.perform(get("/alert/"+alert.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.id", is(alert.getId().intValue())))
                    .andExpect(jsonPath("$.name", is(alert.getName())))
                    .andExpect(jsonPath("$.url", is(alert.getUrl())))
                    .andExpect(jsonPath("$.method", is(alert.getMethod())))
                    .andExpect(jsonPath("$.period", is(alert.getPeriod().intValue())));
        });

        // verify
        verify(alertService, times(1)).getAlertById(anyLong());
    }

    @Test
    void getAlertThrowException() {
        final Long TEST_ID = 1L;
        //given. Throw exception because entity not found
        when(alertService.getAlertById(TEST_ID)).thenThrow(new EntityNotFoundException("User not found with ID: " + TEST_ID));

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(get("/alert/"+TEST_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        });

        // verify
        verify(alertService, times(1)).getAlertById(anyLong());
    }

    @Test
    void getResultsOfAlert() {
        final Long TEST_ID = 1L;
        Alert alert = alertList.get(0);
        Response[] alertResults = alert.getResults().toArray(Response[]::new);
        //given
        when(alertService.getAlertById(TEST_ID)).thenReturn(alert);

        assertDoesNotThrow(() -> {
            mockMvc.perform(get("/alert/"+alert.getId()+"/results"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andDo(mvcResult -> System.out.println("result list: " + mvcResult.getResponse().getContentAsString()))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(alertResults[0].getId().intValue())))
                    .andExpect(jsonPath("$[1].responseCode", is(alertResults[1].getResponseCode())));
        });

        // verify
        verify(alertService, times(1)).getAlertById(anyLong());
    }

    @Test
    void addAlert() throws JsonProcessingException {
        final Alert alert = alertList.get(1);
        // given
        // when(alertService.addAlert(alert)).thenReturn(alert); // Not use. Error.
        when(alertService.addAlert(any(Alert.class))).thenReturn(alert);

        String alertJson = new ObjectMapper().writeValueAsString(alert);

        assertDoesNotThrow(() -> {
            mockMvc.perform(
                    post("/alert/add").contentType(MediaType.APPLICATION_JSON_UTF8).content(alertJson)
            )
                    .andDo(mvcResult1 -> System.out.println("req: "+mvcResult1.getRequest().getContentAsString()))
                    .andDo(mvcResult1 -> System.out.println("resp: "+mvcResult1.getResponse().getContentAsString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.name", is(alert.getName())))
                    .andExpect(jsonPath("$.url", is(alert.getUrl())))
                    .andExpect(jsonPath("$.method", is(alert.getMethod())))
                    .andExpect(jsonPath("$.period", is(alert.getPeriod().intValue())));
        });

        // verify
        verify(alertService, times(1)).addAlert(any(Alert.class));
    }

    @Test
    void addAlertShouldThrowException() throws JsonProcessingException {
        final Alert alert = alertList.get(1);
        // given
        when(alertService.addAlert(any(Alert.class))).thenThrow(new PersistenceException("URL is wrong!"));

        String alertJson = new ObjectMapper().writeValueAsString(alert);

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    post("/alert/add").contentType(MediaType.APPLICATION_JSON_UTF8).content(alertJson)
            )
                    .andDo(print())
                    .andDo(mvcResult1 -> System.out.println("req: "+mvcResult1.getRequest().getContentAsString()))
                    .andDo(mvcResult1 -> System.out.println("resp: "+mvcResult1.getResponse().getContentAsString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        });

        // verify
        verify(alertService, times(1)).addAlert(any(Alert.class));
    }

    @Test
    void updateAlert() throws JsonProcessingException {
        final Alert alert = alertList.get(1);
        // given
        when(alertService.updateAlert(any(Alert.class))).thenReturn(alert);

        String alertJson = new ObjectMapper().writeValueAsString(alert);

        assertDoesNotThrow(() -> {
            mockMvc.perform(
                    put("/alert/update").contentType(MediaType.APPLICATION_JSON_UTF8).content(alertJson)
            )
                    .andDo(mvcResult1 -> System.out.println("req: "+mvcResult1.getRequest().getContentAsString()))
                    .andDo(mvcResult1 -> System.out.println("resp: "+mvcResult1.getResponse().getContentAsString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.name", is(alert.getName())))
                    .andExpect(jsonPath("$.url", is(alert.getUrl())))
                    .andExpect(jsonPath("$.method", is(alert.getMethod())))
                    .andExpect(jsonPath("$.period", is(alert.getPeriod().intValue())));
        });

        // verify
        verify(alertService, times(1)).updateAlert(any(Alert.class));
    }

    @Test
    void deleteAlert() {
        final Long TEST_ID = 1L;

        assertDoesNotThrow(() -> {
            mockMvc.perform(delete("/alert/delete/"+TEST_ID))
                    .andDo(print())
                    .andExpect(status().isOk());
        });

        // verify
        verify(alertService, times(1)).deleteAlertById(anyLong());
    }
}