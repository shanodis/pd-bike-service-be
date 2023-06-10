package me.project.runners;

import me.project.entitiy.OrderStatus;
import me.project.enums.Status;
import me.project.repository.OrderStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Status Seeder")
class OrderStatusSeederTest {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusSeeder orderStatusSeeder;

    @Test
    @DisplayName("Should log the seeded order statuses and completion message")
    void runShouldLogSeededOrderStatusesAndCompletion() {
        when(orderStatusRepository.existsById(any(UUID.class))).thenReturn(false);

        orderStatusSeeder.run(mock(ApplicationArguments.class));

        verify(orderStatusRepository, times(Status.values().length)).save(any(OrderStatus.class));
        verify(orderStatusRepository, times(Status.values().length)).existsById(any(UUID.class));
        verifyNoMoreInteractions(orderStatusRepository);
    }

    @Test
    @DisplayName("Should seed order statuses when they do not exist in the repository")
    void runShouldSeedOrderStatusesWhenNotExists() {
        when(orderStatusRepository.existsById(any(UUID.class))).thenReturn(false);

        orderStatusSeeder.run(null);

        verify(orderStatusRepository, times(Status.values().length)).save(any(OrderStatus.class));
        verify(orderStatusRepository, times(Status.values().length)).existsById(any(UUID.class));
    }

    @Test
    @DisplayName("Should not seed order statuses when they already exist in the repository")
    void runShouldNotSeedOrderStatusesWhenExists() {
        when(orderStatusRepository.existsById(any(UUID.class))).thenReturn(true);

        orderStatusSeeder.run(mock(ApplicationArguments.class));

        verify(orderStatusRepository, never()).save(any(OrderStatus.class));
        verify(orderStatusRepository, never()).saveAll(anyIterable());
        verify(orderStatusRepository, never()).delete(any(OrderStatus.class));
        verify(orderStatusRepository, never()).deleteAll(anyIterable());
        verifyNoMoreInteractions(orderStatusRepository);
    }

}