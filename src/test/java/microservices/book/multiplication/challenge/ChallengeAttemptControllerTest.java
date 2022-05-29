package microservices.book.multiplication.challenge;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import microservices.book.multiplication.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class) // makes sure that our JUnit 5 test loads the extensions for Spring so we can use a test context.
@AutoConfigureJsonTesters   // tells Spring to configure beans of type JacksonTester. we use @Autowired to inject two JacksonTester beans from the test context.
@WebMvcTest(ChallengeAttemptController.class)  // it’ll load only the relevant configuration around the controller: validation, serializers, security, error handlers, etc
public class ChallengeAttemptControllerTest {

    @MockBean
    private ChallengeService challengeService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JacksonTester<ChallengeAttemptDTO> jsonRequestAttempt;
    @Autowired
    private JacksonTester<ChallengeAttempt> jsonResultAttempt;

    @Test
    void postValidResult() throws Exception {
        // given
        User user = new User(1L, "john");
        long attemptId = 5L;
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 70, "john", 3500);
        ChallengeAttempt expectedResponse = new ChallengeAttempt(attemptId, user.getId(), 50, 70, 3500, true);
        given(challengeService
                .verifyAttempt(eq(attemptDTO)))
                .willReturn(expectedResponse);

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/attempts").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttempt.write(attemptDTO).getJson()))
                .andReturn().getResponse();

        // then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttempt.write(
                        expectedResponse
                ).getJson());

    }

    @Test
    void postInvalidResult() throws Exception {
        // given an attempt with invalid input data
        ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(2000, -70, "john", 1);
        // when
        MockHttpServletResponse response = mvc.perform(
                post("/attempts").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttempt.write(attemptDTO).getJson()))
                .andReturn().getResponse();
        // then
        then(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


}
