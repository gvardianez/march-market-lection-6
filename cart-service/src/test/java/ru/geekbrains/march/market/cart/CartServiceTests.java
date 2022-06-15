package ru.geekbrains.march.market.cart;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.geekbrains.march.market.api.ProductDto;
import ru.geekbrains.march.market.cart.exceptions.FieldValidationException;
import ru.geekbrains.march.market.cart.exceptions.ResourceNotFoundException;
import ru.geekbrains.march.market.cart.integrations.ProductServiceIntegration;
import ru.geekbrains.march.market.cart.services.CartService;

import java.math.BigDecimal;

@SpringBootTest(classes = CartService.class)
public class CartServiceTests {

    @Autowired
    private CartService cartService;

    @MockBean
    private ProductServiceIntegration productServiceIntegration;

    public static ProductDto milkDto;
    public static ProductDto breadDto;
    public static ProductDto cheeseDto;

    @BeforeAll
    public static void initProductsDto() {
        milkDto = new ProductDto(1L, "Молоко", BigDecimal.valueOf(50.00), "Еда");
        breadDto = new ProductDto(2L, "Хлеб", BigDecimal.valueOf(30.00), "Еда");
        cheeseDto = new ProductDto(3L, "Сыр", BigDecimal.valueOf(150.00), "Еда");
    }

    @BeforeEach
    public void clearCart() {
        cartService.clearCart();
    }

    @Test
    public void addToCartTest() {
        Mockito.doReturn(milkDto)
                .when(productServiceIntegration)
                .findById(milkDto.getId());
        cartService.addToCart(milkDto.getId());
        cartService.addToCart(milkDto.getId());
        cartService.addToCart(milkDto.getId());
        Assertions.assertEquals(1, cartService.getCurrentCart().getItems().size());
        Assertions.assertEquals(3, cartService.getCurrentCart().getItems().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(150.00), cartService.getCurrentCart().getTotalPrice());
        Mockito.doReturn(breadDto)
                .when(productServiceIntegration)
                .findById(breadDto.getId());
        cartService.addToCart(breadDto.getId());
        Assertions.assertEquals(2, cartService.getCurrentCart().getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(180.00), cartService.getCurrentCart().getTotalPrice());
    }

    @Test
    public void changeProductQuantityTest() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> cartService.changeProductQuantity(1L, 10));

        Mockito.doReturn(milkDto)
                .when(productServiceIntegration)
                .findById(milkDto.getId());
        cartService.addToCart(milkDto.getId());

        cartService.changeProductQuantity(milkDto.getId(), 2);
        Assertions.assertEquals(3, cartService.getCurrentCart().getItems().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(150.00), cartService.getCurrentCart().getTotalPrice());

        cartService.changeProductQuantity(milkDto.getId(), -1);
        Assertions.assertEquals(2, cartService.getCurrentCart().getItems().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(100.00), cartService.getCurrentCart().getTotalPrice());

        cartService.changeProductQuantity(milkDto.getId(), -5);
        Assertions.assertEquals(0, cartService.getCurrentCart().getItems().size());
        Assertions.assertEquals(BigDecimal.ZERO, cartService.getCurrentCart().getTotalPrice());
    }

    @Test
    public void setProductQuantityTest() {
        Assertions.assertThrows(FieldValidationException.class, () -> cartService.setProductQuantity(cheeseDto.getId(), -5));

        Mockito.doReturn(cheeseDto)
                .when(productServiceIntegration)
                .findById(cheeseDto.getId());
        cartService.addToCart(cheeseDto.getId());

        cartService.setProductQuantity(cheeseDto.getId(), 10);
        Assertions.assertEquals(10, cartService.getCurrentCart().getItems().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(1500.00), cartService.getCurrentCart().getTotalPrice());

        cartService.setProductQuantity(cheeseDto.getId(), 0);
        Assertions.assertEquals(0, cartService.getCurrentCart().getItems().size());
        Assertions.assertEquals(BigDecimal.ZERO, cartService.getCurrentCart().getTotalPrice());
    }

    @Test
    public void removeProductByIdTest() {
        Mockito.doReturn(cheeseDto)
                .when(productServiceIntegration)
                .findById(cheeseDto.getId());
        cartService.addToCart(cheeseDto.getId());

        Mockito.doReturn(milkDto)
                .when(productServiceIntegration)
                .findById(milkDto.getId());
        cartService.addToCart(milkDto.getId());

        cartService.removeProductById(cheeseDto.getId());
        Assertions.assertEquals(1, cartService.getCurrentCart().getItems().size());
    }


}
