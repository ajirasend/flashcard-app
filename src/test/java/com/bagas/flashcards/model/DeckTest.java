package com.bagas.flashcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DeckTest {

    @Test
    void addCardIncreasesSize() {
        Deck deck = new Deck("Java Basics");
        deck.addCard(new Card("What is JVM?", "Java Virtual Machine"));
        assertEquals(1, deck.size());
    }

    @Test
    void blankQuestionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Card("  ", "answer"));
    }

    @Test
    void blankDeckNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(""));
    }
}
