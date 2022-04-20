package ru.geekbrains.march.market.core.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geekbrains.march.market.api.CartDto;
import ru.geekbrains.march.market.api.ProductDto;

@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final WebClient cartServiceWebClient;

    public CartDto getCart() {
        return cartServiceWebClient.get()
                .uri("/api/v1/cart/")
                .retrieve()
                .bodyToMono(CartDto.class)
                .block();
    }

    public void clearCart() {
        cartServiceWebClient.get()
                .uri("/api/v1/cart/clear")
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
