package ru.gooamoko.service.shoppingcard;

import ru.gooamoko.model.CardItem;

import java.util.*;

/**
 * Реализация корзины покупателя с использованием винхронизации
 */
public class SynchronizedShoppingCardService implements ShoppingCardService {
    private final Map<UUID, List<CardItem>> cards = new HashMap<>();

    @Override
    public synchronized void addItem(UUID userId, CardItem cardItem) {
        cards.compute(userId, (k, v) -> addItemToList(v, cardItem));
    }

    @Override
    public synchronized void removeItem(UUID userId, CardItem cardItem) {
        cards.compute(userId, (k, v) -> removeItemFromList(v, cardItem));
    }

    @Override
    public List<CardItem> getItems(UUID userId) {
        List<CardItem> cardItems = new LinkedList<>();
        synchronized (this) {
            List<CardItem> items = cards.get(userId);
            if (items != null && !items.isEmpty()) {
                cardItems.addAll(items);
            }
        }
        return cardItems;
    }


    private List<CardItem> addItemToList(List<CardItem> items, CardItem cardItem) {
        if (items == null) {
            items = new LinkedList<>();
        }
        items.add(cardItem);
        return items;
    }

    private List<CardItem> removeItemFromList(List<CardItem> items, CardItem cardItem) {
        if (items == null) {
            items = new LinkedList<>();
        }

        if (!items.isEmpty()) {
            items.remove(cardItem);
        }

        return items;
    }
}
