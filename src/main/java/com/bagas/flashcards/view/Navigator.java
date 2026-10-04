package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Deck;

/** Lets screens ask the main window to switch to another screen. */
public interface Navigator {
    void showDeckList();

    void editDeck(Deck deck);

    void quizDeck(Deck deck);
}
