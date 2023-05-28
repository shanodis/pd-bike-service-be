package me.project.service.user;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.user.*;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.CustomerDetailsDTO;
import me.project.dtos.response.user.SimpleCustomerDTO;
import me.project.dtos.response.user.SimpleEmployeeDTO;
import me.project.dtos.response.user.SimpleUserDTO;
import me.project.entitiy.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface IUserService extends UserDetailsService {
    User getUser(UUID id);

    User getUser(String email);

    User findUserByEmail(String email);

    CustomerDetailsDTO getCustomerDetails(UUID userId);

    List<User> getUsers();

    PageResponse<SimpleUserDTO> getUsers(PageRequestDTO pageRequestDTO);

    PageResponse<SimpleCustomerDTO> getSimpleCustomers(PageRequestDTO pageRequestDTO, String phrase);

    PageResponse<SimpleEmployeeDTO> getSimpleEmployees(PageRequestDTO pageRequestDTO);

    PageResponse<DictionaryResponseDTO> getUsersDictionary(PageRequestDTO pageRequestDTO);

    User createAppUser(UserCreateDTO userCredentials);

    User registerCustomer(CustomerRegisterDTO customerRegisterDTO);

    UUID createCustomer(ClientCreateDTO clientCreateDTO);

    User createOAuth2User(String email, String firstName, String lastName, AppUserRole appUserRole);

    User createEmployee(CustomerRegisterDTO customerRegisterDTO);

    void updateAppUser(UUID id, UserUpdateDTO userCredentials);

    void activateAndSetPassword(UUID userId, NewPasswordDTO newPasswordDTO);

    void resetPassword(String email);

    void changeUserPassword(UUID userId, ChangePasswordDTO changePasswordDTO);

    void changeStateOfUser(UUID userId);

    void deleteUserById(UUID id);

    boolean existsByEmail(String email);
}
