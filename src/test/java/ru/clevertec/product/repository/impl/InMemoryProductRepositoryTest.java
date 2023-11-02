package ru.clevertec.product.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.clevertec.product.ProductTestData;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.entity.Product.Fields;
import ru.clevertec.product.repository.ProductRepository;

public class InMemoryProductRepositoryTest {

  private static ProductRepository productRepository = InMemoryProductRepository.getInstance();
  private Product product;
  private UUID id;

  @BeforeEach
  public void setUp() {
    Product product = ProductTestData.builder()
        .withUuid(null)
        .withCreated(null)
        .build()
        .buildProduct();

    this.product = productRepository.save(product);
    id = this.product.getUuid();
  }

  @AfterEach
  public void cleanUp() {
    productRepository.findAll().stream()
        .map(Product::getUuid)
        .forEach(productRepository::delete);
  }

  @ParameterizedTest
  @MethodSource("productArgumentProvider")
  public void findByIdTestShouldReturnProduct(UUID id, Product expected) {
    // when
    Product actual = productRepository.findById(id).orElse(null);

    //then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void findAllTestShouldReturnProductList() {
    // given
    List<Product> expected = List.of(product);

    // when
    List<Product> actual = productRepository.findAll();

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void saveTestShouldSaveProduct() {
    // given
    Product expected = this.product;

    // when
    Product actual = productRepository.save(product);

    // then
    assertThat(actual)
        .hasFieldOrProperty(Fields.uuid).isNotNull()
        .hasFieldOrPropertyWithValue(Fields.name, expected.getName())
        .hasFieldOrPropertyWithValue(Fields.description, expected.getDescription())
        .hasFieldOrPropertyWithValue(Fields.price, expected.getPrice())
        .hasFieldOrProperty(Fields.created).isNotNull();
  }

  @Test
  public void saveTestShouldThrowExceptionWhenProductIsNull() {
    // when, then
    assertThatThrownBy(() -> productRepository.save(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void deleteTestShouldDeleteProduct() {
    // given
    Product expected = null;

    // when
    productRepository.delete(id);
    Product actual = productRepository.findById(id).orElse(null);

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  public static Stream<Arguments> productArgumentProvider() {
    Product product = ProductTestData.builder().withUuid(null).withCreated(null).build()
        .buildProduct();

    product = productRepository.save(product);

    return Stream.of(
        arguments(product.getUuid(), product),
        arguments(UUID.randomUUID(), null),
        arguments(null, null)
    );
  }
}
