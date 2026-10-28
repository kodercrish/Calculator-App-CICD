package com.example.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests: starts the full Spring app and calls the real endpoints through MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ---------- successful operations ----------

    @Test
    void addReturnsResult() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.ADD).param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2.0))
                .andExpect(jsonPath("$.b").value(3.0))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    void subtractReturnsResult() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.SUBTRACT).param("a", "10").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").value(6.0));
    }

    @Test
    void multiplyReturnsResult() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.MULTIPLY).param("a", "3").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").value(12.0));
    }

    @Test
    void divideReturnsResult() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.DIVIDE).param("a", "10").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").value(2.5));
    }

    // ---------- error cases (HTTP 400) ----------

    @Test
    void divideByZeroReturnsBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.DIVIDE).param("a", "5").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Division by zero is not allowed"));
    }

    @Test
    void missingParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.ADD).param("a", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required parameter 'b'"));
    }

    @Test
    void invalidNumberReturnsBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE + ApiEndpoints.ADD).param("a", "abc").param("b", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'a' must be a valid number, got 'abc'"));
    }

    // ---------- health check ----------

    @Test
    void healthEndpointIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
