package me.project.repository;

import me.project.entitiy.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BikeRepository extends JpaRepository<Bike, UUID>, JpaSpecificationExecutor<Bike> {
}