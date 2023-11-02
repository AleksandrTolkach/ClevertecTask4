package ru.clevertec.product.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.mapper.ProductMapperImpl;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.repository.impl.InMemoryProductRepository;
import ru.clevertec.product.service.ProductService;
import ru.clevertec.product.validator.Validator;
import ru.clevertec.product.validator.impl.ProductDtoDtoValidator;

public class ProductServiceImpl implements ProductService {

    private static final ProductService instance = new ProductServiceImpl();

    private final ProductMapper mapper;
    private final ProductRepository productRepository;
    private final Validator<ProductDto> productDtoValidator;

    private ProductServiceImpl() {
        mapper = new ProductMapperImpl();
        productRepository = InMemoryProductRepository.getInstance();
        productDtoValidator = ProductDtoDtoValidator.getInstance();
    }

    @Override
    public InfoProductDto get(UUID uuid) {
        Product product = findProductIfExists(uuid);
        return mapper.toInfoProductDto(product);
    }

    @Override
    public List<InfoProductDto> getAll() {
        return productRepository.findAll().stream()
            .map(mapper::toInfoProductDto)
            .collect(Collectors.toList());
    }

    @Override
    public UUID create(ProductDto productDto) {
        productDtoValidator.validate(productDto);
        Product product = mapper.toProduct(productDto);
        return productRepository.save(product).getUuid();
    }

    @Override
    public void update(UUID uuid, ProductDto productDto) {
        productDtoValidator.validate(productDto);
        Product product = findProductIfExists(uuid);
        product = mapper.merge(product, productDto);
        productRepository.save(product);
    }

    @Override
    public void delete(UUID uuid) {
        productRepository.delete(uuid);
    }

    private Product findProductIfExists(UUID uuid) {
        return productRepository.findById(uuid)
            .orElseThrow(() -> new ProductNotFoundException(uuid));
    }

    public static ProductService getInstance() {
        return instance;
    }
}
