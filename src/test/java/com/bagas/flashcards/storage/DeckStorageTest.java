package com.bagas.flashcards.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DeckStorageTest {

    private final DeckStorage storage = new DeckStorage();

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoadReturnsSameDeck() throws IOException {
        Deck deck = new Deck("Java Basics");
        deck.addCard(new Card("What is the JVM?", "Java Virtual Machine"));
        deck.addCard(new Card("What is a class?", "A blueprint for objects"));
        Path file = tempDir.resolve("deck.txt");

        storage.save(deck, file);
        Deck loaded = storage.load(file);

        assertEquals("Java Basics", loaded.getName());
        assertEquals(2, loaded.size());
        assertEquals("What is the JVM?", loaded.getCards().get(0).getQuestion());
        assertEquals("A blueprint for objects", loaded.getCards().get(1).getAnswer());
    }

    @Test
    void specialCharactersSurviveRoundTrip() throws IOException {
        Deck deck = new Deck("Tricky");
        deck.addCard(new Card("Tab\there", "Line1\nLine2 and back\\slash"));
        Path file = tempDir.resolve("tricky.txt");

        storage.save(deck, file);
        Card loaded = storage.load(file).getCards().get(0);

        assertEquals("Tab\there", loaded.getQuestion());
        assertEquals("Line1\nLine2 and back\\slash", loaded.getAnswer());
    }

    @Test
    void saveCreatesMissingFolders() throws IOException {
        Path file = tempDir.resolve("a/b/deck.txt");

        storage.save(new Deck("Nested"), file);

        assertEquals("Nested", storage.load(file).getName());
    }

    @Test
    void emptyFileIsRejected() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.write(file, List.of());

        assertThrows(IOException.class, () -> storage.load(file));
    }

    @Test
    void malformedCardLineIsRejected() throws IOException {
        Path file = tempDir.resolve("bad.txt");
        Files.write(file, List.of("Deck", "no tab separator here"));

        assertThrows(IOException.class, () -> storage.load(file));
    }
}
