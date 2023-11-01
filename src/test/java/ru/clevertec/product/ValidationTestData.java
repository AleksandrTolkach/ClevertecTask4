package ru.clevertec.product;

import ru.clevertec.product.exception.ValidationError;
import ru.clevertec.product.exception.ValidationExceptionList;

public class ValidationTestData {

  public static ValidationExceptionList getValidationExceptionList(String fieldName, String message) {
    ValidationExceptionList validationExceptionList = new ValidationExceptionList();
    validationExceptionList.addError(new ValidationError(fieldName, message));
    return validationExceptionList;
  }
}
