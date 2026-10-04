package com.bagas.flashcards.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A named collection of flashcards.
 */
public class Deck {
    private final String name;
    private final List<Card> cards = new ArrayList<>();

    public Deck(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deck name must not be empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    /** Swaps an existing card for an edited one, keeping its position in the deck. */
    public void replaceCard(Card oldCard, Card newCard) {
        int index = cards.indexOf(oldCard);
        if (index < 0) {
            throw new IllegalArgumentException("Card is not in this deck");
        }
        cards.set(index, newCard);
    }

    /** Returns a read-only view so callers must use addCard/removeCard. */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }
}
