package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.storage.DeckRepository;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/** The application window. Shows one screen at a time: deck list, editor or quiz. */
public class MainFrame extends JFrame implements Navigator {
    private static final String LIST = "list";
    private static final String EDITOR = "editor";
    private static final String QUIZ = "quiz";

    private final DeckRepository repository;
    private final CardLayout layout = new CardLayout();
    private final JPanel root = new JPanel(layout);
    private final DeckListPanel listPanel;
    private JPanel editorPanel;
    private JPanel quizPanel;

    public MainFrame(DeckRepository repository) {
        super("Flashcard App");
        this.repository = repository;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 480);
        setLocationRelativeTo(null);

        listPanel = new DeckListPanel(repository, this);
        root.add(listPanel, LIST);
        add(root);

        showDeckList();
    }

    @Override
    public void showDeckList() {
        listPanel.refresh();
        layout.show(root, LIST);
    }

    @Override
    public void editDeck(Deck deck) {
        if (editorPanel != null) {
            root.remove(editorPanel);
        }
        editorPanel = new EditorPanel(deck, repository, this);
        root.add(editorPanel, EDITOR);
        layout.show(root, EDITOR);
    }

    @Override
    public void quizDeck(Deck deck) {
        if (quizPanel != null) {
            root.remove(quizPanel);
        }
        quizPanel = new QuizPanel(deck, this::showDeckList);
        root.add(quizPanel, QUIZ);
        layout.show(root, QUIZ);
    }
}
