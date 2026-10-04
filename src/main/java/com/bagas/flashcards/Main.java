package com.bagas.flashcards;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.view.QuizPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing UI must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flashcard App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.add(new QuizPanel(sampleDeck()));
            frame.setVisible(true);
        });
    }

    // Temporary: replaced by loading real decks once the deck list screen exists
    private static Deck sampleDeck() {
        Deck deck = new Deck("Java Basics");
        deck.addCard(new Card("What does JVM stand for?", "Java Virtual Machine"));
        deck.addCard(new Card("Which keyword prevents a class from being extended?", "final"));
        deck.addCard(new Card("What is the difference between == and equals()?",
                "== compares references; equals() compares content (when overridden)."));
        deck.addCard(new Card("Which collection keeps unique elements only?", "Set (e.g. HashSet)"));
        deck.addCard(new Card("What is polymorphism?",
                "One interface, many implementations: a subclass object can be used as its parent type."));
        return deck;
    }
}
