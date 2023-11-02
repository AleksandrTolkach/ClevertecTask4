package ru.clevertec.product.repository.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryProductRepository implements ProductRepository {

    private static final InMemoryProductRepository instance = new InMemoryProductRepository();
    private static final String NULL_PRODUCT = "Не передан продукт";

    private final Map<UUID, Product> productMap = new HashMap<>();

    @Override
    public Optional<Product> findById(UUID uuid) {
        return productMap.entrySet().stream()
            .filter(entry -> entry.getKey().equals(uuid))
            .map(Entry::getValue)
            .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(NULL_PRODUCT);
        }

        UUID uuid = product.getUuid();
        return productMap.compute(uuid != null ? uuid : UUID.randomUUID(),
            (key, value) -> (value != null)
            ? prepareProductToSave(key, value.getCreated(), product)
            : prepareProductToSave(key, LocalDateTime.now(), product));
    }

    @Override
    public void delete(UUID uuid) {
      productMap.remove(uuid);
    }

    public static ProductRepository getInstance() {
        return instance;
    }

    private Product prepareProductToSave(UUID uuid, LocalDateTime created, Product product) {
        return Product.builder()
            .uuid(uuid)
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .created(created)
            .build();
    }
}
