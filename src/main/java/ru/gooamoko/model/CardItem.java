package ru.gooamoko.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CardItem {
    private final String name;
    private final BigDecimal cost;
    private final int amount;

    public CardItem(String name, BigDecimal cost, int amount) {
        this.name = name;
        this.cost = cost;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardItem cardItem = (CardItem) o;
        return amount == cardItem.amount && name.equals(cardItem.name) && cost.equals(cardItem.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost, amount);
    }
}
