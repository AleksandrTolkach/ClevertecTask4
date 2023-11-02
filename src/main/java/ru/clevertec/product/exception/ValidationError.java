package ru.clevertec.product.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс представляющий описание ошибки в формате входных данных,
 * а также места, в котором расположена ошибка.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ValidationError {

  private String field;
  private String message;
}
