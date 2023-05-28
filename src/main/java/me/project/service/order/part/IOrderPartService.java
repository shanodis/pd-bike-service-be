package me.project.service.order.part;

import me.project.dtos.request.orderPart.OrderPartCreateDTO;
import me.project.entitiy.OrderPart;

import java.util.UUID;

public interface IOrderPartService {

    OrderPart getOrderPartById(UUID orderPartId);

    UUID createOrderPart(UUID orderId, OrderPartCreateDTO request);

}
