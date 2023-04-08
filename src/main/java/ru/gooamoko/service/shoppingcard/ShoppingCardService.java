package ru.gooamoko.service.shoppingcard;

import ru.gooamoko.model.CardItem;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс для корзины в интернет-магазине
 */
public interface ShoppingCardService {

    /**
     * Добавить покупку в корзину
     *
     * @param userId   идентификатор пользователя.
     * @param cardItem покупка.
     */
    void addItem(UUID userId, CardItem cardItem);

    /**
     * Удалить покупку из корзины
     *
     * @param userId   идентификатор пользователя.
     * @param cardItem покупка.
     */
    void removeItem(UUID userId, CardItem cardItem);

    /**
     * Получить все покупки пользователя
     *
     * @param userId идентификатор пользователя.
     * @return список покупок.
     */
    List<CardItem> getItems(UUID userId);
}
