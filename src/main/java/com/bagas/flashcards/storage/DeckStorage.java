package com.bagas.flashcards.storage;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads a {@link Deck} as a plain text file.
 *
 * <p>File format:
 * <pre>
 * Deck name
 * question&lt;TAB&gt;answer
 * question&lt;TAB&gt;answer
 * </pre>
 * Tabs, newlines and backslashes inside text are escaped (\t, \n, \\) so they
 * can never break the format.
 */
public class DeckStorage {

    private static final String SEPARATOR = "\t";

    public void save(Deck deck, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(escape(deck.getName()));
        for (Card card : deck.getCards()) {
            lines.add(escape(card.getQuestion()) + SEPARATOR + escape(card.getAnswer()));
        }

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, lines);
    }

    /**
     * @throws IOException if the file cannot be read or is empty/corrupt
     */
    public Deck load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty()) {
            throw new IOException("Deck file is empty: " + file);
        }

        Deck deck;
        try {
            deck = new Deck(unescape(lines.get(0)));
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid deck name in file: " + file, e);
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue; // tolerate blank lines
            }
            String[] parts = line.split(SEPARATOR, 2);
            if (parts.length != 2) {
                throw new IOException("Malformed card on line " + (i + 1) + " of " + file);
            }
            try {
                deck.addCard(new Card(unescape(parts[0]), unescape(parts[1])));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid card on line " + (i + 1) + " of " + file, e);
            }
        }
        return deck;
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private static String unescape(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                switch (next) {
                    case 't' -> sb.append('\t');
                    case 'n' -> sb.append('\n');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(c).append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
