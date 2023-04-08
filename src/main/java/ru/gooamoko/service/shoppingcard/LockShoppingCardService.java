package ru.gooamoko.service.shoppingcard;

import ru.gooamoko.model.CardItem;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Реализация корзины покупателя с использованием винхронизации
 */
public class LockShoppingCardService implements ShoppingCardService {
    private final Map<UUID, List<CardItem>> cards = new HashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public void addItem(UUID userId, CardItem cardItem) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            cards.compute(userId, (k, v) -> addItemToList(v, cardItem));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeItem(UUID userId, CardItem cardItem) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            cards.compute(userId, (k, v) -> removeItemFromList(v, cardItem));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<CardItem> getItems(UUID userId) {
        List<CardItem> cardItems = new LinkedList<>();
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            List<CardItem> items = cards.get(userId);
            if (items != null && !items.isEmpty()) {
                cardItems.addAll(items);
            }
        } finally {
            lock.unlock();
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
