package me.project.service.user;

import me.project.auth.enums.AppUserRole;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.address.AddressCreateDTO;
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
import me.project.entitiy.*;
import me.project.repository.UserRepository;
import me.project.search.specificator.Specifications;
import me.project.service.address.IAddressService;
import me.project.service.company.ICompanyService;
import me.project.service.country.ICountryService;
import me.project.service.files.IFileService;
import me.project.service.order.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ICompanyService companyService;

    @Mock
    private IAddressService addressService;

    @Mock
    private ICountryService countryService;

    @Mock
    private IOrderService orderService;

    @Mock
    private IFileService fileService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTemplates emailTemplates;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setLocked(false);
        user.setEnabled(true);
        user.setAppUserRole(AppUserRole.CLIENT);
        user.setEmail("email@email.com");
    }


    @Test
    @DisplayName("Should throw UsernameNotFoundException when the email does not exist in the repository")
    void loadUserByUsernameWhenEmailDoesNotExist() {
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.loadUserByUsername(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return UserDetails when the email exists in the repository")
    void loadUserByUsernameWhenEmailExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);

        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    @DisplayName("Should return the user when the id exists")
    void getUserWhenIdExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw an exception when the id does not exist")
    void getUserWhenIdDoesNotExistThenThrowException() {
        UUID nonExistingId = UUID.randomUUID();
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUser(nonExistingId));
        verify(userRepository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Should return null when the email is not found")
    void getUserWhenEmailNotFound() {
        when(userRepository.getByEmail(anyString())).thenReturn(null);

        User result = userService.getUser("nonexistingemail@example.com");

        assertNull(result);
        verify(userRepository, times(1)).getByEmail(anyString());
    }

    @Test
    @DisplayName("Should return the user with the given email")
    void getUserWithGivenEmail() {// Mocking the userRepository's getByEmail method to return the user object
        when(userRepository.getByEmail(anyString())).thenReturn(user);

        // Calling the getUser method with the email of the user object
        User returnedUser = userService.getUser(user.getEmail());

        // Verifying that the userRepository's getByEmail method was called once with the email of the user object
        verify(userRepository, times(1)).getByEmail(user.getEmail());

        // Asserting that the returned user object is equal to the user object
        assertEquals(user, returnedUser);
    }

    @Test
    @DisplayName("Should return null when the user does not have an avatar")
    void getUserAvatarWhenUserDoesNotHaveAvatar() {// Mock the getUser method to return the user without an avatar
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Call the getUserAvatar method
        String avatarUrl = userService.getUserAvatar(user.getUserId());

        // Assert that the returned value is null
        assertNull(avatarUrl);

        // Verify that the getUser method was called once with the correct argument
        verify(userRepository, times(1)).findById(user.getUserId());
    }

    @Test
    @DisplayName("Should return the user avatar URL when the user has an avatar")
    void getUserAvatarWhenUserHasAvatar() {
        UUID userId = UUID.randomUUID();
        File avatar = new File(UUID.randomUUID(), "avatar.jpg");
        User user = new User();
        user.setUserId(userId);
        user.setAvatar(avatar);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileService.getFileUrl(avatar.getFileId())).thenReturn("http://example.com/avatar.jpg");

        String avatarUrl = userService.getUserAvatar(userId);

        assertEquals("http://example.com/avatar.jpg", avatarUrl);
        verify(userRepository, times(1)).findById(userId);
        verify(fileService, times(1)).getFileUrl(avatar.getFileId());
    }

    @Test
    @DisplayName("Should throw an exception when the email does not exist in the database")
    void findUserByEmailWhenEmailDoesNotExistThenThrowException() {
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findUserByEmail(email));
    }

    @Test
    @DisplayName("Should return the user when the email exists in the database")
    void findUserByEmailWhenEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(user, foundUser);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should return null when the email does not exist")
    void findUserByEmailSilentWhenEmailDoesNotExist() {
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        User result = userService.findUserByEmailSilent(email);

        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should return the user when the email exists")
    void findUserByEmailSilentWhenEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        User result = userService.findUserByEmailSilent(user.getEmail());

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw an exception when user ID is not found")
    void getCustomerDetailsWhenUserIdNotFoundThenThrowException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getCustomerDetails(userId));
    }

    @Test
    @DisplayName("Should return customer details for the given user ID")
    void getCustomerDetailsForGivenUserId() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.ofNullable(user));

        CustomerDetailsDTO expectedCustomerDetailsDTO = new CustomerDetailsDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumberPrefix(),
                user.getPhoneNumber(),
                user.getNote(),
                user.getEmail(),
                user.getIsUsing2FA()
        );

        CustomerDetailsDTO actualCustomerDetailsDTO = userService.getCustomerDetails(user.getUserId());

        assertEquals(expectedCustomerDetailsDTO, actualCustomerDetailsDTO);

        verify(userRepository, times(1)).findById(user.getUserId());
    }

    @Test
    @DisplayName("Should return a list of all users")
    void getUsersReturnsAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> returnedUsers = userService.getUsers();

        verify(userRepository, times(1)).findAll();

        assertTrue(returnedUsers.contains(user));
    }

    @Test
    @DisplayName("Should throw an exception when getUsers is called with a null PageRequestDTO")
    void getUsersWithNullPageRequestDTOThrowsException() {
        assertThrows(NullPointerException.class, () -> userService.getUsers(null));
    }

    @Test
    @DisplayName("Should return an empty page when getUsers is called with a PageRequestDTO with no users")
    void getUsersWithEmptyPageRequestDTO() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(1);
        pageRequestDTO.setPageLimit(10);
        pageRequestDTO.setSortBy("email");
        pageRequestDTO.setSortDir("asc");
        when(userRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        PageResponse<SimpleUserDTO> result = userService.getUsers(pageRequestDTO);

        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should return a page of users when getUsers is called with a valid PageRequestDTO")
    void getUsersWithValidPageRequestDTO() {// Create a mock PageRequestDTO
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(1);
        pageRequestDTO.setPageLimit(10);
        pageRequestDTO.setSortBy("email");
        pageRequestDTO.setSortDir("asc");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        PageResponse<SimpleUserDTO> result = userService.getUsers(pageRequestDTO);

        verify(userRepository, times(1)).findAll(any(Pageable.class));

        assertEquals(1, result.getContent().size());
        SimpleUserDTO simpleUserDTO = result.getContent().get(0);
        assertEquals(user.getUserId(), simpleUserDTO.getUserId());
        assertEquals(user.getEmail(), simpleUserDTO.getEmail());
        assertEquals(user.getFirstName(), simpleUserDTO.getFirstName());
        assertEquals(user.getLastName(), simpleUserDTO.getLastName());
        assertEquals(user.getPhoneNumberPrefix(), simpleUserDTO.getPhoneNumberPrefix());
        assertEquals(user.getPhoneNumber(), simpleUserDTO.getPhoneNumber());
        assertEquals(user.getAppUserRole(), simpleUserDTO.getAppUserRole());
    }

    @Test
    @DisplayName("Should return an empty page when no customers match the provided phrase")
    void getSimpleCustomersWhenNoCustomersMatchPhrase() {// Create a PageRequestDTO object with page number 0 and page size 10
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(1);
        pageRequestDTO.setPageLimit(10);
        pageRequestDTO.setSortBy("email");
        pageRequestDTO.setSortDir("asc");

        when(userRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(Page.empty());

        PageResponse<SimpleCustomerDTO> result = userService.getSimpleCustomers(pageRequestDTO, "non-existing-customer");

        verify(userRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));

        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should return a page of simple customers when phrase is not provided")
    void getSimpleCustomersWhenPhraseNotProvided() {// Create a mock PageRequestDTO object
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(1);
        pageRequestDTO.setPageLimit(10);
        pageRequestDTO.setSortBy("email");
        pageRequestDTO.setSortDir("asc");

        List<User> customerList = new ArrayList<>();
        customerList.add(user);
        Page<User> customerPage = new PageImpl<>(customerList);

        when(userRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(customerPage);

        PageResponse<SimpleCustomerDTO> result = userService.getSimpleCustomers(pageRequestDTO, null);

        verify(userRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    @Test
    @DisplayName("Should return a page of simple customers filtered by the provided phrase")
    void getSimpleCustomersWhenPhraseProvided() {// Create a mock PageRequestDTO object
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(1);
        pageRequestDTO.setPageLimit(10);
        pageRequestDTO.setSortBy("email");
        pageRequestDTO.setSortDir("asc");

        List<User> customers = new ArrayList<>();
        customers.add(user);
        Page<User> page = new PageImpl<>(customers);

        when(userRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(page);

        PageResponse<SimpleCustomerDTO> result = userService.getSimpleCustomers(pageRequestDTO, "John");

        verify(userRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    @Test
    @DisplayName("Should return an empty page of employee dictionary when no statuses are available")
    void getSimpleEmployeesWhenNoUsersAreAvailable() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "email");
        user.setAppUserRole(AppUserRole.EMPLOYEE);
        when(userRepository.findAllByAppUserRole(user.getAppUserRole(), pageRequestDTO.getRequest(User.class))).thenReturn(Page.empty());

        PageResponse<SimpleEmployeeDTO> pageResponse = userService.getSimpleEmployees(pageRequestDTO);

        assertEquals(0, pageResponse.getContent().size());
        assertEquals(1, pageResponse.getCurrentPage());
        assertEquals(1, pageResponse.getTotalPages());
        verify(userRepository, times(1)).findAllByAppUserRole(user.getAppUserRole(), pageRequestDTO.getRequest(User.class));
    }

    @Test
    @DisplayName("Should return a page of employee dictionary when valid page request is provided")
    void getSimpleEmployeesWhenValidPageRequestIsProvided() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "email");
        user.setAppUserRole(AppUserRole.EMPLOYEE);
        when(userRepository.findAllByAppUserRole(user.getAppUserRole(), pageRequestDTO.getRequest(User.class))).thenReturn(
                new PageImpl<>(Collections.singletonList(user))
        );

        PageResponse<SimpleEmployeeDTO> result = userService.getSimpleEmployees(pageRequestDTO);

        assertEquals(1, result.getContent().size());
        assertEquals(user.getUserId(), result.getContent().get(0).getUserId());
        assertEquals(user.getEmail(), result.getContent().get(0).getEmail());
        verify(userRepository, times(1)).findAllByAppUserRole(user.getAppUserRole(), pageRequestDTO.getRequest(User.class));
    }

    @Test
    @DisplayName("Should return an empty page of user dictionary when no statuses are available")
    void getAllUsersDictionaryWhenNoUsersAreAvailable() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "email");
        when(userRepository.findAll(pageRequestDTO.getRequest(User.class))).thenReturn(Page.empty());

        PageResponse<DictionaryResponseDTO> pageResponse = userService.getUsersDictionary(pageRequestDTO);

        assertEquals(0, pageResponse.getContent().size());
        assertEquals(1, pageResponse.getCurrentPage());
        assertEquals(1, pageResponse.getTotalPages());
        verify(userRepository, times(1)).findAll(pageRequestDTO.getRequest(User.class));
    }

    @Test
    @DisplayName("Should return a page of user dictionary when valid page request is provided")
    void getAllUsersDictionaryWhenValidPageRequestIsProvided() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "email");
        when(userRepository.findAll(pageRequestDTO.getRequest(User.class))).thenReturn(
                new PageImpl<>(Collections.singletonList(user))
        );

        PageResponse<DictionaryResponseDTO> result = userService.getUsersDictionary(pageRequestDTO);

        assertEquals(1, result.getContent().size());
        assertEquals(user.getUserId(), result.getContent().get(0).getId());
        assertEquals(user.getEmail(), result.getContent().get(0).getName());
        verify(userRepository, times(1)).findAll(pageRequestDTO.getRequest(User.class));
    }

    @Test
    @DisplayName("Should create app user with company and address when user role is client and company and address data is present")
    void testCreateAppUserWithCompanyAndAddressWhenUserRoleIsClientAndDataIsPresent() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.CLIENT);
        createDTO.setCompanyName("ACME Inc.");
        createDTO.setTaxNumber("1234567890");
        createDTO.setCountryId(UUID.randomUUID());
        createDTO.setPassword("password");
        createDTO.setNote("password");
        createDTO.setPhoneNumber("666666666");
        createDTO.setPhoneNumberPrefix("+48");
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("1234");
        createDTO.setCity("City");

        User user = new User(createDTO, false, true);
        Company company = new Company(UUID.randomUUID(), "ACME Inc.", "1234567890", user);
        Address address = new Address();
        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(createDTO.getPassword())).thenReturn("encodedPassword");
        when(companyService.createCompanyIfNotExists(any(CompanyCreateDTO.class))).thenReturn(company);
        when(countryService.getCountryById(createDTO.getCountryId())).thenReturn(address.getCountry());
        when(addressService.createAddressIfNotExists(any(AddressCreateDTO.class))).thenReturn(address);

        // call the method being tested
        User result = userService.createAppUser(createDTO);

        // assert that the user was created with the expected data
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AppUserRole.CLIENT, result.getAppUserRole());
        assertFalse(result.getIsPasswordChangeRequired());
        assertEquals(company, result.getCompany());
        assertEquals(address, result.getAddress());
        verify(userRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should create app user without company and address when user role is not client")
    void testCreateAppUserWithoutCompanyAndAddressWhenUserRoleIsNotClient() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.ADMIN);
        createDTO.setCompanyName("");
        createDTO.setTaxNumber("");
        createDTO.setCountryId(UUID.randomUUID());
        createDTO.setPassword("password");
        createDTO.setNote("password");
        createDTO.setPhoneNumber("666666666");
        createDTO.setPhoneNumberPrefix("+48");
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("1234");
        createDTO.setCity("City");

        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(createDTO.getPassword())).thenReturn("encodedPassword");

        // call the method being tested
        User result = userService.createAppUser(createDTO);

        // assert that the user was created with the expected data
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AppUserRole.ADMIN, result.getAppUserRole());
        assertFalse(result.getIsPasswordChangeRequired());
        assertNull(result.getCompany());
        assertNull(result.getAddress());
        verify(userRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should throw exception when email is already taken")
    void testCreateAppUserShouldThrowExceptionWhenEmailIsAlreadyTaken() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.CLIENT);
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("12345");
        createDTO.setCity("City");
        createDTO.setCompanyName("ACME Inc.");
        createDTO.setTaxNumber("1234567890");
        createDTO.setCountryId(UUID.randomUUID());

        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(true);

        // call the method being tested and assert that it throws an exception
        assertThrows(ResponseStatusException.class, () -> userService.createAppUser(createDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create app user without company and address when company and address data is not present")
    void testCreateAppUserWithoutCompanyAndAddressWhenDataIsNotPresent() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.CLIENT);
        createDTO.setCompanyName("");
        createDTO.setTaxNumber("");
        createDTO.setCountryId(UUID.randomUUID());
        createDTO.setPassword("password");
        createDTO.setNote("password");
        createDTO.setPhoneNumber("666666666");
        createDTO.setPhoneNumberPrefix("+48");
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("1234");
        createDTO.setCity("City");

        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(createDTO.getPassword())).thenReturn("encodedPassword");

        // call the method being tested
        User result = userService.createAppUser(createDTO);

        // assert that the user was created with the expected data
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AppUserRole.CLIENT, result.getAppUserRole());
        assertFalse(result.getIsPasswordChangeRequired());
        assertNull(result.getCompany());
        assertNull(result.getAddress());
        verify(userRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should create app user with company and without address when address data is not present")
    void testCreateAppUserWithCompanyAndWithoutAddressWhenAddressDataIsNotPresent() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.CLIENT);
        createDTO.setCompanyName("ACME Inc.");
        createDTO.setTaxNumber("1234567890");
        createDTO.setCountryId(UUID.randomUUID());
        createDTO.setPassword("password");
        createDTO.setNote("password");
        createDTO.setPhoneNumber("666666666");
        createDTO.setPhoneNumberPrefix("+48");
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("1234");
        createDTO.setCity("City");

        User user = new User(createDTO, false, true);
        Company company = new Company(UUID.randomUUID(), "ACME Inc.", "1234567890", user);
        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(createDTO.getPassword())).thenReturn("encodedPassword");
        when(companyService.createCompanyIfNotExists(any(CompanyCreateDTO.class))).thenReturn(company);

        // call the method being tested
        User result = userService.createAppUser(createDTO);

        // assert that the user was created with the expected data
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AppUserRole.CLIENT, result.getAppUserRole());
        assertFalse(result.getIsPasswordChangeRequired());
        assertEquals(company, result.getCompany());
        assertNull(result.getAddress());
        verify(userRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should create app user with address and without company when company data is not present")
    void testCreateAppUserWithAddressAndWithoutCompanyWhenCompanyDataIsNotPresent() {
        // create test data
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setEmail("johndoe@example.com");
        createDTO.setAppUserRole(AppUserRole.CLIENT);
        createDTO.setCompanyName("");
        createDTO.setTaxNumber("");
        createDTO.setCountryId(UUID.randomUUID());
        createDTO.setPassword("password");
        createDTO.setNote("password");
        createDTO.setPhoneNumber("666666666");
        createDTO.setPhoneNumberPrefix("+48");
        createDTO.setStreetName("Main St.");
        createDTO.setPostCode("1234");
        createDTO.setCity("City");

        Address address = new Address();
        when(userRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(createDTO.getPassword())).thenReturn("encodedPassword");
        when(countryService.getCountryById(createDTO.getCountryId())).thenReturn(address.getCountry());
        when(addressService.createAddressIfNotExists(any(AddressCreateDTO.class))).thenReturn(address);

        // call the method being tested
        User result = userService.createAppUser(createDTO);

        // assert that the user was created with the expected data
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AppUserRole.CLIENT, result.getAppUserRole());
        assertFalse(result.getIsPasswordChangeRequired());
        assertNull(result.getCompany());
        assertEquals(address, result.getAddress());
        verify(userRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should create customer with user only")
    void testCreateCustomerWithUserOnly() {
        // create test data
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setCountryId(UUID.randomUUID());
        clientCreateDTO.setFirstName("John");
        clientCreateDTO.setLastName("Doe");
        clientCreateDTO.setEmail("johndoe@example.com");
        clientCreateDTO.setPhoneNumber("666666666");
        clientCreateDTO.setPhoneNumberPrefix("+48");

        when(userRepository.existsByEmail(clientCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(UUID.randomUUID());
            return user;
        });

        // call the method being tested
        UUID userId = userService.createCustomer(clientCreateDTO);

        // assert that the user was created and saved
        assertNotNull(userId);
        verify(userRepository, times(2)).save(any(User.class));
        verify(emailService, times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("Should create customer with user and company")
    void testCreateCustomerWithUserAndCompany() {
        // create test data
        UUID companyId = UUID.randomUUID();
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setCountryId(UUID.randomUUID());
        clientCreateDTO.setFirstName("John");
        clientCreateDTO.setLastName("Doe");
        clientCreateDTO.setEmail("johndoe@example.com");
        clientCreateDTO.setCompanyName("ACME Inc.");
        clientCreateDTO.setTaxNumber("1234567890");
        clientCreateDTO.setPhoneNumber("666666666");
        clientCreateDTO.setPhoneNumberPrefix("+48");

        when(userRepository.existsByEmail(clientCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(UUID.randomUUID());
            return user;
        });
        when(companyService.createCompanyIfNotExists(any(CompanyCreateDTO.class))).thenReturn(new Company(companyId, "ACME Inc.", "1234567890", null));

        // call the method being tested
        UUID userId = userService.createCustomer(clientCreateDTO);

        // assert that the user and company were created and saved
        assertNotNull(userId);
        verify(userRepository, times(2)).save(any(User.class));
        verify(companyService, times(1)).createCompanyIfNotExists(any(CompanyCreateDTO.class));
        verify(emailService, times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("Should create customer with user and address")
    void testCreateCustomerWithUserAndAddress() {
        // create test data
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setCountryId(UUID.randomUUID());
        clientCreateDTO.setFirstName("John");
        clientCreateDTO.setLastName("Doe");
        clientCreateDTO.setEmail("johndoe@example.com");
        clientCreateDTO.setStreetName("Main St.");
        clientCreateDTO.setPostCode("12345");
        clientCreateDTO.setCity("City");
        clientCreateDTO.setPhoneNumber("666666666");
        clientCreateDTO.setPhoneNumberPrefix("+48");

        when(userRepository.existsByEmail(clientCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(UUID.randomUUID());
            return user;
        });
        when(addressService.createAddressIfNotExists(any(AddressCreateDTO.class))).thenReturn(new Address(user, new Country(), "Main St.", "123123", "City"));

        // call the method being tested
        UUID userId = userService.createCustomer(clientCreateDTO);

        // assert that the user and address were created and saved
        assertNotNull(userId);
        verify(userRepository, times(2)).save(any(User.class));
        verify(addressService, times(1)).createAddressIfNotExists(any(AddressCreateDTO.class));
        verify(emailService, times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("Should create customer with user, company and address")
    void testCreateCustomerWithUserCompanyAndAddress() {
        // create test data
        UUID companyId = UUID.randomUUID();
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setCountryId(UUID.randomUUID());
        clientCreateDTO.setFirstName("John");
        clientCreateDTO.setLastName("Doe");
        clientCreateDTO.setEmail("johndoe@example.com");
        clientCreateDTO.setCompanyName("ACME Inc.");
        clientCreateDTO.setTaxNumber("1234567890");
        clientCreateDTO.setStreetName("Main St.");
        clientCreateDTO.setPostCode("12345");
        clientCreateDTO.setCity("City");
        clientCreateDTO.setPhoneNumber("666666666");
        clientCreateDTO.setPhoneNumberPrefix("+48");

        when(userRepository.existsByEmail(clientCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(UUID.randomUUID());
            return user;
        });
        when(companyService.createCompanyIfNotExists(any(CompanyCreateDTO.class))).thenReturn(new Company(companyId, "ACME Inc.", "1234567890", null));
        when(addressService.createAddressIfNotExists(any(AddressCreateDTO.class))).thenReturn(new Address(user, new Country(), "Main St.", "123", "City"));

        // call the method being tested
        UUID userId = userService.createCustomer(clientCreateDTO);

        // assert that the user, company and address were created and saved
        assertNotNull(userId);
        verify(userRepository, times(2)).save(any(User.class));
        verify(companyService, times(1)).createCompanyIfNotExists(any(CompanyCreateDTO.class));
        verify(addressService, times(1)).createAddressIfNotExists(any(AddressCreateDTO.class));
        verify(emailService, times(1)).send(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw CONFLICT when email is already taken")
    void testCreateCustomerWhenEmailIsAlreadyTaken() {
        // create test data
        ClientCreateDTO clientCreateDTO = new ClientCreateDTO();
        clientCreateDTO.setFirstName("John");
        clientCreateDTO.setLastName("Doe");
        clientCreateDTO.setEmail("mail@example.com");

        when(userRepository.existsByEmail(clientCreateDTO.getEmail())).thenReturn(true);

        // call the method being tested and assert that it throws CONFLICT
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.createCustomer(clientCreateDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This email is already taken!", exception.getReason());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw a conflict exception when registering a customer with an already taken email")
    void registerCustomerWithTakenEmailThenThrowConflictException() {
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO(
                user.getEmail(),
                "John",
                "Doe",
                "+1",
                "1234567890",
                "Note"
        );

        when(userRepository.existsByEmail(customerRegisterDTO.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.registerCustomer(customerRegisterDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This email is already taken!", exception.getReason());
        verify(userRepository, times(1)).existsByEmail(customerRegisterDTO.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should register a new customer with unique email and send a welcome email")
    void registerCustomerWithUniqueEmailAndSendWelcomeEmail() {// Create a new CustomerRegisterDTO object
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO(
                "newemail@example.com",
                "John",
                "Doe",
                "+1",
                "1234567890",
                "Note"
        );

        // Mock the behavior of the userRepository.existsByEmail method to return false
        when(userRepository.existsByEmail(customerRegisterDTO.getEmail())).thenReturn(false);

        // Mock the behavior of the userRepository.save method to return the user object
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Mock the behavior of the emailTemplates.emailTemplateForWelcome method to return a string
        when(emailTemplates.emailTemplateForWelcome(any(User.class))).thenReturn("Welcome to Bike Service!");

        // Call the registerCustomer method with the customerRegisterDTO object
        User registeredUser = userService.registerCustomer(customerRegisterDTO);

        // Verify that the userRepository.existsByEmail method was called once with the email from the customerRegisterDTO object
        verify(userRepository, times(1)).existsByEmail(customerRegisterDTO.getEmail());

        // Verify that the userRepository.save method was called once with any User object
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that the emailService.send method was called once with the email, email template and subject
        verify(emailService, times(1)).send(
                customerRegisterDTO.getEmail(),
                emailTemplates.emailTemplateForWelcome(registeredUser),
                "Welcome to Bike Service!"
        );

        // Assert that the registeredUser object is not null
        assertNotNull(registeredUser);

        // Assert that the registeredUser object has the same email as the customerRegisterDTO object
        assertEquals(customerRegisterDTO.getEmail(), registeredUser.getEmail());

        // Assert that the registeredUser object has the same first name as the customerRegisterDTO object
        assertEquals(customerRegisterDTO.getFirstName(), registeredUser.getFirstName());

        // Assert that the registeredUser object has the same last name as the customerRegisterDTO object
        assertEquals(customerRegisterDTO.getLastName(), registeredUser.getLastName());

        // Assert that the registeredUser object has the same phone number prefix as the customerRegisterDTO object
        assertEquals(customerRegisterDTO.getPhoneNumberPrefix(), registeredUser.getPhoneNumberPrefix());

        // Assert that the registeredUser object has the same phone number as the customerRegisterDTO object
        assertEquals(customerRegisterDTO.getPhoneNumber(), registeredUser.getPhoneNumber());

        // Assert that the registeredUser object has an empty note
        assertEquals("", registeredUser.getNote());

        // Assert that the registeredUser object has the CLIENT role
        assertEquals(AppUserRole.CLIENT, registeredUser.getAppUserRole());
    }

    @Test
    @DisplayName("Should create a new OAuth2 user and send a welcome email")
    void createOAuth2UserAndSendWelcomeEmail() {
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Doe";
        AppUserRole appUserRole = AppUserRole.CLIENT;

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(emailTemplates.emailTemplateForWelcome(any(User.class))).thenReturn("Welcome to Bike Service!");
        doNothing().when(emailService).send(anyString(), anyString(), anyString());

        User createdUser = userService.createOAuth2User(email, firstName, lastName, appUserRole);

        assertNotNull(createdUser);
        assertEquals(email, createdUser.getEmail());
        assertEquals(firstName, createdUser.getFirstName());
        assertEquals(lastName, createdUser.getLastName());
        assertEquals(appUserRole, createdUser.getAppUserRole());

        verify(userRepository, times(1)).save(any(User.class));

        verify(emailTemplates, times(1)).emailTemplateForWelcome(any(User.class));

        verify(emailService, times(1)).send(email, "Welcome to Bike Service!", "Welcome to Bike Service!");
    }

    @Test
    @DisplayName("Should throw an exception when the email is already taken")
    void createEmployeeWhenEmailIsAlreadyTakenThenThrowException() {
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO(
                "email@example.com",
                "John",
                "Doe",
                "+1",
                "1234567890",
                "Note"
        );

        when(userRepository.existsByEmail(customerRegisterDTO.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.createEmployee(customerRegisterDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This email is already taken!", exception.getReason());
        verify(userRepository, times(1)).existsByEmail(customerRegisterDTO.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should create a new employee when the email is not taken")
    void createEmployeeWhenEmailIsNotTaken() {
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO(
                "test@test.com",
                "John",
                "Doe",
                "+1",
                "123456789",
                "Note"
        );

        when(userRepository.existsByEmail(customerRegisterDTO.getEmail())).thenReturn(false);

        user.setAppUserRole(AppUserRole.EMPLOYEE);
        user.setEmail(customerRegisterDTO.getEmail());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createEmployee(customerRegisterDTO);

        assertNotNull(createdUser);
        assertEquals(user.getUserId(), createdUser.getUserId());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getAppUserRole(), createdUser.getAppUserRole());

        verify(userRepository, times(1)).existsByEmail(customerRegisterDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).send(
                user.getEmail(),
                emailTemplates.emailTemplateForWelcome(user),
                "Welcome to Bike Service Employee Family!"
        );
    }

    @Test
    @DisplayName("Should throw an exception when user does not exist")
    void uploadUserAvatarWhenUserDoesNotExistThenThrowException() {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.uploadUserAvatar(userId, file));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should upload user avatar when user exists and file is provided")
    void uploadUserAvatarWhenUserExistsAndFileProvided() {
        UUID userId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);
        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileService.uploadFile(file)).thenReturn(UUID.randomUUID());

        userService.uploadUserAvatar(userId, file);

        verify(userRepository, times(1)).findById(userId);
        verify(fileService, times(1)).uploadFile(file);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should update user when email is not changed")
    void testUpdateAppUserWhenEmailIsNotChanged() {
        // create test data
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("johndoe@example.com");
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // call the method being tested
        userService.updateAppUser(user.getUserId(), updateDTO);

        // assert that the user was updated and saved
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should update user when email is changed and new email is not taken")
    void testUpdateAppUserWhenEmailIsChangedAndNewEmailIsNotTaken() {
        // create test data
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("johndoe@example.com");
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // call the method being tested
        userService.updateAppUser(user.getUserId(), updateDTO);

        // assert that the user was updated and saved
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw CONFLICT when email is changed and new email is taken")
    void testUpdateAppUserWhenEmailIsChangedAndNewEmailIsTaken() {
        // create test data
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("johndoe@example.com");

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateDTO.getEmail())).thenReturn(true);

        // call the method being tested and assert that it throws CONFLICT
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateAppUser(user.getUserId(), updateDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("This email is already taken!", exception.getReason());
    }

    @Test
    @DisplayName("Should update user, company and address when user is client and company is present")
    void testUpdateAppUserWhenUserIsClientAndCompanyIsPresent() {
        // create test data
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");
        updateDTO.setEmail("johndoe@example.com");
        updateDTO.setCompanyName("ACME Inc.");
        updateDTO.setTaxNumber("1234567890");
        updateDTO.setStreetName("Main St.");
        updateDTO.setPostCode("12345");
        updateDTO.setCity("City");
        updateDTO.setCountryId(countryId);

        user.setIsPasswordChangeRequired(false);

        Country country = new Country(countryId, "Country", new ArrayList<>());
        Company company = new Company(companyId, "ACME Inc.", "1234567890", user);
        Address address = new Address(user, country, "Main St.", "12345", "City");
        address.setAddressId(addressId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(companyService.getCompanyByUser(user)).thenReturn(company);
        when(addressService.getAddressByUser(user)).thenReturn(address);
        when(countryService.getCountryById(countryId)).thenReturn(country);

        // call the method being tested
        userService.updateAppUser(userId, updateDTO);

        // assert that the user, company and address were updated and saved
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe@example.com", user.getEmail());
        assertEquals(AppUserRole.CLIENT, user.getAppUserRole());
        assertFalse(user.getIsPasswordChangeRequired());
        assertEquals("ACME Inc.", company.getCompanyName());
        assertEquals("1234567890", company.getTaxNumber());
        assertEquals("Main St.", address.getStreetName());
        assertEquals("12345", address.getPostCode());
        assertEquals("City", address.getCity());
        assertEquals("Country", address.getCountry().getCountryName());
        verify(userRepository, times(1)).save(user);
        verify(companyService, times(1)).updateCompany(companyId, new CompanyUpdateDTO("ACME Inc.", "1234567890"));
        verify(addressService, times(1)).updateAddress(addressId, new AddressUpdateDTO("Main St.", "12345", "City", country));
    }

    @Test
    @DisplayName("Should throw an exception when a null file is provided")
    void updateUserAvatarWithNullFileThenThrowException() {
        assertThrows(ResponseStatusException.class, () -> {
            userService.updateUserAvatar(user.getUserId(), null);
        });
    }

    @Test
    @DisplayName("Should throw an exception when an invalid user ID is provided")
    void updateUserAvatarWithInvalidUserIdThenThrowException() {
        UUID invalidUserId = UUID.randomUUID();
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUserAvatar(invalidUserId, file));

        verify(userRepository, times(1)).findById(invalidUserId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should update the user avatar when a valid user ID and file are provided")
    void updateUserAvatarWithValidUserIdAndFile() {// Create a mock file
        MultipartFile mockFile = mock(MultipartFile.class);

        // Create a mock file object
        File mockFileObject = new File(UUID.randomUUID(), "testFile.jpg");

        // Create a mock user object
        User mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setAvatar(mockFileObject);

        // Mock the userRepository to return the mock user object when findById is called
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));

        // Mock the fileService to return the UUID of the uploaded file
        UUID newAvatarId = UUID.randomUUID();
        when(fileService.uploadFile(mockFile)).thenReturn(newAvatarId);

        // Mock the fileService to return the mock file object when getFileById is called with the newAvatarId
        when(fileService.getFileById(newAvatarId)).thenReturn(mockFileObject);

        // Call the updateUserAvatar method with the mock user ID and mock file
        userService.updateUserAvatar(mockUser.getUserId(), mockFile);

        // Verify that the userRepository was called to save the updated user object
        verify(userRepository, times(1)).save(mockUser);

        // Verify that the fileService was called to delete the old avatar file
        verify(fileService, times(1)).deleteFile(mockFileObject.getFileId());

        // Verify that the user object's avatar was updated with the new file object
        assertEquals(mockFileObject, mockUser.getAvatar());
    }

    @Test
    @DisplayName("Should activate user account and set password when user is not activated and new password and confirmation match")
    void testActivateAndSetPasswordWhenUserIsNotActivatedAndNewPasswordAndConfirmationMatch() {
        // create test data
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(newPassword, newPasswordConfirm);

        user.setPassword(bCryptPasswordEncoder.encode("oldPassword"));
        user.setEnabled(true);
        user.setIsPasswordChangeRequired(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // call the method being tested
        userService.activateAndSetPassword(userId, newPasswordDTO);

        // assert that the user account was activated, password was updated and saved
        assertFalse(user.getIsPasswordChangeRequired());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when user with given id doesn't exist")
    void testActivateAndSetPasswordWhenUserWithGivenIdDoesNotExist() {
        // create test data
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(newPassword, newPasswordConfirm);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // call the method being tested and assert that it throws NOT_FOUND
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.activateAndSetPassword(userId, newPasswordDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User with given id " + userId + " doesn't exist in database!", exception.getReason());
    }

    @Test
    @DisplayName("Should throw CONFLICT when user is already activated")
    void testActivateAndSetPasswordWhenUserIsAlreadyActivated() {
        // create test data
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(newPassword, newPasswordConfirm);

        user.setPassword(bCryptPasswordEncoder.encode("oldPassword"));
        user.setEnabled(false);
        user.setIsPasswordChangeRequired(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // call the method being tested and assert that it throws CONFLICT
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.activateAndSetPassword(userId, newPasswordDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("User with given id " + userId + " already activated account and set password", exception.getReason());
    }

    @Test
    @DisplayName("Should throw NOT_ACCEPTABLE when new password and confirmation don't match")
    void testActivateAndSetPasswordWhenNewPasswordAndConfirmationDoNotMatch() {
        // create test data
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword";
        String newPasswordConfirm = "differentPassword";
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(newPassword, newPasswordConfirm);

        user.setPassword(bCryptPasswordEncoder.encode("newPassword"));
        user.setIsPasswordChangeRequired(true);
        user.setEnabled(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // call the method being tested and assert that it throws NOT_ACCEPTABLE
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.activateAndSetPassword(userId, newPasswordDTO);
        });
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
        assertEquals("New password confirmation doesn't match new password", exception.getReason());
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void resetPasswordWhenUserDoesNotExistThenThrowException() {
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.resetPassword(email));

        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should reset the password and send an email when the user exists")
    void resetPasswordWhenUserExists() {
        String email = user.getEmail();
        String emailTemplate = "emailTemplate";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(emailTemplates.emailTemplateForResetPassword(user)).thenReturn(emailTemplate);

        userService.resetPassword(email);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(2)).save(user);
        verify(emailService, times(1)).send(email, emailTemplate, "Password Reset Requested!");
    }

    @Test
    @DisplayName("Should change user password when old password is correct and new password and confirmation match")
    void testChangeUserPasswordWhenOldPasswordIsCorrectAndNewPasswordAndConfirmationMatch() {
        // create test data
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword, newPassword, newPasswordConfirm);

        when(bCryptPasswordEncoder.encode(oldPassword)).thenReturn(oldPassword);
        user.setPassword(bCryptPasswordEncoder.encode(oldPassword));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // call the method being tested
        userService.changeUserPassword(user.getUserId(), changePasswordDTO);

        // assert that the user password was updated and saved
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when user with given id doesn't exist")
    void testChangeUserPasswordWhenUserWithGivenIdDoesNotExist() {
        // create test data
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword, newPassword, newPasswordConfirm);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        // call the method being tested and assert that it throws NOT_FOUND
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.changeUserPassword(user.getUserId(), changePasswordDTO);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Should throw CONFLICT when old password is incorrect")
    void testChangeUserPasswordWhenOldPasswordIsIncorrect() {
        // create test data
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword, newPassword, newPasswordConfirm);

        user.setPassword(bCryptPasswordEncoder.encode("correctPassword"));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(oldPassword)).thenReturn("encodedOldPassword");

        // call the method being tested and assert that it throws CONFLICT
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.changeUserPassword(user.getUserId(), changePasswordDTO);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Password provided doesn't match actual password, Request Denied", exception.getReason());
    }

    @Test
    @DisplayName("Should throw NOT_ACCEPTABLE when new password and confirmation don't match")
    void testChangeUserPasswordWhenNewPasswordAndConfirmationDoNotMatch() {
        // create test data
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newPasswordConfirm = "differentPassword";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword, newPassword, newPasswordConfirm);

        when(bCryptPasswordEncoder.encode(oldPassword)).thenReturn(oldPassword);
        user.setPassword(bCryptPasswordEncoder.encode(oldPassword));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // call the method being tested and assert that it throws NOT_ACCEPTABLE
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.changeUserPassword(user.getUserId(), changePasswordDTO);
        });
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
        assertEquals("New password and password confirmation doesn't match", exception.getReason());
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void changeStateOfUserWhenUserDoesNotExistThenThrowException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.changeStateOfUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should change the state of the user when the user exists")
    void changeStateOfUserWhenUserExists() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.changeStateOfUser(user.getUserId());

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getLocked());
        assertFalse(user.getEnabled());
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a user by id that does not exist")
    void deleteUserByIdWhenUserDoesNotExistThenThrowException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    @DisplayName("Should delete the user by id when the user exists")
    void deleteUserByIdWhenUserExists() {
        UUID userId = user.getUserId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should return true when the email exists in the database")
    void existsByEmailWhenEmailExists() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Should return false when the email does not exist in the database")
    void existsByEmailWhenEmailDoesNotExist() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void deleteUserAvatarWhenUserNotFoundThenThrowException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.deleteUserAvatar(userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, fileService);
    }

    @Test
    @DisplayName("Should delete the user avatar and set it to null")
    void deleteUserAvatarWhenAvatarExists() {
        UUID fileId = UUID.randomUUID();
        // Create a mock user with an avatar
        File avatar = new File();
        avatar.setFileId(fileId);
        user.setAvatar(avatar);

        // Mock the userRepository to return the user
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Call the deleteUserAvatar method
        userService.deleteUserAvatar(user.getUserId());

        // Verify that the user's avatar is set to null
        assertNull(user.getAvatar());

        // Verify that the userRepository.save method is called once
        verify(userRepository, times(1)).save(user);

        // Verify that the fileService.deleteFile method is called once with the avatar's fileId
        verify(fileService, times(1)).deleteFile(fileId);
    }
}