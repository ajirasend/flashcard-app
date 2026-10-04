package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.storage.DeckRepository;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import java.awt.Insets;

/** Home screen: lists saved decks and lets the user study, edit, create or delete them. */
public class DeckListPanel extends JPanel {
    private final DeckRepository repository;
    private final Navigator navigator;
    private final DefaultListModel<Deck> model = new DefaultListModel<>();
    private final JList<Deck> list = new JList<>(model);

    public DeckListPanel(DeckRepository repository, Navigator navigator) {
        this.repository = repository;
        this.navigator = navigator;

        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JLabel title = new JLabel("My Decks", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        content.add(title, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(list.getFont().deriveFont(Font.PLAIN, 16f));
        list.setFixedCellHeight(44);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(l, value, index, selected, focus);
                Deck deck = (Deck) value;
                label.putClientProperty("html.disable", Boolean.TRUE); // show user text literally
                label.setText(deck.getName() + "  (" + deck.size() + (deck.size() == 1 ? " card)" : " cards)"));
                label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                return label;
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    studySelected();
                }
            }
        });
        content.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton studyButton = new JButton("Study");
        JButton editButton = new JButton("Edit cards");
        JButton newButton = new JButton("New deck");
        JButton deleteButton = new JButton("Delete");
        studyButton.addActionListener(e -> studySelected());
        editButton.addActionListener(e -> editSelected());
        newButton.addActionListener(e -> createDeck());
        deleteButton.addActionListener(e -> deleteSelected());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        for (JButton b : new JButton[] {studyButton, editButton, newButton, deleteButton}) {
            b.setFont(b.getFont().deriveFont(Font.PLAIN, 15f));
            b.setMargin(new Insets(8, 16, 8, 16));
            buttons.add(b);
        }
        content.add(buttons, BorderLayout.SOUTH);

        add(new MaxWidthPanel(content, 750), BorderLayout.CENTER);
    }

    /** Reloads the deck list from disk. */
    public void refresh() {
        model.clear();
        try {
            for (Deck deck : repository.loadAll()) {
                model.addElement(deck);
            }
        } catch (IOException e) {
            showError("Could not load decks: " + e.getMessage());
        }
    }

    private Deck selectedDeck() {
        Deck deck = list.getSelectedValue();
        if (deck == null) {
            JOptionPane.showMessageDialog(this, "Select a deck first.");
        }
        return deck;
    }

    private void studySelected() {
        Deck deck = selectedDeck();
        if (deck == null) {
            return;
        }
        if (deck.size() == 0) {
            JOptionPane.showMessageDialog(this, "This deck has no cards yet. Use \"Edit cards\" to add some.");
            return;
        }
        navigator.quizDeck(deck);
    }

    private void editSelected() {
        Deck deck = selectedDeck();
        if (deck != null) {
            navigator.editDeck(deck);
        }
    }

    private void createDeck() {
        String name = JOptionPane.showInputDialog(this, "Deck name:");
        if (name == null) {
            return; // cancelled
        }
        try {
            Deck deck = new Deck(name);
            if (repository.exists(deck.getName())) {
                JOptionPane.showMessageDialog(this, "A deck with a similar name already exists.");
                return;
            }
            repository.save(deck);
            navigator.editDeck(deck);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IOException e) {
            showError("Could not save deck: " + e.getMessage());
        }
    }

    private void deleteSelected() {
        Deck deck = selectedDeck();
        if (deck == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + deck.getName() + "\" and its " + deck.size() + " cards?",
                "Delete deck", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            repository.delete(deck);
            refresh();
        } catch (IOException e) {
            showError("Could not delete deck: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
