package org.server.remoteclass.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.server.remoteclass.controller.AuthController;
import org.server.remoteclass.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testA(){
        Assertions.assertThat(5).isEqualTo(5);
    }

    @Test
    @DisplayName("정상적인 Post요청시, 서버에서 상태코드 200을 받는다.")
    public void 회원가입() throws Exception{
        UserDto userDto = new UserDto();
        userDto.setEmail("park1234@naver.com");
        userDto.setPassword("helloworld");
        String json = mapper.writeValueAsString(userDto);
//        String json = mapper.writeValueAsString(new SubmittedUserSolutionDto(1L, 1L, "2", false));
        String URL = "/signup";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(json);
        mockMvc.perform(requestBuilder).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
    }

}
