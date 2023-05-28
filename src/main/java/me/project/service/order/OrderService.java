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
import me.project.service.bike.BikeService;
import me.project.service.bike.IBikeService;
import me.project.service.order.part.IOrderPartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
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
        return orderRepository.findByUserOrderByCreatedOnDesc(user);
    }

    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND(orderId))
        );

    }


    public PageResponse<OrderPaginationResponseDTO> getOrders(PageRequestDTO requestDTO, String phrase) {

        Specifications<Order> orderSpecifications  = new Specifications<>();

        if (phrase != null && !phrase.isEmpty()) {
            orderSpecifications
                    .or(new SearchCriteria("note" , phrase.trim(), SearchOperation.MATCH))
                    .or(new SearchCriteria("bikeName", phrase.trim(), SearchOperation.MATCH_JOIN_BIKE));

            return new PageResponse<>(
                    orderRepository.findAll(orderSpecifications, requestDTO.getRequest(Order.class))
                            .map(OrderPaginationResponseDTO::convertFromEntity)
            );

        }

        return new PageResponse<>(
                orderRepository.findAll(requestDTO.getRequest(Order.class))
                        .map(OrderPaginationResponseDTO::convertFromEntity)
        );
    }


    public UUID createOrder(OrderCreateRequestDTO request) {

        UUID bikeId = request.getBikeId();

        if (request.getBikeId() == null) {
            Bike bike = new Bike();

            bike.setBikeName(request.getBikeName());
            bike.setBikeModel(request.getBikeModel());
            bike.setBikeMake(request.getBikeMake());
            bike.setYearOfProduction(request.getYearOfProduction());

            bikeRepository.save(bike);

            bikeId = bike.getBikeId();

        } else {
            bikeRepository.findById(request.getBikeId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found")
            );
        }

        userRepository.findById(request.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        Order order = new Order();

        {
            Bike tmp = new Bike();
            tmp.setBikeId(bikeId);
            order.setBike(tmp);
        }
        {
            User tmp = new User();
            tmp.setUserId(request.getUserId());
            order.setUser(tmp);
        }
        order.setNote(request.getNote().trim());
        order.setCreatedOn(LocalDateTime.now());

        orderRepository.save(order);

        return order.getOrderId();

    }


    public void addOrderPartToOrder(UUID orderId, UUID orderPartId){
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

    public void deleteOrdersOrderPart(UUID orderId, UUID orderPartId){
        Order order = getById(orderId);

        ArrayList<OrderPart> orderParts = new ArrayList<>(getById(orderId).getOrderParts());

        orderParts.remove(orderPartService.getOrderPartById(orderPartId));

        order.setOrderParts(orderParts);
//        order.setOrderStatus(Status.TODO.getOrderStatus());

        orderRepository.save(order);
    }


}
