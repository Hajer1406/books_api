package com.hajer.livres_proj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import com.hajer.livres_proj.restcontrollers.HealthController;

public class HealthControllerTest {

    @Test
    public void testHealthEndpointReturnsOk() {
        HealthController controller = new HealthController();
        ResponseEntity<Map<String, String>> response = controller.health();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("ok", response.getBody().get("status"));
        assertEquals("books_api", response.getBody().get("service"));
    }
}
