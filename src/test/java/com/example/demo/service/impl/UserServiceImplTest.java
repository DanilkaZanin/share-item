package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserMapperImpl;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Spy
    UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    UserServiceImpl userService;


    /*@Test
    void createUserTest() {
        UserDto userCreateRequest = new UserDto("login", "password");
        var repositoryUser = userMapper.toUser(userCreateRequest);

        when(userRepository.existsByLogin("login"))
                .thenReturn(Boolean.FALSE);

        when(userRepository.save(any(User.class)))
                .thenReturn(repositoryUser);

        UserDto userResponse = userService.createUser(userCreateRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).existsByLogin("login");

        assertNotNull(userResponse);
        assertEquals(UserDto.class, userResponse.getClass());
        assertEquals("login", userResponse.login());
        assertEquals("password", userResponse.password());
    }

    @Test
    void createUserExistingLoginExceptionTest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest("login", "password");

        when(userRepository.existsByLogin("login"))
                .thenReturn(Boolean.TRUE);

        assertThrows(ExistsLoginException.class, () -> userService.createUser(userCreateRequest));
        verify(userRepository, times(1)).existsByLogin("login");
    }*/
}
