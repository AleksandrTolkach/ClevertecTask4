package ru.clevertec.product.validator.impl;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product.Fields;
import ru.clevertec.product.exception.ValidationError;
import ru.clevertec.product.exception.ValidationExceptionList;
import ru.clevertec.product.validator.ValidationMessage;
import ru.clevertec.product.validator.Validator;

/**
 * Класс для валидации ProductDto.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDtoDtoValidator implements Validator<ProductDto> {

  public static final String PRODUCT = "ProductDto";
  private static final String NAME_REGEX = "(^[\\p{IsCyrillic}+| ]{5,10}$)";
  private static final String DESCRIPTION_REGEX = "(^[\\p{IsCyrillic}+| ]{10,30}$)";
  private static final Validator<ProductDto> instance = new ProductDtoDtoValidator();

  @Override
  public void validate(ProductDto productDto) {
    ValidationExceptionList validationExceptionList = new ValidationExceptionList();

    if (productDto == null) {
      validationExceptionList.addError(new ValidationError(PRODUCT, ValidationMessage.ENTITY_NULL));
      throw validationExceptionList;
    }

    String name = productDto.name();
    regExValidate(validationExceptionList, name, Fields.name, NAME_REGEX);

    String productDtoDescription = productDto.description();
    regExValidate(validationExceptionList, productDtoDescription, Fields.description,
        DESCRIPTION_REGEX);

    BigDecimal price = productDto.price();
    if (price == null) {
      validationExceptionList.addError(new ValidationError(Fields.price,
          ValidationMessage.FIELD_NULL));
    } else if (price.compareTo(BigDecimal.ZERO) < 0) {
      validationExceptionList.addError(new ValidationError(Fields.price,
          ValidationMessage.FIELD_WRONG_VALUE));
    }

    if (!validationExceptionList.isEmpty()) {
      throw validationExceptionList;
    }
  }

  private void regExValidate(ValidationExceptionList validationExceptionList, String name,
      String nameField, String nameRegex) {
    if (StringUtils.isBlank(name)) {
      validationExceptionList.addError(new ValidationError(nameField,
          ValidationMessage.FIELD_NULL));
    } else {
      Pattern namePattern = Pattern.compile(nameRegex);
      Matcher nameMatcher = namePattern.matcher(name);

      if (!nameMatcher.find()) {
        validationExceptionList.addError(new ValidationError(nameField,
            ValidationMessage.FIELD_WRONG_SYMBOL));
      }
    }
  }

  public static Validator<ProductDto> getInstance() {
    return instance;
  }
}
