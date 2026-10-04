package com.bagas.flashcards;

import com.formdev.flatlaf.FlatLightLaf;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.storage.DeckRepository;
import com.bagas.flashcards.view.MainFrame;

import java.io.IOException;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Decks live in the user's home folder, outside the project, so they never end up in git
        Path decksFolder = Path.of(System.getProperty("user.home"), ".flashcard-app", "decks");
        DeckRepository repository = new DeckRepository(decksFolder);
        seedSampleDeckIfEmpty(repository);

        // Must run before any Swing component is created
        FlatLightLaf.setup();

        // Swing UI must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame(repository).setVisible(true));
    }

    /** On first run, gives the user one deck to try out. */
    private static void seedSampleDeckIfEmpty(DeckRepository repository) {
        try {
            if (repository.loadAll().isEmpty()) {
                repository.save(sampleDeck());
            }
        } catch (IOException e) {
            System.err.println("Could not create sample deck: " + e.getMessage());
        }
    }

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
