package me.project.runners;

import me.project.enums.Status;
import me.project.repository.OrderStatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@AllArgsConstructor
public class OrderStatusSeeder implements ApplicationRunner {

    private final OrderStatusRepository orderStatusRepository;

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
