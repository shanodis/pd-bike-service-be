package me.project.repository;

import me.project.entitiy.BikeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BikeFileRepository extends JpaRepository<BikeFile, UUID>, JpaSpecificationExecutor<BikeFile> {
}