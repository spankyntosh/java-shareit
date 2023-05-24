package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    UserDto requestBody = UserDto.builder()
            .name("user 1")
            .email("user1@mail.com")
            .build();

    UserDto responseBody = UserDto.builder()
            .id(1)
            .name("user 1")
            .email("user1@mail.com")
            .build();

    @Test
    public void createUser_thenRequest_WhenResponseOk() throws Exception {

        when(userService.createUser(any(UserDto.class)))
                .thenReturn(responseBody);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user 1")))
                .andExpect(jsonPath("$.email", is("user1@mail.com")));

        verify(userService).createUser(any(UserDto.class));

    }

    @Test
    public void createUserEmptyName_thenRequest_WhenException() throws Exception {

        requestBody.setName("");
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(responseBody);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andExpect(status().is(400));

        verify(userService, never()).createUser(any(UserDto.class));
    }

    @Test
    public void createUserInvalidEmail_thenRequest_WhenException() throws Exception {

        requestBody.setEmail("fdaf.adf");
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(responseBody);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andExpect(status().is(400));

        verify(userService, never()).createUser(any(UserDto.class));
    }

    @Test
    public void getUserById_ThenRequest_WhenGetUser() throws Exception {
        when(userService.getUserById(anyInt()))
                .thenReturn(responseBody);

        mvc.perform(get("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) 1)))
                .andExpect(jsonPath("$.name", is(responseBody.getName().toString())))
                .andExpect(jsonPath("$.email", is(requestBody.getEmail().toString())));

    }

    @Test
    public void updateUser_ThenRequest_WhenResponseOk() throws Exception {
        when(userService.updateUser(anyInt(), any(UserDto.class)))
                .thenReturn(responseBody);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUsers_ThenRequest_WhenResponseOk() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(responseBody));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    public void deleteUser_ThenRequest_WhenResponseOk() throws Exception {
        doAnswer(answer -> {
            return null;
        }).when(userService).deleteUser(anyInt());

        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

    }
}
