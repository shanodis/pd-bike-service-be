package me.project.auth;

import me.project.dtos.request.ChangePasswordDTO;
import me.project.dtos.request.UserCreateDTO;
import me.project.dtos.request.UserUpdateDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface IUserService extends UserDetailsService {
    User createAppUser(UserCreateDTO userCredentials);

    List<User> getUsers();

    User getUser(UUID id);

    User getUser(String email);

    void updateAppUser(UUID id, UserUpdateDTO userCredentials);

    void changeUserPassword(ChangePasswordDTO changePasswordDTO);

    User getUserByEmail(String email);

    void changeStateOfUser(UUID userId);

    void deleteUserById(UUID id);
}
