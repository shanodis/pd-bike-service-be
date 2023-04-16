package me.project.service.order;

import me.project.entitiy.Order;
import me.project.entitiy.User;
import me.project.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;

    public Order getLatestByUser(User user) {
        return orderRepository.findByUserOrderByCreatedOnDesc(user);
    }
}
