package com.bagas.flashcards.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Tracks the progress of one run through a deck: which card is current and the score.
 * Contains no UI code, so it is easy to unit test.
 */
public class QuizSession {
    private final List<Card> cards;
    private int index = 0;
    private int correctCount = 0;

    /** Quiz in the deck's original order. */
    public QuizSession(Deck deck) {
        this(deck, null);
    }

    /**
     * @param random if not null, the cards are shuffled with it (pass a seeded Random in tests)
     */
    public QuizSession(Deck deck, Random random) {
        if (deck.size() == 0) {
            throw new IllegalArgumentException("Cannot quiz an empty deck");
        }
        this.cards = new ArrayList<>(deck.getCards());
        if (random != null) {
            Collections.shuffle(cards, random);
        }
    }

    public boolean isFinished() {
        return index >= cards.size();
    }

    /** @throws IllegalStateException if the quiz is already finished */
    public Card getCurrentCard() {
        if (isFinished()) {
            throw new IllegalStateException("Quiz is finished");
        }
        return cards.get(index);
    }

    /** Records the self-graded result for the current card and moves to the next one. */
    public void answer(boolean knewIt) {
        if (isFinished()) {
            throw new IllegalStateException("Quiz is finished");
        }
        if (knewIt) {
            correctCount++;
        }
        index++;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getTotal() {
        return cards.size();
    }

    /** 1-based number of the current card, capped at the total once finished. */
    public int getCurrentNumber() {
        return Math.min(index + 1, cards.size());
    }
}
