package ru.clevertec.product.mapper.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.clevertec.product.ProductTestData;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.mapper.ProductMapper;

public class ProductMapperImplTest {

  private ProductMapper productMapper = ProductMapperImpl.getInstance();
  private Product product = ProductTestData.builder().build().buildProduct();
  private InfoProductDto infoProductDto = ProductTestData.builder().build().buildInfoProductDto();
  private ProductDto productDto = ProductTestData.builder().build().buildProductDto();

  @Test
  public void toProductTestShouldReturnProduct() {
    // given
    Product expected = ProductTestData.builder()
        .withUuid(null)
        .withCreated(null)
        .build()
        .buildProduct();

    // when
    Product actual = productMapper.toProduct(productDto);

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void toInfoProductTestDtoShouldReturnInfoProductDto() {
    // given
    InfoProductDto expected = infoProductDto;

    // when
    InfoProductDto actual = productMapper.toInfoProductDto(product);

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void mergeTestTestShouldReturnProduct() {
    // given
    Product expected = product;

    // when
    Product actual = productMapper.merge(product, productDto);

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }
}
