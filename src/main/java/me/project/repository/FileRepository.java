package me.project.repository;

import me.project.entitiy.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID>, JpaSpecificationExecutor<File> {
    boolean existsByFileName(String fileName);

    File getByFileName(String concatedFileName);
}