package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.model.QuizSession;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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
    private final FlashCardView cardView = new FlashCardView();
    private final JButton flipButton = new JButton("Flip (Space)");
    private final JButton knewButton = new JButton("I knew it (\u2192)");
    private final JButton missedButton = new JButton("I didn't (\u2190)");
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
        installShortcuts();
        startQuiz();
    }

    /**
     * Space = flip, left arrow = "I didn't", right arrow = "I knew it", Escape = back.
     * Bound to the whole window so they work no matter which component has focus.
     */
    private void installShortcuts() {
        // Buttons must not steal focus, otherwise Space would "click" whichever button was last used
        flipButton.setFocusable(false);
        knewButton.setFocusable(false);
        missedButton.setFocusable(false);

        bindKey("SPACE", "flip", this::flipIfAllowed);
        bindKey("RIGHT", "knew", () -> gradeIfAllowed(true));
        bindKey("LEFT", "missed", () -> gradeIfAllowed(false));
        bindKey("ESCAPE", "back", onExit);
    }

    private void bindKey(String keyStroke, String name, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    // Shortcuts must follow the same rules as the buttons: no flipping or grading on the score screen,
    // and no grading before the answer has been seen.
    private void flipIfAllowed() {
        if (!session.isFinished() && flipButton.isEnabled()) {
            flip();
        }
    }

    private void gradeIfAllowed(boolean knewIt) {
        if (!session.isFinished() && showingAnswer) {
            grade(knewIt);
        }
    }

    private JPanel buildQuizView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 24, 20));

        JPanel header = new JPanel(new BorderLayout());
        JButton backButton = new JButton("< Back");
        backButton.addActionListener(e -> onExit.run());
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.PLAIN, 15f));
        header.add(backButton, BorderLayout.WEST);
        header.add(progressLabel, BorderLayout.CENTER);
        // Invisible twin of the back button keeps the progress label truly centered
        header.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        panel.add(cardView, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        for (JButton button : new JButton[] {missedButton, flipButton, knewButton}) {
            button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
            button.setMargin(new Insets(10, 22, 10, 22));
            buttons.add(button);
        }
        panel.add(buttons, BorderLayout.SOUTH);

        flipButton.addActionListener(e -> flip());
        knewButton.addActionListener(e -> grade(true));
        missedButton.addActionListener(e -> grade(false));
        return panel;
    }

    private JPanel buildResultView() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quiz finished!", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.PLAIN, 22f));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton again = new JButton("Try again");
        JButton back = new JButton("Back to decks");
        again.addActionListener(e -> startQuiz());
        back.addActionListener(e -> onExit.run());
        for (JButton button : new JButton[] {again, back}) {
            button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
            button.setMargin(new Insets(10, 22, 10, 22));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        content.add(title);
        content.add(Box.createVerticalStrut(16));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(32));
        content.add(again);
        content.add(Box.createVerticalStrut(10));
        content.add(back);

        // GridBagLayout with default constraints centers its child both ways
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(content);
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
        cardView.showSide(false, card.getQuestion());
        flipButton.setEnabled(true);
        knewButton.setEnabled(false);
        missedButton.setEnabled(false);
    }

    private void flip() {
        Card card = session.getCurrentCard();
        showingAnswer = !showingAnswer;
        cardView.showSide(showingAnswer, showingAnswer ? card.getAnswer() : card.getQuestion());
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
}
