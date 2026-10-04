package com.bagas.flashcards.storage;

import com.bagas.flashcards.model.Deck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages a folder of deck files: one text file per deck.
 * Builds on {@link DeckStorage}, which handles a single file.
 */
public class DeckRepository {
    private static final String EXTENSION = ".txt";

    private final Path directory;
    private final DeckStorage storage = new DeckStorage();

    public DeckRepository(Path directory) {
        this.directory = directory;
    }

    /** Loads every readable deck, sorted by name. Unreadable files are skipped. */
    public List<Deck> loadAll() throws IOException {
        List<Deck> decks = new ArrayList<>();
        if (!Files.isDirectory(directory)) {
            return decks;
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = stream.filter(f -> f.getFileName().toString().endsWith(EXTENSION)).toList();
        }
        for (Path file : files) {
            try {
                decks.add(storage.load(file));
            } catch (IOException e) {
                System.err.println("Skipping unreadable deck " + file + ": " + e.getMessage());
            }
        }
        decks.sort(Comparator.comparing(d -> d.getName().toLowerCase()));
        return decks;
    }

    public void save(Deck deck) throws IOException {
        storage.save(deck, fileFor(deck.getName()));
    }

    public void delete(Deck deck) throws IOException {
        Files.deleteIfExists(fileFor(deck.getName()));
    }

    /** True if a deck with this name (or a name that maps to the same file) is already saved. */
    public boolean exists(String name) {
        return Files.exists(fileFor(name));
    }

    /** "My Deck!" and "my deck" map to the same file name, "my_deck.txt". */
    private Path fileFor(String deckName) {
        String base = deckName.trim().toLowerCase()
                .replaceAll("[^\\p{L}\\p{Nd}]+", "_")
                .replaceAll("^_+|_+$", "");
        if (base.isEmpty()) {
            base = "deck";
        }
        return directory.resolve(base + EXTENSION);
    }
}
