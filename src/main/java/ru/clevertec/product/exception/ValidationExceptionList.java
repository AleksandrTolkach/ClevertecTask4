package ru.clevertec.product.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Класс представляющий исключение, выбрасываемое при идентификации ошибок в формате входных данных.
 */
@NoArgsConstructor
public class ValidationExceptionList extends RuntimeException {

  private Map<String, List<String>> errorMap = new HashMap<>();

  /**
   * Метод для добавления ошибки в исключение.
   *
   * @param error идентифицированная ошибка.
   */
  public void addError(ValidationError error) {
    String field = error.getField();
    List<String> fieldList = errorMap.get(field);

    if (fieldList == null) {
      errorMap.put(field, new ArrayList<>(List.of(error.getMessage())));
    } else {
      fieldList.add(error.getMessage());
    }
  }

  /**
   * Метод для проверки существования ошибок валидации.
   *
   * @return возвращает true если ошибки есть и false, если нет.
   */
  public boolean isEmpty() {
    return errorMap.isEmpty();
  }

  @Override
  public String getMessage() {
    return errorMap.toString();
  }
}
