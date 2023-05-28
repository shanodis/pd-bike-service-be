package me.project.repository;

import me.project.dtos.response.order.OrderPaginationResponseDTO;
import me.project.entitiy.Order;
import me.project.entitiy.Service;
import me.project.entitiy.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Order findByUserOrderByCreatedOnDesc(User user);
}