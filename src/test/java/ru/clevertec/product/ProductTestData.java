package ru.clevertec.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;

@Data
@Builder(setterPrefix = "with")
@Accessors(fluent = true, chain = true)
@FieldNameConstants
public class ProductTestData {

  @Builder.Default
  private UUID uuid = UUID.fromString("c2796e63-4858-4a5a-a28e-76f81e27c58e");
  @Builder.Default
  private String name =  "Dog";
  @Builder.Default
  private String description = "Hot";
  @Builder.Default
  private BigDecimal price = BigDecimal.TEN;
  @Builder.Default
  private LocalDateTime created = LocalDateTime.MIN;

  public Product buildProduct() {
    return Product.builder()
        .uuid(uuid)
        .name(name)
        .description(description)
        .price(price)
        .created(created)
        .build();
  }

  public ProductDto buildProductDto() {
    return new ProductDto(name, description, price);
  }

  public InfoProductDto buildInfoProductDto() {
    return new InfoProductDto(uuid, name, description, price);
  }
}
