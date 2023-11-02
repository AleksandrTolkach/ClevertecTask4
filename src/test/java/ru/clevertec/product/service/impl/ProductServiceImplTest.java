package ru.clevertec.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.clevertec.product.ValidationTestData.getValidationExceptionList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.clevertec.product.ProductTestData;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.entity.Product.Fields;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.exception.ValidationExceptionList;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.repository.impl.InMemoryProductRepository;
import ru.clevertec.product.validator.ValidationMessage;
import ru.clevertec.product.validator.Validator;
import ru.clevertec.product.validator.impl.ProductDtoDtoValidator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceImplTest {

  private ProductServiceImpl productService;
  @Mock
  private ProductMapper productMapper;
  @Mock
  private ProductRepository productRepository;
  private MockedStatic<InMemoryProductRepository> productRepositoryMockedStatic;
  @Mock
  private Validator<ProductDto> productDtoValidator;
  private MockedStatic<ProductDtoDtoValidator> validatorMockedStatic;
  @Captor
  private ArgumentCaptor<Product> productCaptor;
  private Product product = ProductTestData.builder().build().buildProduct();
  private ProductDto productDto = ProductTestData.builder().build().buildProductDto();
  private InfoProductDto infoProductDto = ProductTestData.builder().build().buildInfoProductDto();
  private UUID id = product.getUuid();
  private String PRODUCT_NOT_FOUND_MESSAGE = String.format("Product with uuid: %s not found", id);

  @BeforeEach
  public void setUp() throws NoSuchMethodException, InvocationTargetException,
      InstantiationException, IllegalAccessException {
    productRepositoryMockedStatic = mockStatic(InMemoryProductRepository.class);
    productRepositoryMockedStatic
        .when(InMemoryProductRepository::getInstance)
        .thenReturn(productRepository);

    validatorMockedStatic = mockStatic(ProductDtoDtoValidator.class);
    validatorMockedStatic
        .when(ProductDtoDtoValidator::getInstance)
        .thenReturn(productDtoValidator);

    Constructor<ProductServiceImpl> privateConstructor = ProductServiceImpl.class
        .getDeclaredConstructor();
    privateConstructor.setAccessible(true);

    productService = privateConstructor.newInstance();
  }

  @AfterEach
  public void cleanUp() {
    productRepositoryMockedStatic.close();
    validatorMockedStatic.close();
  }

  @Test
  public void getTestShouldReturnInfoProductDto() {
    // given
    when(productRepository.findById(id))
        .thenReturn(Optional.of(product));
    when(productMapper.toInfoProductDto(product))
        .thenReturn(infoProductDto);

    InfoProductDto expected = infoProductDto;

    // when
    InfoProductDto actual = productService.get(id);

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void getTestShouldThrowExceptionWhenProductNotExist() {
    // given
    when(productRepository.findById(id))
        .thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> productService.get(id))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage(PRODUCT_NOT_FOUND_MESSAGE);
  }

  @Test
  public void getAllTestShouldReturnInfoProductDtoList() {
    //given
    when(productRepository.findAll())
        .thenReturn(List.of(product));
    when(productMapper.toInfoProductDto(product))
        .thenReturn(infoProductDto);

    List<InfoProductDto> expected = List.of(infoProductDto);

    // when
    List<InfoProductDto> actual = productService.getAll();

    // then
    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  public void createTestShouldCreateProduct() {
    // given
    Product mappedProduct = ProductTestData.builder().withUuid(null).build().buildProduct();

    doNothing()
        .when(productDtoValidator)
        .validate(productDto);
    when(productMapper.toProduct(productDto))
        .thenReturn(mappedProduct);
    when(productRepository.save(productCaptor.capture()))
        .thenReturn(product);

    UUID expectedUuid = id;

    // when
    UUID actualUuid = productService.create(productDto);
    Product actualProduct = productCaptor.getValue();

    // then
    assertThat(actualUuid)
        .isEqualTo(expectedUuid);
    assertThat(actualProduct)
        .hasFieldOrPropertyWithValue(Fields.uuid, null);
  }

  @Test
  public void createTestShouldThrowExceptionWhenProductIsNull() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(ProductDtoDtoValidator.PRODUCT, ValidationMessage.ENTITY_NULL);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.create(productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void createTestShouldThrowExceptionWhenProductHasBlankName() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.name, ValidationMessage.FIELD_NULL);

    String expected = validationExceptionList.getMessage();

    // when
    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    // then
    assertThatThrownBy(() -> productService.create(productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void createTestShouldThrowExceptionWhenProductHasInCorrectName() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.name,
            ValidationMessage.FIELD_WRONG_VALUE);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.create(productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void createTestShouldThrowExceptionWhenProductHasBlankDescription() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.description,
            ValidationMessage.FIELD_NULL);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.create(productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void createTestShouldThrowExceptionWhenProductHasInCorrectDescription() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.description,
            ValidationMessage.FIELD_WRONG_VALUE);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.create(productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void updateTestShouldUpdateProduct() {
    // given
    doNothing()
        .when(productDtoValidator)
        .validate(productDto);
    when(productRepository.findById(id))
        .thenReturn(Optional.of(product));
    when(productMapper.merge(product, productDto))
        .thenReturn(product);
    when(productRepository.save(product))
        .thenReturn(product);

    // when
    productService.update(id, productDto);

    // then
    verify(productRepository, times(1))
        .save(product);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductNotExist() {
    // given
    doNothing()
        .when(productDtoValidator)
        .validate(productDto);
    when(productRepository.findById(id))
        .thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage(PRODUCT_NOT_FOUND_MESSAGE);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductIsNull() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(ProductDtoDtoValidator.PRODUCT, ValidationMessage.ENTITY_NULL);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductHasBlankName() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.name, ValidationMessage.FIELD_NULL);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductHasInCorrectName() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.name,
            ValidationMessage.FIELD_WRONG_VALUE);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductHasBlankDescription() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.description,
            ValidationMessage.FIELD_NULL);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void updateTestShouldThrowExceptionWhenProductHasInCorrectDescription() {
    // given
    ValidationExceptionList validationExceptionList =
        getValidationExceptionList(Fields.description,
            ValidationMessage.FIELD_WRONG_VALUE);

    doThrow(validationExceptionList)
        .when(productDtoValidator)
        .validate(productDto);

    String expected = validationExceptionList.getMessage();

    // when, then
    assertThatThrownBy(() -> productService.update(id, productDto))
        .isInstanceOf(ValidationExceptionList.class)
        .extracting(Throwable::getMessage)
        .isEqualTo(expected);
  }

  @Test
  public void deleteTestShouldDeleteProduct() {
    // given
    doNothing()
        .when(productRepository)
        .delete(id);

    // when
    productService.delete(id);

    // then
    verify(productRepository, times(1))
        .delete(id);
  }
}
