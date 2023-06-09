package me.project.repository;

import me.project.auth.enums.AppUserRole;
import me.project.entitiy.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional()
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String Email);

    boolean existsByEmail(String Email);

    User getByEmail(String email);

    Page<User> findAllByAppUserRole(AppUserRole employee, Pageable pageable);
}