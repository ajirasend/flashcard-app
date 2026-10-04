package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.model.QuizSession;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Shows one flashcard at a time: question, flip to reveal the answer,
 * then self-grade. A score screen is shown when the deck is finished.
 */
public class QuizPanel extends JPanel {
    private static final String QUIZ_VIEW = "quiz";
    private static final String RESULT_VIEW = "result";

    private final Deck deck;
    private final Runnable onExit;
    private QuizSession session;
    private boolean showingAnswer;

    private final CardLayout views = new CardLayout();
    private final JLabel progressLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel sideLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel cardText = new JLabel("", SwingConstants.CENTER);
    private final JButton flipButton = new JButton("Flip");
    private final JButton knewButton = new JButton("I knew it");
    private final JButton missedButton = new JButton("I didn't");
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);

    /**
     * @param onExit called when the user leaves the quiz (e.g. to go back to the deck list)
     */
    public QuizPanel(Deck deck, Runnable onExit) {
        this.deck = deck;
        this.onExit = onExit;
        setLayout(views);
        add(buildQuizView(), QUIZ_VIEW);
        add(buildResultView(), RESULT_VIEW);
        startQuiz();
    }

    private JPanel buildQuizView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        JButton backButton = new JButton("< Back");
        backButton.addActionListener(e -> onExit.run());
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.PLAIN, 14f));
        header.add(backButton, BorderLayout.WEST);
        header.add(progressLabel, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        sideLabel.setFont(sideLabel.getFont().deriveFont(Font.BOLD, 12f));
        sideLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cardText.setFont(cardText.getFont().deriveFont(Font.PLAIN, 22f));
        cardPanel.add(sideLabel, BorderLayout.NORTH);
        cardPanel.add(cardText, BorderLayout.CENTER);
        panel.add(cardPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(flipButton);
        buttons.add(missedButton);
        buttons.add(knewButton);
        panel.add(buttons, BorderLayout.SOUTH);

        flipButton.addActionListener(e -> flip());
        knewButton.addActionListener(e -> grade(true));
        missedButton.addActionListener(e -> grade(false));
        return panel;
    }

    private JPanel buildResultView() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

        JLabel title = new JLabel("Quiz finished!", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.PLAIN, 20f));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton again = new JButton("Try again");
        again.setAlignmentX(Component.CENTER_ALIGNMENT);
        again.addActionListener(e -> startQuiz());

        JButton back = new JButton("Back to decks");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> onExit.run());

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(scoreLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(again);
        panel.add(Box.createVerticalStrut(10));
        panel.add(back);
        return panel;
    }

    private void startQuiz() {
        session = new QuizSession(deck, new Random());
        views.show(this, QUIZ_VIEW);
        showQuestion();
    }

    private void showQuestion() {
        Card card = session.getCurrentCard();
        showingAnswer = false;
        progressLabel.setText("Card " + session.getCurrentNumber() + " of " + session.getTotal());
        sideLabel.setText("QUESTION");
        cardText.setText(wrap(card.getQuestion()));
        flipButton.setEnabled(true);
        knewButton.setEnabled(false);
        missedButton.setEnabled(false);
    }

    private void flip() {
        Card card = session.getCurrentCard();
        showingAnswer = !showingAnswer;
        sideLabel.setText(showingAnswer ? "ANSWER" : "QUESTION");
        cardText.setText(wrap(showingAnswer ? card.getAnswer() : card.getQuestion()));
        // Only allow grading once the answer has been seen
        knewButton.setEnabled(showingAnswer);
        missedButton.setEnabled(showingAnswer);
    }

    private void grade(boolean knewIt) {
        session.answer(knewIt);
        if (session.isFinished()) {
            scoreLabel.setText("You knew " + session.getCorrectCount() + " of " + session.getTotal() + " cards");
            views.show(this, RESULT_VIEW);
        } else {
            showQuestion();
        }
    }

    /** JLabel needs HTML to wrap long text; escape so user text can't inject markup. */
    private static String wrap(String text) {
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<html><div style='text-align:center;width:400px'>" + safe + "</div></html>";
    }
}
