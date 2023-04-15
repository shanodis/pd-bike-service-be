package me.project.repository;

import me.project.entitiy.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, UUID>, JpaSpecificationExecutor<OrderStatus> {
}