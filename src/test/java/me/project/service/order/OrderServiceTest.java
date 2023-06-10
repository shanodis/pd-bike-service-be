package me.project.service.order;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.request.orderPart.OrderPartUpdateRequestDTO;
import me.project.dtos.request.orderService.OrderServiceCreateRequestDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.entitiy.*;
import me.project.repository.*;
import me.project.search.specificator.Specifications;
import me.project.service.order.part.IOrderPartService;
import me.project.service.order.status.OrderStatusService;
import me.project.service.service.IServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private IOrderPartService orderPartService;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderPartRepository orderPartRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private OrderServiceRepository orderServiceRepository;

    @Mock
    private IServiceService serviceService;

    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());

        orderService =
                new OrderService(
                        orderRepository,
                        orderPartService,
                        orderStatusService,
                        bikeRepository,
                        userRepository,
                        orderPartRepository,
                        serviceRepository,
                        orderServiceRepository,
                        serviceService);
    }

    @Test
    @DisplayName("Should delete order service when order ID and order service ID are valid")
    void testDeleteOrdersOrderServiceWhenOrderIdAndOrderServiceIdValid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderServiceId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);

        me.project.entitiy.OrderService orderServiceEntity = new me.project.entitiy.OrderService();
        orderServiceEntity.setOrderServiceId(orderServiceId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderServiceRepository.findById(orderServiceId)).thenReturn(Optional.of(orderServiceEntity));

        // call the method being tested
        orderService.deleteOrdersOrderService(orderId, orderServiceId);

        // assert that the repository methods were called and the order service was deleted
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderServiceRepository, times(1)).findById(orderServiceId);
        verify(orderServiceRepository, times(1)).delete(orderServiceEntity);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testDeleteOrdersOrderServiceWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        UUID orderServiceId = UUID.randomUUID();

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.deleteOrdersOrderService(invalidOrderId, orderServiceId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository, orderServiceRepository);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order service ID is invalid")
    void testDeleteOrdersOrderServiceWhenOrderServiceIdInvalid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID invalidOrderServiceId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(orderServiceRepository.findById(invalidOrderServiceId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.deleteOrdersOrderService(orderId, invalidOrderServiceId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderServiceRepository, times(1)).findById(invalidOrderServiceId);
        verifyNoMoreInteractions(orderRepository, orderServiceRepository);
    }

    @Test
    @DisplayName("Should delete order part when order ID and order part ID are valid")
    void testDeleteOrdersOrderPartWhenOrderIdAndOrderPartIdValid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);

        OrderPart orderPart = new OrderPart();
        orderPart.setOrderPartId(orderPartId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPartService.getOrderPartById(orderPartId)).thenReturn(orderPart);

        // call the method being tested
        orderService.deleteOrdersOrderPart(orderId, orderPartId);

        // assert that the repository methods were called and the order part was deleted
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderPartService, times(1)).getOrderPartById(orderPartId);
        verify(orderPartRepository, times(1)).delete(orderPart);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testDeleteOrdersOrderPartWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.deleteOrdersOrderPart(invalidOrderId, orderPartId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository, orderPartRepository, orderPartService);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order part ID is invalid")
    void testDeleteOrdersOrderPartWhenOrderPartIdInvalid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID invalidOrderPartId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(orderPartService.getOrderPartById(invalidOrderPartId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.deleteOrdersOrderPart(orderId, invalidOrderPartId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderPartService, times(1)).getOrderPartById(invalidOrderPartId);
        verifyNoMoreInteractions(orderRepository, orderPartRepository, orderPartService);
    }

    @Test
    @DisplayName("Should update order part when order ID, order part ID, and request are valid")
    void testUpdateOrdersOrderPartWhenOrderIdOrderPartIdAndRequestValid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);

        OrderPartUpdateRequestDTO request = new OrderPartUpdateRequestDTO();
        request.setOrderCode("Test order code");
        request.setOrderName("Test order name");
        request.setOrderPrice(BigDecimal.valueOf(10.0));

        OrderPart orderPart = new OrderPart();
        orderPart.setOrderPartId(orderPartId);
        orderPart.setOrderCode("Old order code");
        orderPart.setOrderName("Old order name");
        orderPart.setOrderPrice(BigDecimal.valueOf(5.0));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPartRepository.findById(orderPartId)).thenReturn(Optional.of(orderPart));

        // call the method being tested
        orderService.updateOrdersOrderPart(orderId, orderPartId, request);

        // assert that the repository methods were called and the order part was updated
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderPartRepository, times(1)).findById(orderPartId);
        verify(orderPartRepository, times(1)).save(orderPart);
        assertEquals(request.getOrderCode(), orderPart.getOrderCode());
        assertEquals(request.getOrderName(), orderPart.getOrderName());
        assertEquals(request.getOrderPrice(), orderPart.getOrderPrice());
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testUpdateOrdersOrderPartWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();

        OrderPartUpdateRequestDTO request = new OrderPartUpdateRequestDTO();

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.updateOrdersOrderPart(invalidOrderId, orderPartId, request));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository, orderPartRepository);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order part ID is invalid")
    void testUpdateOrdersOrderPartWhenOrderPartIdInvalid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID invalidOrderPartId = UUID.randomUUID();

        OrderPartUpdateRequestDTO request = new OrderPartUpdateRequestDTO();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(orderPartRepository.findById(invalidOrderPartId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.updateOrdersOrderPart(orderId, invalidOrderPartId, request));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderPartRepository, times(1)).findById(invalidOrderPartId);
        verifyNoMoreInteractions(orderRepository, orderPartRepository);
    }

    @Test
    @DisplayName("Should update order note when order ID is valid")
    void testUpdateOrderNoteWhenOrderIdValid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        String note = "Test note";

        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // call the method being tested
        orderService.updateOrderNote(orderId, note);

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testUpdateOrderNoteWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        String note = "Test note";

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.updateOrderNote(invalidOrderId, note));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Should update order service status when order ID and order status ID are valid")
    void testUpdateOrderServiceWhenOrderIdAndOrderStatusIdValid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderStatusId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatusId(orderStatusId);
        when(orderStatusService.getStatusById(orderStatusId)).thenReturn(orderStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // call the method being tested
        orderService.updateOrderService(orderId, orderStatusId);

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderStatusService, times(1)).getStatusById(orderStatusId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testUpdateOrderServiceWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        UUID orderStatusId = UUID.randomUUID();

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.updateOrderService(invalidOrderId, orderStatusId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository, orderStatusService);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order status ID is invalid")
    void testUpdateOrderServiceWhenOrderStatusIdInvalid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderStatusId = UUID.randomUUID();
        UUID invalidOrderStatusId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderStatusService.getStatusById(invalidOrderStatusId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.updateOrderService(orderId, invalidOrderStatusId));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderStatusService, times(1)).getStatusById(invalidOrderStatusId);
        verifyNoMoreInteractions(orderRepository, orderStatusService);
    }

    @Test
    @DisplayName("Should add order service to order when service ID is not null and service price is different")
    void testAddOrderServiceToOrderWhenServiceIdNotNullAndServicePriceDifferent() {
        // create test data
        UUID orderServiceId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        String serviceName = "Test service";
        BigDecimal servicePrice = BigDecimal.valueOf(10.0);
        OrderServiceCreateRequestDTO request = new OrderServiceCreateRequestDTO(serviceId, serviceName, BigDecimal.valueOf(20.0));

        Order order = new Order();
        order.setOrderId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ServiceDTO service = new ServiceDTO(serviceId, serviceName, servicePrice);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);

        me.project.entitiy.OrderService expectedOrderService = new me.project.entitiy.OrderService();
        expectedOrderService.setOrderServiceId(orderServiceId);
        expectedOrderService.setOrder(order);
        expectedOrderService.setService(new me.project.entitiy.Service(serviceId, serviceName, servicePrice, new ArrayList<>()));
        when(orderServiceRepository.save(any(me.project.entitiy.OrderService.class))).thenReturn(expectedOrderService);

        // call the method being tested
        UUID result = orderService.addOrderServiceToOrder(orderId, request);

        // assert the result
        assertNotNull(result);
        assertEquals(expectedOrderService.getOrderServiceId(), result);

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(serviceService, times(1)).getServiceById(serviceId);
        verify(orderServiceRepository, times(1)).save(any(me.project.entitiy.OrderService.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should add order service to order when service ID is not null and service price is the same")
    void testAddOrderServiceToOrderWhenServiceIdNotNullAndServicePriceSame() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID orderServiceId = UUID.randomUUID();
        String serviceName = "Test service";
        BigDecimal servicePrice = BigDecimal.valueOf(10.0);
        OrderServiceCreateRequestDTO request = new OrderServiceCreateRequestDTO(serviceId, serviceName, servicePrice);

        Order order = new Order();
        order.setOrderId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ServiceDTO service = new ServiceDTO(serviceId, serviceName, servicePrice);
        when(serviceService.getServiceById(serviceId)).thenReturn(service);

        me.project.entitiy.OrderService expectedOrderService = new me.project.entitiy.OrderService();
        expectedOrderService.setOrderServiceId(orderServiceId);
        expectedOrderService.setOrder(order);
        expectedOrderService.setService(new me.project.entitiy.Service(serviceId, serviceName, servicePrice, new ArrayList<>()));
        when(orderServiceRepository.save(any(me.project.entitiy.OrderService.class))).thenReturn(expectedOrderService);

        // call the method being tested
        UUID result = orderService.addOrderServiceToOrder(orderId, request);

        // assert the result
        assertNotNull(result);
        assertEquals(expectedOrderService.getOrderServiceId(), result);

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(serviceService, times(1)).getServiceById(serviceId);
        verify(orderServiceRepository, times(1)).save(any(me.project.entitiy.OrderService.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should add order service to order when service ID is null")
    void testAddOrderServiceToOrderWhenServiceIdNull() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderServiceId = UUID.randomUUID();
        String serviceName = "Test service";
        BigDecimal servicePrice = BigDecimal.valueOf(10.0);
        OrderServiceCreateRequestDTO request = new OrderServiceCreateRequestDTO(null, serviceName, servicePrice);

        Order order = new Order();
        order.setOrderId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        me.project.entitiy.OrderService expectedOrderService = new me.project.entitiy.OrderService();
        expectedOrderService.setOrderServiceId(orderServiceId);
        expectedOrderService.setOrder(order);
        expectedOrderService.setService(new me.project.entitiy.Service(null, serviceName, servicePrice, new ArrayList<>()));
        when(orderServiceRepository.save(any(me.project.entitiy.OrderService.class))).thenReturn(expectedOrderService);

        // call the method being tested
        UUID result = orderService.addOrderServiceToOrder(orderId, request);

        // assert the result
        assertNotNull(result);
        assertEquals(expectedOrderService.getOrderServiceId(), result);

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderServiceRepository, times(1)).save(any(me.project.entitiy.OrderService.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("Should throw ResponseStatusException when order ID is invalid")
    void testAddOrderServiceToOrderWhenOrderIdInvalid() {
        // create test data
        UUID invalidOrderId = UUID.randomUUID();
        String serviceName = "Test service";
        BigDecimal servicePrice = BigDecimal.valueOf(10.0);
        OrderServiceCreateRequestDTO request = new OrderServiceCreateRequestDTO(null, serviceName, servicePrice);

        when(orderRepository.findById(invalidOrderId)).thenThrow(ResponseStatusException.class);

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.addOrderServiceToOrder(invalidOrderId, request));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(invalidOrderId);
        verifyNoMoreInteractions(orderRepository, orderServiceRepository, serviceService);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when service ID is invalid")
    void testAddOrderServiceToOrderWhenServiceIdInvalid() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID invalidServiceId = UUID.randomUUID();
        String serviceName = "Test service";
        BigDecimal servicePrice = BigDecimal.valueOf(10.0);
        OrderServiceCreateRequestDTO request = new OrderServiceCreateRequestDTO(invalidServiceId, serviceName, servicePrice);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(serviceService.getServiceById(invalidServiceId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // call the method being tested and assert that it throws a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> orderService.addOrderServiceToOrder(orderId, request));

        // assert that the repository methods were called
        verify(orderRepository, times(1)).findById(orderId);
        verify(serviceService, times(1)).getServiceById(invalidServiceId);
        verifyNoMoreInteractions(orderRepository, orderServiceRepository, serviceService);
    }

    @Test
    @DisplayName("Should throw an exception when the order does not exist")
    void addOrderPartToOrderWhenOrderDoesNotExistThenThrowException() {
        UUID orderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> {
                    orderService.addOrderPartToOrder(orderId, orderPartId);
                });

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderPartService);
    }

    @Test
    @DisplayName("Should add the order part to the order when both order and order part exist")
    void testAddOrderPartToOrderWhenOrderAndOrderPartExist() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderPartId = UUID.randomUUID();
        Order order = new Order(
                "Test note",
                LocalDateTime.now(),
                new Bike(user, "Test bike", "Test make", "Test model", "Test serial", 2021),
                user,
                new OrderStatus(UUID.randomUUID(), "Test status")
        );
        order.setOrderId(orderId);
        OrderPart orderPart = new OrderPart(
                UUID.randomUUID(),
                order,
                "Test part",
                "Test description",
                BigDecimal.valueOf(5)
        );
        orderPart.setOrderPartId(orderPartId);
        order.setOrderParts(new ArrayList<>());

        // mock dependencies
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPartService.getOrderPartById(orderPartId)).thenReturn(orderPart);

        // call the method being tested
        orderService.addOrderPartToOrder(orderId, orderPartId);

        // assert that the repository methods were called
        verify(orderRepository, times(2)).findById(orderId);
        verify(orderPartService, times(1)).getOrderPartById(orderPartId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw an exception when user is not found")
    void createOrderWhenUserNotFoundThenThrowException() {
        OrderCreateRequestDTO request = new OrderCreateRequestDTO();
        request.setUserId(UUID.randomUUID());
        request.setNote("Test note");

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        // when, then
        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            orderService.createOrder(request);
                        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    @DisplayName("Should throw an exception when bike is not found")
    void createOrderWhenBikeNotFoundThenThrowException() {
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO();
        requestDTO.setUserId(user.getUserId());
        requestDTO.setBikeId(UUID.randomUUID());
        requestDTO.setNote("Test note");

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bikeRepository.findById(requestDTO.getBikeId())).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            orderService.createOrder(requestDTO);
                        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Bike not found", exception.getReason());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bikeRepository, times(1)).findById(requestDTO.getBikeId());
    }

    @Test
    @DisplayName("Should create a new order with a new bike when bikeId is not provided")
    void createOrderWithNewBike() {
        String note = "Test note";
        String bikeName = "Test bike name";
        String bikeMake = "Test bike make";
        String bikeModel = "Test bike model";
        String serialNumber = "Test serial number";
        int yearOfProduction = 2021;

        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO();
        requestDTO.setUserId(user.getUserId());
        requestDTO.setNote(note);
        requestDTO.setBikeName(bikeName);
        requestDTO.setBikeMake(bikeMake);
        requestDTO.setBikeModel(bikeModel);
        requestDTO.setSerialNumber(serialNumber);
        requestDTO.setYearOfProduction(yearOfProduction);

        Bike bike = new Bike();
        bike.setBikeId(UUID.randomUUID());
        bike.setUser(user);
        bike.setBikeName(bikeName);
        bike.setBikeMake(bikeMake);
        bike.setBikeModel(bikeModel);
        bike.setSerialNumber(serialNumber);
        bike.setYearOfProduction(yearOfProduction);

        Order order = new Order(
                note,
                LocalDateTime.now(),
                bike,
                user,
                new OrderStatus(UUID.randomUUID(), "TODO")
        );
        order.setOrderId(UUID.randomUUID());

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bikeRepository.save(any(Bike.class))).thenReturn(bike);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UUID orderId = orderService.createOrder(requestDTO);

        assertNotNull(orderId);
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bikeRepository, times(1)).save(any(Bike.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should create a new order with an existing bike when bikeId is provided")
    void createOrderWithExistingBike() {
        UUID orderId = UUID.randomUUID();
        UUID bikeId = UUID.randomUUID();
        String note = "Test note";
        String bikeName = "Test bike name";
        String bikeMake = "Test bike make";
        String bikeModel = "Test bike model";
        String serialNumber = "Test serial number";
        int yearOfProduction = 2021;

        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO();
        requestDTO.setUserId(user.getUserId());
        requestDTO.setNote(note);
        requestDTO.setBikeId(bikeId);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        Bike bike = new Bike(user, bikeName, bikeMake, bikeModel, serialNumber, yearOfProduction);
        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));

        Order order = new Order(
                note,
                LocalDateTime.now(),
                bike,
                user,
                new OrderStatus(UUID.randomUUID(), "TODO")
        );
        order.setOrderId(orderId);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UUID createdOrderId = orderService.createOrder(requestDTO);

        assertNotNull(createdOrderId);
        assertEquals(orderId, createdOrderId);
        assertEquals(note, order.getNote());
        assertEquals(bike, order.getBike());
        assertEquals(user, order.getUser());
        assertNotNull(order.getCreatedOn());
        assertEquals("TODO", order.getOrderStatus().getOrderStatusName());

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(bikeRepository, times(1)).findById(bikeId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return a page of orders without any filters")
    void getOrdersWithoutFilters() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "createdOn");
        String phrase = null;
        LocalDateTime orderDateFrom = null;
        LocalDateTime orderDateTo = null;
        UUID orderStatusId = null;
        UUID userId = null;

        when(orderRepository.findAll(pageRequestDTO.getRequest(Order.class)))
                .thenReturn(Page.empty());

        PageResponse<OrderPaginationResponseDTO> result = orderService.getOrders(pageRequestDTO, phrase, orderDateFrom, orderDateTo, orderStatusId, userId);

        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("Should return a page of orders within a specific date range")
    void testGetOrdersWithinDateRange() {
        // create test data
        LocalDateTime orderDateFrom = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime orderDateTo = LocalDateTime.of(2022, 1, 31, 23, 59, 59);
        UUID orderStatusId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "createdOn");

        Page<Order> orders = new PageImpl<>(Collections.emptyList());
        when(orderRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(orders);

        // call the method being tested
        PageResponse<OrderPaginationResponseDTO> result = orderService.getOrders(
                pageRequestDTO,
                null,
                orderDateFrom,
                orderDateTo,
                orderStatusId,
                userId
        );

        // assert the result
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getContent().size());

        // assert that the repository method was called
        verify(orderRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a page of orders with the given filters")
    void testGetOrdersWithFilters() {
        // create test data
        UUID orderId = UUID.randomUUID();
        UUID orderStatusId = UUID.randomUUID();
        LocalDateTime createdOn = LocalDateTime.now();
        String bikeName = "BikeName";
        String bikeModel = "BikeModel";
        String note = "Note";
        String orderStatusName = "OrderStatusName";
        String phrase = "BikeName";
        LocalDateTime orderDateFrom = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime orderDateTo = LocalDateTime.of(2022, 1, 31, 23, 59, 59);

        Order order = new Order(
                note,
                createdOn,
                new Bike(user, bikeName, "BikeMake", bikeModel, "SerialNumber", 2021),
                user,
                new OrderStatus(orderStatusId, orderStatusName)
        );
        order.setOrderId(orderId);
        order.setOrderServices(new ArrayList<>());

        Page<Order> orders = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findAll(any(Specifications.class), any(Pageable.class))).thenReturn(orders);

        // call the method being tested
        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "asc", "createdOn");

        PageResponse<OrderPaginationResponseDTO> result = orderService.getOrders(
                pageRequestDTO,
                phrase,
                orderDateFrom,
                orderDateTo,
                orderStatusId,
                user.getUserId()
        );

        // assert the result
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        OrderPaginationResponseDTO orderDTO = result.getContent().get(0);
        assertEquals(orderId, orderDTO.getOrderId());
        assertEquals(bikeName, orderDTO.getBikeName());
        assertEquals(bikeModel, orderDTO.getBikeModel());
        assertEquals(orderStatusName, orderDTO.getOrderStatusName());
        assertEquals(createdOn, orderDTO.getCreatedOn());
        assertEquals(user.getUserId(), orderDTO.getUserId());

        // assert that the repository method was called
        verify(orderRepository, times(1)).findAll(any(Specifications.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw a ResponseStatusException when the order ID is not found")
    void getByIdWhenOrderIdNotFoundThenThrowResponseStatusException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> orderService.getById(orderId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Should return the order when the order ID is valid")
    void getByIdWhenOrderIdIsValid() {
        UUID orderId = UUID.randomUUID();
        Order order =
                new Order(
                        "Test note",
                        LocalDateTime.now(),
                        new Bike(),
                        user,
                        new OrderStatus(UUID.randomUUID(), "Test status"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getById(orderId);

        assertNotNull(result);
        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should return null when there are no orders for the given user")
    void getLatestByUserReturnsNullWhenNoOrders() {
        when(orderRepository.findFirstByUserOrderByCreatedOnDesc(user)).thenReturn(null);

        Order result = orderService.getLatestByUser(user);

        assertNull(result);
        verify(orderRepository, times(1)).findFirstByUserOrderByCreatedOnDesc(user);
    }

    @Test
    @DisplayName("Should return the latest order for the given user")
    void testGetLatestByUserReturnsLatestOrder() {
        // create test data
        User user = new User();
        user.setUserId(UUID.randomUUID());
        Bike bike = new Bike(user, "Test bike", "Test make", "Test model", "12345", 2021);
        Order order2 = new Order("Test note 2", LocalDateTime.now(), bike, user, null);


        when(orderRepository.findFirstByUserOrderByCreatedOnDesc(user)).thenReturn(order2);

        // call the method being tested
        Order result = orderService.getLatestByUser(user);

        // assert the result
        assertNotNull(result);
        assertEquals(order2, result);

        // assert that the repository method was called
        verify(orderRepository, times(1)).findFirstByUserOrderByCreatedOnDesc(user);
    }
}