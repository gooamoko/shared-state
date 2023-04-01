package ru.gooamoko.service;

import ru.gooamoko.model.CardItem;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Сервис корзины покупок.
 */
public class ShoppingCardService {
    private final ConcurrentMap<UUID, List<CardItem>> cards = new ConcurrentHashMap<>();


    public void addItem(UUID userId, CardItem cardItem) {
        cards.compute(userId, (k, v) -> addItemToList(v, cardItem));
    }

    public void removeItem(UUID userId, CardItem cardItem) {
        cards.compute(userId, (k, v) -> removeItemFromList(v, cardItem));
    }

    public List<CardItem> getItems(UUID userId) {
        List<CardItem> cardItems = new LinkedList<>();
        List<CardItem> items = cards.get(userId);
        if (items != null && !items.isEmpty()) {
            cardItems.addAll(items);
        }
        return cardItems;
    }


    private List<CardItem> addItemToList(List<CardItem> items, CardItem cardItem) {
        if (items == null) {
            items = new CopyOnWriteArrayList<>();
        }
        items.add(cardItem);
        return items;
    }

    private List<CardItem> removeItemFromList(List<CardItem> items, CardItem cardItem) {
        if (items == null) {
            items = new CopyOnWriteArrayList<>();
        }

        if (!items.isEmpty()) {
            items.remove(cardItem);
        }

        return items;
    }
}
