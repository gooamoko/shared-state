package ru.gooamoko.service;

import ru.gooamoko.model.CardItem;

import java.util.*;

/**
 * Сервис корзины покупок.
 */
public class ShoppingCardService {
    private final Map<UUID, List<CardItem>> cards = new HashMap<>();


    public void addItem(UUID userId, CardItem cardItem) {
        cards.compute(userId, (k, v) -> appendList(v, cardItem));
    }

    public List<CardItem> getItems(UUID userId) {
        List<CardItem> cardItems = new LinkedList<>();
        List<CardItem> items = cards.get(userId);
        if (items != null && !items.isEmpty()) {
            cardItems.addAll(items);
        }
        return cardItems;
    }


    private List<CardItem> appendList(List<CardItem> items, CardItem cardItem) {
        if (items == null) {
            items = new LinkedList<>();
        }
        items.add(cardItem);
        return items;
    }
}
