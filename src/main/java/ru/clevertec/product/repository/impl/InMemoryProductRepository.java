package ru.clevertec.product.repository.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryProductRepository implements ProductRepository {

    private static final InMemoryProductRepository instance = new InMemoryProductRepository();

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {

    }

    public static ProductRepository getInstance() {
        return instance;
    };
}
