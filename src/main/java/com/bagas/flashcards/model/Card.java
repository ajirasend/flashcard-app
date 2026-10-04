package com.bagas.flashcards.model;

/**
 * A single flashcard with a question (front) and an answer (back).
 */
public class Card {
    private final String question;
    private final String answer;

    public Card(String question, String answer) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be empty");
        }
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer must not be empty");
        }
        this.question = question.trim();
        this.answer = answer.trim();
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
