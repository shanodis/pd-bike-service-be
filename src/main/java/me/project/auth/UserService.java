package me.project.auth;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.email.EmailService;
import me.project.email.EmailTemplates;
import me.project.dtos.request.user.ChangePasswordDTO;
import me.project.dtos.request.user.UserCreateDTO;
import me.project.dtos.request.user.UserUpdateDTO;
import me.project.entitiy.Company;
import me.project.service.company.ICompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final static String USER_NOT_FOUND = "User with email %s not found";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final ICompanyService companyService;

    private final EmailService emailService;
    private final EmailTemplates emailTemplates;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(USER_NOT_FOUND, email))
        );
    }

    @Transactional
    public User createAppUser(UserCreateDTO createDTO) {
        if (userRepository.existsByEmail(createDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        String passwordBeforeEncoding = createDTO.getPassword();

        createDTO.setPassword(bCryptPasswordEncoder.encode(createDTO.getPassword()));
        User user = new User(createDTO, false, true);

        userRepository.save(user);

        //create address

        if(createDTO.getAppUserRole().equals(AppUserRole.CLIENT)) {
            Company company = companyService.createCompanyIfNotExists(new CompanyCreateDTO(
                    createDTO.getCompanyName().trim(),
                    createDTO.getTaxNumber().trim())
            );
            //TODO finish create user/ create UserCompany
        }

//        emailService.send(
//                user.getEmail(),
//                emailTemplates.emailTemplateForWelcome(user.getEmail(), passwordBeforeEncoding),
//                "Welcome!"
//        );

        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id doesn't exist!")
        );
    }

    public User getUser(String email) {
        return userRepository.getByEmail(email);
    }

    @Transactional
    public void updateAppUser(UUID id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(USER_NOT_FOUND, updateDTO.getEmail()))
        );

        if (!user.getEmail().equals(updateDTO.getEmail()))
            if (userRepository.existsByEmail(updateDTO.getEmail()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        user = updateDTO.convertToUser(user);
        //update address

        if(user.getAppUserRole().equals(AppUserRole.CLIENT))
           companyService.updateCompany(
                   updateDTO.getCompanyId(),
                   new CompanyUpdateDTO(updateDTO.getCompanyName().trim(), updateDTO.getTaxNumber().trim())
           );

        userRepository.save(user);
    }

    public void changeUserPassword(UUID userId, ChangePasswordDTO changePasswordDTO){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + userId + " doesn't exist in database!")
        );

        String oldPassword = bCryptPasswordEncoder.encode(changePasswordDTO.getOldPassword());

        if(!oldPassword.equals(user.getPassword()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Password provided doesn't match actual password, Request Denied");

        if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getNewPasswordConfirm()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New password and password confirmation doesn't match");

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDTO.getNewPassword()));

        userRepository.save(user);
    }

    public void deleteUserById(UUID id) {
         userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + id + " doesn't exist in database!")
        );
        userRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email " + email + " doesn't exist in database!")
        );
    }

    public void changeStateOfUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + userId + " doesn't exist in database!")
        );
        user.setLocked(!user.getLocked());
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
}
