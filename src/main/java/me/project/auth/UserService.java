package me.project.auth;

import me.project.email.EmailService;
import me.project.email.EmailTemplates;
import me.project.dtos.request.ChangePasswordDTO;
import me.project.dtos.request.UserCreateDTO;
import me.project.dtos.request.UserUpdateDTO;
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

    private final EmailService emailService;
    private final EmailTemplates emailTemplates;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email))
        );
    }

    public User createAppUser(UserCreateDTO userCredentials) {
        if (userRepository.existsByEmail(userCredentials.getEmail())) {
            throw new IllegalStateException("This email is already taken!");
        }

        userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));

        User user = new User(userCredentials, false, true);

        //create address

        //if(!userCredentials.getIsEmployee())
            //create company

        //TODO finish create user
        userRepository.save(user);
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
    public void updateAppUser(UUID id, UserUpdateDTO userCredentials) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, userCredentials.getEmail()))
        );

        if (!user.getEmail().equals(userCredentials.getEmail()))
            if (userRepository.existsByEmail(userCredentials.getEmail())) {
                throw new IllegalStateException("This email is already taken!");
            }


        user = userCredentials.overrideToUser(user);
        //update address

        //if(!userCredentials.getIsEmployee())
            //update company

        //TODO finish Update
        userRepository.save(user);
    }

    public void changeUserPassword(ChangePasswordDTO changePasswordDTO){
        User user = userRepository.findById(changePasswordDTO.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException("User with given id " + changePasswordDTO.getUserId() + " doesn't exist in database!")
        );

        String oldPassword = bCryptPasswordEncoder.encode(changePasswordDTO.getOldPassword());

        if(!oldPassword.equals(user.getPassword()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Password provided doesn't match actual password, Request Denied");

        if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getNewPasswordConfirm()))
            throw new IllegalStateException("New password and password confirmation doesn't match");

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
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email " + email + " doesn't exist in database!")
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
