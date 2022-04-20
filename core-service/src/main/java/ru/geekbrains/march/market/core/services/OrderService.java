package ru.geekbrains.march.market.core.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.march.market.api.CartDto;
import ru.geekbrains.march.market.core.entities.Order;
import ru.geekbrains.march.market.core.entities.OrderItem;
import ru.geekbrains.march.market.core.exceptions.ResourceNotFoundException;
import ru.geekbrains.march.market.core.integrations.CartServiceIntegration;
import ru.geekbrains.march.market.core.repositories.OrderRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartServiceIntegration cartServiceIntegration;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public Order createNewOrder(String username) {
        CartDto cartDto = cartServiceIntegration.getCart();
        if (cartDto.getItems().size() == 0) throw new IllegalStateException("Cart is Empty");
        List<OrderItem> orderItems = new ArrayList<>();
        cartDto.getItems().forEach(cartItemDto ->
                orderItems.add(new OrderItem(productService.findById(cartItemDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found, id = " + cartItemDto.getProductId())), cartItemDto.getQuantity())));
        Order order = orderRepository.save(new Order(username, orderItems, cartDto.getTotalPrice()));
        cartServiceIntegration.clearCart();
        return order;
    }

    public Order getOrder(Long id) {
        return orderRepository.getById(id);
    }

}
