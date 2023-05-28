package me.project.service.user;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.address.AddressUpdateDTO;
import me.project.dtos.request.company.CompanyCreateDTO;
import me.project.dtos.request.company.CompanyUpdateDTO;
import me.project.dtos.request.user.*;
import me.project.dtos.response.DictionaryResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.user.CustomerDetailsDTO;
import me.project.dtos.response.user.SimpleCustomerDTO;
import me.project.dtos.response.user.SimpleEmployeeDTO;
import me.project.dtos.response.user.SimpleUserDTO;
import me.project.email.EmailService;
import me.project.email.EmailTemplates;
import me.project.entitiy.Address;
import me.project.entitiy.User;
import me.project.enums.SearchOperation;
import me.project.repository.UserRepository;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.address.IAddressService;
import me.project.service.company.ICompanyService;
import me.project.service.country.ICountryService;
import me.project.service.order.IOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
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
    private final IAddressService addressService;
    private final ICountryService countryService;
    private final IOrderService orderService;

    private final EmailService emailService;
    private final EmailTemplates emailTemplates;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(USER_NOT_FOUND, email))
        );
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id doesn't exist!")
        );
    }

    public User getUser(String email) {
        return userRepository.getByEmail(email);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email " + email + " doesn't exist in database!")
        );
    }

    public CustomerDetailsDTO getCustomerDetails(UUID userId) {
        return CustomerDetailsDTO.convertFromEntity(getUser(userId));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public PageResponse<SimpleUserDTO> getUsers(PageRequestDTO pageRequestDTO) {

        return new PageResponse<>(
                userRepository.findAll(pageRequestDTO.getRequest(User.class))
                        .map(SimpleUserDTO::convertFromEntity)
        );
    }

    public PageResponse<SimpleCustomerDTO> getSimpleCustomers(PageRequestDTO pageRequestDTO, String phrase) {

        Specifications<User> userSpecifications = new Specifications<User>()
                .and(new SearchCriteria("appUserRole", AppUserRole.CLIENT, SearchOperation.EQUAL));

        if (phrase != null)
            userSpecifications
                    .or(new SearchCriteria("firstName", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("lastName", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("email", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("phoneNumber", phrase.trim(), SearchOperation.MATCH));

        return new PageResponse<>(
                userRepository.findAll(userSpecifications, pageRequestDTO.getRequest(User.class))
                        .map(user -> SimpleCustomerDTO.convertFromEntity(
                                user,
                                orderService.getLatestByUser(user) != null ? orderService.getLatestByUser(user).getCreatedOn() : null)
                        )
        );
    }

    public PageResponse<SimpleEmployeeDTO> getSimpleEmployees(PageRequestDTO pageRequestDTO) {

        return new PageResponse<>(
                userRepository.findAllByAppUserRole(AppUserRole.EMPLOYEE, pageRequestDTO.getRequest(User.class))
                        .map(SimpleEmployeeDTO::convertFromEntity)
        );
    }

    public PageResponse<DictionaryResponseDTO> getUsersDictionary(PageRequestDTO pageRequestDTO) {
        return new PageResponse<>(
                userRepository.findAll(pageRequestDTO.getRequest(User.class))
                        .map(user -> new DictionaryResponseDTO(user.getUserId(), user.getUsername()))
        );
    }

    @Transactional
    public User createAppUser(UserCreateDTO createDTO) {
        if (userRepository.existsByEmail(createDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        createDTO.setPassword(bCryptPasswordEncoder.encode(createDTO.getPassword()));
        User user = new User(createDTO, false, true);


        if (createDTO.getAppUserRole().equals(AppUserRole.CLIENT)) {

            boolean createCompany = !createDTO.getCompanyName().isEmpty() && !createDTO.getTaxNumber().isEmpty();
            if (createCompany)
                user.setCompany(companyService.createCompanyIfNotExists(
                        new CompanyCreateDTO(
                                user,
                                createDTO.getCompanyName().trim(),
                                createDTO.getTaxNumber().trim())
                ));

            boolean createAddress = !createDTO.getStreetName().isEmpty() && !createDTO.getPostCode().isEmpty() && !createDTO.getCity().isEmpty();
            if (createAddress)
                user.setAddress(addressService.createAddressIfNotExists(
                        new Address(
                                user,
                                countryService.getCountryById(createDTO.getCountryId()),
                                createDTO.getStreetName(),
                                createDTO.getPostCode(),
                                createDTO.getCity()
                        )
                ));
        }

        userRepository.save(user);

        return user;
    }

    @Transactional(rollbackOn = {SendFailedException.class, MessagingException.class})
    public UUID createCustomer(ClientCreateDTO clientCreateDTO) {
        if (userRepository.existsByEmail(clientCreateDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        User user = new User(clientCreateDTO, true, false);

        boolean createCompanyPresent = clientCreateDTO.getCompanyName() != null && clientCreateDTO.getTaxNumber() != null;
        boolean createCompany = false;

        if (createCompanyPresent)
            createCompany = !clientCreateDTO.getCompanyName().isEmpty() && !clientCreateDTO.getTaxNumber().isEmpty();

        if (createCompanyPresent && createCompany)
            user.setCompany(
                    companyService.createCompanyIfNotExists(
                            new CompanyCreateDTO(
                                    user,
                                    clientCreateDTO.getCompanyName().trim(),
                                    clientCreateDTO.getTaxNumber().trim())
                    ));

        boolean createAddressPresent = clientCreateDTO.getStreetName() != null && clientCreateDTO.getPostCode() != null
                && clientCreateDTO.getCity() != null && clientCreateDTO.getCountryId() != null;
        boolean createAddress = false;

        if (createAddressPresent)
            createAddress = !clientCreateDTO.getStreetName().isEmpty() && !clientCreateDTO.getPostCode().isEmpty() && !clientCreateDTO.getCity().isEmpty();

        userRepository.save(user);

        if (createAddressPresent && createAddress)
            user.setAddress(
                    addressService.createAddressIfNotExists(
                            new Address(
                                    user,
                                    countryService.getCountryById(clientCreateDTO.getCountryId()),
                                    clientCreateDTO.getStreetName(),
                                    clientCreateDTO.getPostCode(),
                                    clientCreateDTO.getCity()
                            )
                    ));

        userRepository.save(user);

        emailService.send(
                user.getEmail(),
                emailTemplates.emailTemplateForWelcome(user),
                "Welcome to Bike Service!"
        );

        return user.getUserId();
    }

    @Transactional(rollbackOn = {SendFailedException.class, MessagingException.class})
    public User registerCustomer(CustomerRegisterDTO customerRegisterDTO) {

        if (userRepository.existsByEmail(customerRegisterDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        User user = new User(customerRegisterDTO, true, false, AppUserRole.CLIENT);

        userRepository.save(user);

        emailService.send(
                user.getEmail(),
                emailTemplates.emailTemplateForWelcome(user),
                "Welcome to Bike Service!"
        );

        return user;
    }

    @Transactional(rollbackOn = {SendFailedException.class, MessagingException.class})
    public User createOAuth2User(String email, String firstName, String lastName, AppUserRole appUserRole) {

        User user = new User(email, firstName, lastName, AppUserRole.CLIENT, true, false);

        userRepository.save(user);

        emailService.send(
                user.getEmail(),
                emailTemplates.emailTemplateForWelcome(user),
                "Welcome to Bike Service!"
        );

        return user;
    }

    @Transactional(rollbackOn = {SendFailedException.class, MessagingException.class})
    public User createEmployee(CustomerRegisterDTO customerRegisterDTO) {

        if (userRepository.existsByEmail(customerRegisterDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already taken!");

        User user = new User(customerRegisterDTO, true, false, AppUserRole.EMPLOYEE);

        userRepository.save(user);

        emailService.send(
                user.getEmail(),
                emailTemplates.emailTemplateForWelcome(user),
                "Welcome to Bike Service Employee Family!"
        );

        return user;
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

        if (user.getAppUserRole().equals(AppUserRole.CLIENT)) {
            companyService.updateCompany(
                    companyService.getCompanyByUser(user).getCompanyId(),
                    new CompanyUpdateDTO(
                            updateDTO.getCompanyName().trim(),
                            updateDTO.getTaxNumber().trim()
                    )
            );


            addressService.updateAddress(
                    addressService.getAddressByUser(user).getAddressId(),
                    new AddressUpdateDTO(
                            updateDTO.getStreetName(),
                            updateDTO.getPostCode(),
                            updateDTO.getCity(),
                            countryService.getCountryById(updateDTO.getCountryId())
                    )

            );

        }

        userRepository.save(user);
    }

    public void activateAndSetPassword(UUID userId, NewPasswordDTO newPasswordDTO) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + userId + " doesn't exist in database!")
        );

        if (!user.getIsPasswordChangeRequired())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with given id " + userId + " already activated account and set password");

        if (!newPasswordDTO.getNewPassword().equals(newPasswordDTO.getNewPasswordConfirm()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "New password confirmation doesn't match new password");

        changeStateOfUser(userId);

        user.setIsPasswordChangeRequired(false);

        user.setPassword(bCryptPasswordEncoder.encode(newPasswordDTO.getNewPassword()));

        userRepository.save(user);

    }

    @Transactional(rollbackOn = {SendFailedException.class, MessagingException.class})
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email " + email + " doesn't exist in database!")
        );

        changeStateOfUser(user.getUserId());

        user.setIsPasswordChangeRequired(true);

        emailService.send(
                user.getEmail(),
                emailTemplates.emailTemplateForResetPassword(user),
                "Password Reset Requested!"
        );

        userRepository.save(user);

    }

    public void changeUserPassword(UUID userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + userId + " doesn't exist in database!")
        );
        String oldPassword = bCryptPasswordEncoder.encode(changePasswordDTO.getOldPassword());

        if (!oldPassword.equals(user.getPassword()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password provided doesn't match actual password, Request Denied");

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getNewPasswordConfirm()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "New password and password confirmation doesn't match");

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDTO.getNewPassword()));

        userRepository.save(user);
    }

    public void changeStateOfUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + userId + " doesn't exist in database!")
        );
        user.setLocked(!user.getLocked());
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    public void deleteUserById(UUID id) {
        userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given id " + id + " doesn't exist in database!")
        );
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
