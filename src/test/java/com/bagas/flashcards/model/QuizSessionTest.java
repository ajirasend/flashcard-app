package com.bagas.flashcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class QuizSessionTest {

    private Deck deckOf(int n) {
        Deck deck = new Deck("Test");
        for (int i = 1; i <= n; i++) {
            deck.addCard(new Card("Q" + i, "A" + i));
        }
        return deck;
    }

    @Test
    void emptyDeckIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new QuizSession(new Deck("Empty")));
    }

    @Test
    void cardsComeInDeckOrderWithoutShuffle() {
        QuizSession session = new QuizSession(deckOf(3));

        assertEquals("Q1", session.getCurrentCard().getQuestion());
        session.answer(true);
        assertEquals("Q2", session.getCurrentCard().getQuestion());
    }

    @Test
    void scoreCountsOnlyCorrectAnswers() {
        QuizSession session = new QuizSession(deckOf(3));

        session.answer(true);
        session.answer(false);
        session.answer(true);

        assertTrue(session.isFinished());
        assertEquals(2, session.getCorrectCount());
        assertEquals(3, session.getTotal());
    }

    @Test
    void currentNumberProgressesAndCapsAtTotal() {
        QuizSession session = new QuizSession(deckOf(2));

        assertEquals(1, session.getCurrentNumber());
        session.answer(true);
        assertEquals(2, session.getCurrentNumber());
        session.answer(true);
        assertEquals(2, session.getCurrentNumber());
    }

    @Test
    void answeringAfterFinishThrows() {
        QuizSession session = new QuizSession(deckOf(1));
        session.answer(true);

        assertThrows(IllegalStateException.class, () -> session.answer(true));
        assertThrows(IllegalStateException.class, session::getCurrentCard);
    }

    @Test
    void shuffleKeepsAllCardsAndDoesNotChangeDeck() {
        Deck deck = deckOf(10);
        QuizSession session = new QuizSession(deck, new Random(42));

        List<String> seen = new ArrayList<>();
        while (!session.isFinished()) {
            seen.add(session.getCurrentCard().getQuestion());
            session.answer(true);
        }

        assertEquals(10, seen.size());
        assertEquals(10, seen.stream().distinct().count());
        assertEquals("Q1", deck.getCards().get(0).getQuestion());
        assertFalse(seen.equals(List.of("Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10")));
    }
}
