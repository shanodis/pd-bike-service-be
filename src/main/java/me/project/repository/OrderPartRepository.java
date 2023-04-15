package me.project.repository;

import me.project.entitiy.OrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderPartRepository extends JpaRepository<OrderPart, UUID>, JpaSpecificationExecutor<OrderPart> {
}