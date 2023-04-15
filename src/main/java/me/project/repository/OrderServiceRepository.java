package me.project.repository;

import me.project.entitiy.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderServiceRepository extends JpaRepository<OrderService, UUID>, JpaSpecificationExecutor<OrderService> {
}