package ru.geekbrains.march.market.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.geekbrains.march.market.api.CartDto;
import ru.geekbrains.march.market.api.CartItemDto;
import ru.geekbrains.march.market.core.entities.Category;
import ru.geekbrains.march.market.core.entities.Product;
import ru.geekbrains.march.market.core.integrations.CartServiceIntegration;
import ru.geekbrains.march.market.core.repositories.OrderRepository;
import ru.geekbrains.march.market.core.services.OrderService;
import ru.geekbrains.march.market.core.services.ProductService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = OrderService.class)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private CartServiceIntegration cartServiceIntegration;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductService productService;

    @Test
    public void createNewOrderTest() {
        String username = "Vasya Pupkin";

        CartDto invalidCartDto = new CartDto(Collections.EMPTY_LIST, BigDecimal.ZERO);
        Mockito.doReturn(invalidCartDto)
                .when(cartServiceIntegration)
                .getCart();
        Assertions.assertThrows(IllegalStateException.class, () -> orderService.createNewOrder(username));

        Category category = new Category();
        category.setId(1L);
        category.setTitle("Еда");
        category.setProducts(Collections.emptyList());

        Product productButter = new Product();
        productButter.setId(1L);
        productButter.setTitle("Масло");
        productButter.setPrice(BigDecimal.valueOf(50));
        productButter.setCategory(category);

        Product productBread = new Product();
        productBread.setId(2L);
        productBread.setTitle("Хлеб");
        productBread.setPrice(BigDecimal.valueOf(25));
        productBread.setCategory(category);

        CartDto testCartDto = new CartDto(List.of(new CartItemDto(productButter.getId(), productButter.getTitle(), 3, productButter.getPrice(), productButter.getPrice().multiply(BigDecimal.valueOf(3))), new CartItemDto(productBread.getId(), productBread.getTitle(), 2, productBread.getPrice(), productBread.getPrice().multiply(BigDecimal.valueOf(2)))), BigDecimal.valueOf(200));

        Mockito.doReturn(testCartDto)
                .when(cartServiceIntegration)
                .getCart();

        Mockito.doReturn(Optional.of(productButter))
                .when(productService)
                .findById(productButter.getId());

        Mockito.doReturn(Optional.of(productBread))
                .when(productService)
                .findById(productBread.getId());

        orderService.createNewOrder(username);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any());

    }

}
