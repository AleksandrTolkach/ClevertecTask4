package ru.clevertec.product.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Класс для хранения сообщений валидации.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationMessage {

  public static final String FIELD_NULL = "Не передано значение в поле";
  public static final String ENTITY_NULL = "Не передана сущность";
  public static final String FIELD_WRONG_SYMBOL = "Введен недопустимый символ";
  public static final String FIELD_WRONG_VALUE = "Введено неверное значение";
}
