package com.dormmate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dormmate.api.debug.DebugValidationController;
import com.dormmate.global.error.GlobalExceptionHandler;

@WebMvcTest(DebugValidationController.class)
@Import(GlobalExceptionHandler.class)
class ProblemDetailValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validationErrorsContainFieldMessages() throws Exception {
        mockMvc
            .perform(
                post("/debug/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"\",\"expiryDate\":\"2025/13/40\"}"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.errors.name[0]").exists())
            .andExpect(jsonPath("$.errors.expiryDate[0]").exists());
    }
}
