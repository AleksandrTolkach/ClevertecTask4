package ru.clevertec.product.validator;

/**
 * Интерфейс для валидации объектов.
 *
 * @param <I> валидируемый объект.
 */
public interface Validator<I> {

  /**
   * Метод, выполняющий валидацию переданного объекта.
   *
   * @param item объект для валидации.
   */
  void validate(I item);
}
