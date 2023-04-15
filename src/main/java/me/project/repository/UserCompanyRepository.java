package me.project.repository;

import me.project.entitiy.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface UserCompanyRepository extends JpaRepository<UserCompany, UUID>, JpaSpecificationExecutor<UserCompany> {
}