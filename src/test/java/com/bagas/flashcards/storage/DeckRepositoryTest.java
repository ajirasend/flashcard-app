package com.bagas.flashcards.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DeckRepositoryTest {

    @TempDir
    Path tempDir;

    private Deck deck(String name, int cards) {
        Deck deck = new Deck(name);
        for (int i = 1; i <= cards; i++) {
            deck.addCard(new Card("Q" + i, "A" + i));
        }
        return deck;
    }

    @Test
    void loadAllReturnsEmptyWhenFolderDoesNotExist() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir.resolve("missing"));

        assertTrue(repo.loadAll().isEmpty());
    }

    @Test
    void savedDecksAreLoadedSortedByName() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);
        repo.save(deck("banana", 1));
        repo.save(deck("Apple", 2));

        List<Deck> loaded = repo.loadAll();

        assertEquals(2, loaded.size());
        assertEquals("Apple", loaded.get(0).getName());
        assertEquals(2, loaded.get(0).size());
        assertEquals("banana", loaded.get(1).getName());
    }

    @Test
    void savingSameDeckTwiceOverwrites() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);
        Deck deck = deck("Java", 1);
        repo.save(deck);
        deck.addCard(new Card("Q2", "A2"));
        repo.save(deck);

        List<Deck> loaded = repo.loadAll();

        assertEquals(1, loaded.size());
        assertEquals(2, loaded.get(0).size());
    }

    @Test
    void deleteRemovesDeck() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);
        Deck deck = deck("Temp", 1);
        repo.save(deck);

        repo.delete(deck);

        assertTrue(repo.loadAll().isEmpty());
        assertFalse(repo.exists("Temp"));
    }

    @Test
    void existsIgnoresCaseAndSymbols() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);
        repo.save(deck("My Deck!", 1));

        assertTrue(repo.exists("my deck"));
        assertFalse(repo.exists("other"));
    }

    @Test
    void namesWithPathCharactersStayInsideFolder() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);

        repo.save(deck("../../evil/name", 1));

        assertEquals(1, repo.loadAll().size());
        try (var files = Files.list(tempDir)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    void corruptFilesAreSkipped() throws IOException {
        DeckRepository repo = new DeckRepository(tempDir);
        repo.save(deck("Good", 1));
        Files.write(tempDir.resolve("broken.txt"), List.of("Broken", "no tab here"));

        List<Deck> loaded = repo.loadAll();

        assertEquals(1, loaded.size());
        assertEquals("Good", loaded.get(0).getName());
    }
}
