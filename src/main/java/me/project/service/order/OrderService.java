package me.project.service.order;

import lombok.AllArgsConstructor;
import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.request.orderPart.OrderPartUpdateRequestDTO;
import me.project.dtos.request.orderService.OrderServiceCreateRequestDTO;
import me.project.dtos.request.service.CreateServiceDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.dtos.response.services.ServiceDTO;
import me.project.entitiy.Bike;
import me.project.entitiy.Order;
import me.project.entitiy.OrderPart;
import me.project.entitiy.User;
import me.project.enums.SearchOperation;
import me.project.enums.Status;
import me.project.repository.*;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.order.part.IOrderPartService;
import me.project.service.order.status.OrderStatusService;
import me.project.service.service.IServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final IOrderPartService orderPartService;
    private final OrderStatusService orderStatusService;
    private final BikeRepository bikeRepository;
    private final UserRepository userRepository;
    private final OrderPartRepository orderPartRepository;
    private final ServiceRepository serviceRepository;
    private final OrderServiceRepository orderServiceRepository;
    private final IServiceService serviceService;

    private static String ORDER_NOT_FOUND(UUID orderID) {
        return String.format("Order with id %s not exists in database", orderID);
    }

    private static String ORDER_PART_NOT_FOUND(UUID orderPartId) {
        return String.format("Order part with id %s not exists in database", orderPartId);
    }

    public Order getLatestByUser(User user) {
        return orderRepository.findFirstByUserOrderByCreatedOnDesc(user);
    }

    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND(orderId))
        );

    }


    public PageResponse<OrderPaginationResponseDTO> getOrders(PageRequestDTO requestDTO,
                                                              String phrase,
                                                              LocalDateTime orderDateFrom,
                                                              LocalDateTime orderDateTo,
                                                              UUID orderStatusId,
                                                              UUID userId) {

        Specifications<Order> orderSpecifications = new Specifications<>();

        if (userId != null)
            orderSpecifications
                    .and(new SearchCriteria("user.userId", userId, SearchOperation.EQUAL_JOIN));

        if (orderStatusId != null)
            orderSpecifications
                    .and(new SearchCriteria("orderStatus.orderStatusId", orderStatusId, SearchOperation.EQUAL_JOIN));

        if (orderDateFrom != null)
            orderSpecifications
                    .and(new SearchCriteria("createdOn", orderDateFrom, SearchOperation.GREATER_THAN_EQUAL_DATE));

        if (orderDateTo != null)
            orderSpecifications
                    .and(new SearchCriteria("createdOn", orderDateTo, SearchOperation.LESS_THAN_EQUAL_DATE));

        if (phrase != null && !phrase.isEmpty())
            orderSpecifications
                    .or(new SearchCriteria("note", phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bike.bikeName", phrase.trim(), SearchOperation.MATCH_JOIN))
                    .or(new SearchCriteria("user.firstName", phrase.trim(), SearchOperation.MATCH_JOIN))
                    .or(new SearchCriteria("user.lastName", phrase.trim(), SearchOperation.MATCH_JOIN))
                    // TODO poprawić jak jest pusta lista wewnętrzna
//                    .or(new SearchCriteria("orderServices.service.serviceName", phrase.trim(), SearchOperation.MATCH_JOIN_LIST_OBJECT))
                    ;

        if (!orderSpecifications.isEmpty())
            return new PageResponse<>(
                    orderRepository.findAll(orderSpecifications, requestDTO.getRequest(Order.class))
                            .map(OrderPaginationResponseDTO::convertFromEntity)
            );

        return new PageResponse<>(
                orderRepository.findAll(requestDTO.getRequest(Order.class))
                        .map(OrderPaginationResponseDTO::convertFromEntity)
        );
    }


    public UUID createOrder(OrderCreateRequestDTO request) {

        Bike bike;

        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        if (request.getBikeId() == null) {

            bike = new Bike(
                    user,
                    request.getBikeName().trim(),
                    request.getBikeMake().trim(),
                    request.getBikeModel().trim(),
                    request.getSerialNumber(),
                    request.getYearOfProduction()
            );

            bikeRepository.save(bike);

        } else
            bike = bikeRepository.findById(request.getBikeId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found")
            );


        Order order = new Order(
                request.getNote().trim(),
                LocalDateTime.now(),
                bike,
                user,
                Status.TODO.getOrderStatus()
        );

        orderRepository.save(order);

        return order.getOrderId();

    }


    public void addOrderPartToOrder(UUID orderId, UUID orderPartId) {
        Order order = getById(orderId);

        ArrayList<OrderPart> orderParts = new ArrayList<>(getById(orderId).getOrderParts());

        orderParts.add(orderPartService.getOrderPartById(orderPartId));

        order.setOrderParts(orderParts);

        orderRepository.save(order);
    }

    @Transactional
    public UUID addOrderServiceToOrder(UUID orderId, OrderServiceCreateRequestDTO request) {

        Order order = getById(orderId);
        me.project.entitiy.OrderService orderService = new me.project.entitiy.OrderService();

        if (request.getServiceId() != null) {

            ServiceDTO service = serviceService.getServiceById(request.getServiceId());

            if (request.getServicePrice().compareTo(service.getServicePrice()) != 0) {
                orderService = createServiceSetOrderService(request.getServiceName(), request.getServicePrice());
            } else {
                me.project.entitiy.Service tmp = new me.project.entitiy.Service();
                tmp.setServiceId(request.getServiceId());
                orderService.setService(tmp);
                orderServiceRepository.save(orderService);
            }
        } else {
            orderService = createServiceSetOrderService(request.getServiceName(), request.getServicePrice());
        }

        {
            Order tmp = new Order();
            tmp.setOrderId(orderId);
            orderService.setOrder(tmp);
        }

        ArrayList<me.project.entitiy.OrderService> orderServices = new ArrayList<>();
        orderServices.add(orderService);
        order.setOrderServices(orderServices);
        orderRepository.save(order);
        return orderService.getOrderServiceId();
    }

    public void completePayment(UUID orderId) {
        Order order = getById(orderId);

        order.setIsPayed(true);
        order.setOrderStatus(Status.DONE.getOrderStatus());

        orderRepository.save(order);
    }

    public void updateOrderService(UUID orderId, UUID orderStatusId) {

        Order order = getById(orderId);

        order.setOrderStatus(orderStatusService.getStatusById(orderStatusId));

        orderRepository.save(order);

    }

    public void updateOrderNote(UUID orderId, String note) {

        Order order = getById(orderId);

        order.setNote(note);

        orderRepository.save(order);
    }

    public void updateOrdersOrderPart(UUID orderId, UUID orderPartId, OrderPartUpdateRequestDTO request) {

        getById(orderId);

        OrderPart orderPart = orderPartRepository.findById(orderPartId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_PART_NOT_FOUND(orderPartId))
        );

        if (!orderPart.getOrderCode().equals(request.getOrderCode()) && request.getOrderCode() != null)
            orderPart.setOrderCode(request.getOrderCode());

        if (!orderPart.getOrderName().equals(request.getOrderName()) && request.getOrderName() != null)
            orderPart.setOrderName(request.getOrderName());

        if (orderPart.getOrderPrice().compareTo(request.getOrderPrice()) != 0 && request.getOrderPrice() != null)
            orderPart.setOrderPrice(request.getOrderPrice());

        orderPartRepository.save(orderPart);
    }

    public void deleteOrdersOrderPart(UUID orderId, UUID orderPartId) {

        getById(orderId);

        OrderPart orderPart = orderPartService.getOrderPartById(orderPartId);
        orderPartRepository.delete(orderPart);
    }

    public void deleteOrdersOrderService(UUID orderId, UUID orderServiceId) {

        getById(orderId);

        me.project.entitiy.OrderService orderService = orderServiceRepository.findById(orderServiceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order service do not exist")
        );

        orderServiceRepository.delete(orderService);
    }


    private me.project.entitiy.OrderService createServiceSetOrderService(String serviceName, BigDecimal servicePrice) {
        me.project.entitiy.OrderService orderService = new me.project.entitiy.OrderService();
        UUID serviceId = serviceService.createService(new CreateServiceDTO(serviceName, servicePrice));
        {
            me.project.entitiy.Service tmp = new me.project.entitiy.Service();
            tmp.setServiceId(serviceId);
            orderService.setService(tmp);
            orderServiceRepository.save(orderService);
        }

        return orderService;
    }


}
