package ru.clevertec.product.mapper.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.mapper.ProductMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapperImpl implements ProductMapper {

    private static final ProductMapper instance = new ProductMapperImpl();

    @Override
    public Product toProduct(ProductDto productDto) {
        return null;
    }

    @Override
    public InfoProductDto toInfoProductDto(Product product) {
        return null;
    }

    @Override
    public Product merge(Product product, ProductDto productDto) {
        return null;
    }

    public static ProductMapper getInstance() {
        return instance;
    }
}
