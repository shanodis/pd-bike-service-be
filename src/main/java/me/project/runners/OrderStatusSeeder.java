package me.project.runners;

import me.project.enums.Status;
import me.project.repository.OrderStatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Klasa OrderStatusSeeder implementuje interfejs ApplicationRunner i jest komponentem zarządzanym przez Spring.
 * Służy do inicjalnego zasilania tabeli statusów zamówień w bazie danych.
 * Klasa korzysta z repozytorium OrderStatusRepository do zapisu nowych statusów zamówień.
 *
 * Adnotacja @Slf4j generuje logger do rejestrowania informacji.
 * Adnotacja @Component oznacza tę klasę jako komponent, który jest zarządzany przez Spring.
 * Adnotacja @AllArgsConstructor generuje konstruktor przyjmujący wszystkie zależności.
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrderStatusSeeder implements ApplicationRunner {

    private final OrderStatusRepository orderStatusRepository;

    /**
     * Metoda run jest wywoływana przy uruchomieniu aplikacji.
     * Iteruje przez wszystkie wartości zdefiniowane w enumie Status i sprawdza, czy status nie istnieje już w bazie danych.
     * Jeśli status nie istnieje, jest zapisywany do repozytorium i rejestrowane jest informacyjne logowanie.
     * Po zakończeniu zasilania tabeli statusów zamówień, logowane jest zakończenie operacji.
     *
     * @param args obiekt ApplicationArguments reprezentujący argumenty uruchomieniowe aplikacji
     */
    @Override
    public void run(ApplicationArguments args) {

        Arrays.stream(Status.values())
                .forEach(status -> {
                            if (!orderStatusRepository.existsById(status.getOrderStatus().getOrderStatusId())) {
                                orderStatusRepository.save(status.getOrderStatus());
                                log.info(String.format("Order Status %s has been seeded", status.getOrderStatus().getOrderStatusName()));
                            }
                        }
                );

        log.info("Order Status seeding completed . App Ready");

    }

}
