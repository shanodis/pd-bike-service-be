package me.project.auth;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.user.ChangePasswordDTO;
import me.project.dtos.request.user.UserCreateDTO;
import me.project.dtos.request.user.UserUpdateDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.SimpleCustomerDTO;
import me.project.dtos.response.user.SimpleEmployeeDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface IUserService extends UserDetailsService {
    User getUser(UUID id);

    User getUser(String email);

    User findUserByEmail(String email);

    List<User> getUsers();

    PageResponse<User> getUsers(PageRequestDTO pageRequestDTO);

    PageResponse<SimpleCustomerDTO> getSimpleCustomers(PageRequestDTO pageRequestDTO);

    PageResponse<SimpleEmployeeDTO> getSimpleEmployees(PageRequestDTO pageRequestDTO);

    User createAppUser(UserCreateDTO userCredentials);

    void updateAppUser(UUID id, UserUpdateDTO userCredentials);

    void changeUserPassword(UUID userId, ChangePasswordDTO changePasswordDTO);

    void changeStateOfUser(UUID userId);

    void deleteUserById(UUID id);


}
