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

    @Test
    void replaceCardKeepsPosition() {
        Deck deck = new Deck("Test");
        Card first = new Card("Q1", "A1");
        Card second = new Card("Q2", "A2");
        deck.addCard(first);
        deck.addCard(second);

        deck.replaceCard(first, new Card("Q1 edited", "A1 edited"));

        assertEquals("Q1 edited", deck.getCards().get(0).getQuestion());
        assertEquals("Q2", deck.getCards().get(1).getQuestion());
    }

    @Test
    void replaceUnknownCardThrows() {
        Deck deck = new Deck("Test");

        assertThrows(IllegalArgumentException.class,
                () -> deck.replaceCard(new Card("Q", "A"), new Card("Q2", "A2")));
    }
}
