package com.dormmate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ProblemDetailValidationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void validationErrorsContainFieldMessages() throws Exception {
        mockMvc
            .perform(
                post("/debug/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"\",\"expiryDate\":\"2025/13/40\"}"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.errors.name[0]").exists())
            .andExpect(jsonPath("$.errors.expiryDate[0]").exists());
    }
}
