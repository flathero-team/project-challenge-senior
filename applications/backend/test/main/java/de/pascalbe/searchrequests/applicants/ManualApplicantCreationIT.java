package de.pascalbe.searchrequests.applicants;

import com.jayway.jsonpath.JsonPath;
import de.pascalbe.searchrequests.applicants.domain.CreationSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ManualApplicantCreationIT {
    private static final String VALID_REQUEST_BODY = "{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"comment\": \"I am a comment\", \"salutation\": \"MRS\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBeAbleToStoreAndFetchDataAboutManualApplicants() throws Exception {
        var requestResult = mockMvc.perform(post("/applicants")
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.comment").value("I am a comment"))
                .andExpect(jsonPath("$.salutation").value("MRS"));
    }

    @Test
    void shouldMarkApplicantsAsManualApplicants() throws Exception {
        var requestResult = mockMvc.perform(post("/applicants")
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creationSource").value(CreationSource.MANUAL.toString()));
    }

    @Test
    void shouldStoreCreationTimestampForApplicant() throws Exception {
        var requestResult = mockMvc.perform(post("/applicants")
                        .contentType("application/json")
                        .content(VALID_REQUEST_BODY))
                .andExpect(status().isCreated())
                .andReturn();

        var applicantId = JsonPath.read(requestResult.getResponse().getContentAsString(), "$.id");
        assertThat((String) applicantId).isNotBlank();

        var timestampRegex = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{6}Z$";
        mockMvc.perform(get("/applicants/" + applicantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creationTimestamp").isNotEmpty())
                .andExpect(jsonPath("$.creationTimestamp", matchesPattern(timestampRegex)));
    }
}
