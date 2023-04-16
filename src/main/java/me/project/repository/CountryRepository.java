package me.project.repository;

import me.project.entitiy.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {
    boolean existsByCountryName(String CountryName);

    Country getCountryByCountryName(String countryName);
}