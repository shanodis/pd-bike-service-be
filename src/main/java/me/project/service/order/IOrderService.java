package me.project.service.order;

import me.project.entitiy.Order;
import me.project.entitiy.User;

public interface IOrderService {
    Order getLatestByUser(User user);
}
