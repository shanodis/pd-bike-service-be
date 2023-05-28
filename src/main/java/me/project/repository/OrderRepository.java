package me.project.repository;

import me.project.entitiy.Order;
import me.project.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Order findFirstByUserOrderByCreatedOnDesc(User user);
}