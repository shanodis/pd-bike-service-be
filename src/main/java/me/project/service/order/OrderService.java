package me.project.service.order;

import me.project.dtos.request.PageRequestDTO;
import me.project.dtos.request.order.OrderCreateRequestDTO;
import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.dtos.response.page.PageResponse;
import me.project.entitiy.Bike;
import me.project.entitiy.Order;
import me.project.entitiy.OrderPart;
import me.project.entitiy.User;
import me.project.enums.SearchOperation;
import me.project.enums.Status;
import me.project.repository.BikeRepository;
import me.project.repository.OrderRepository;
import me.project.repository.UserRepository;
import me.project.search.SearchCriteria;
import me.project.search.specificator.Specifications;
import me.project.service.order.part.IOrderPartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final IOrderPartService orderPartService;
    private final BikeRepository bikeRepository;
    private final UserRepository userRepository;

    private static String ORDER_NOT_FOUND(UUID orderID) {
        return String.format("Order with id %s not exists in database", orderID);
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
                                                              UUID orderStatusId) {

        Specifications<Order> orderSpecifications = new Specifications<>();

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
                    .or(new SearchCriteria("orderServices.service.serviceName", phrase.trim(), SearchOperation.MATCH_JOIN_LIST_OBJECT))
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

    public void updateOrdersOrderPart(UUID orderId, UUID orderPartId, UUID newOrderPartId) {

        Order order = getById(orderId);

        ArrayList<OrderPart> orderParts = new ArrayList<>(getById(orderId).getOrderParts());

        orderParts.set(
                orderParts.indexOf(orderPartService.getOrderPartById(orderPartId)),
                orderPartService.getOrderPartById(newOrderPartId)
        );

        order.setOrderParts(orderParts);

        orderRepository.save(order);
    }

    public void deleteOrdersOrderPart(UUID orderId, UUID orderPartId) {
        Order order = getById(orderId);

        ArrayList<OrderPart> orderParts = new ArrayList<>(getById(orderId).getOrderParts());

        orderParts.remove(orderPartService.getOrderPartById(orderPartId));

        order.setOrderParts(orderParts);
//        order.setOrderStatus(Status.TODO.getOrderStatus());

        orderRepository.save(order);
    }


}
