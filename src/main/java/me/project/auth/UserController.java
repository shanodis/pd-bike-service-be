package me.project.auth;

import me.project.dtos.request.ChangePasswordDTO;
import me.project.dtos.request.UserCreateDTO;
import me.project.dtos.request.UserUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private final IUserService iUserService;

    @GetMapping
    public List<User> getUsers() {
        return iUserService.getUsers();
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable UUID id) {
        return iUserService.getUser(id);
    }

    @PostMapping
    public User createAppUser(UserCreateDTO userCredentials) {
        return iUserService.createAppUser(userCredentials);
    }

    @PutMapping
    public void updateAppUser(UserUpdateDTO userCredentials) {
        iUserService.updateAppUser(userCredentials);
    }

    @PatchMapping("change-user-password")
    public void changeUserPassword(ChangePasswordDTO changePasswordDTO) {
        iUserService.changeUserPassword(changePasswordDTO);
    }

    @PatchMapping("change-user-state")
    public void changeStateOfUser(UUID userId) {
        iUserService.changeStateOfUser(userId);
    }

    @DeleteMapping
    public void deleteUserById(UUID id) {
        iUserService.deleteUserById(id);
    }
}
