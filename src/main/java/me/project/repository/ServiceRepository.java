package me.project.repository;

import me.project.entitiy.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID>, JpaSpecificationExecutor<Service> {
    Page<Service> findAllByServiceName(String serviceName, Pageable pageable);

    Optional<Service> findByServiceName(String serviceName);
}