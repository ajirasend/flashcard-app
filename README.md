# Flashcard App

A desktop flashcard quiz app built with pure Java (Swing). Create decks, flip cards, and track your quiz score.

> Work in progress

## Features
- [x] Create, edit and delete decks and cards
- [x] Flip cards and self-grade ("I knew it" / "I didn't")
- [x] Score summary at the end of a quiz
- [x] Decks saved automatically to `~/.flashcard-app/decks`
- [x] Cards are shuffled each quiz
- [ ] Keyboard shortcuts

## Requirements
- Java 25 (JDK)
- Maven 3.9+

## Build and run
```bash
git clone https://github.com/ajirasend/flashcard-app.git
cd flashcard-app
mvn test
mvn package
java -jar target/flashcard-app-0.1.0-SNAPSHOT.jar
```

## Project structure
```
src/main/java/com/bagas/flashcards/
├── Main.java
├── model/      # Card, Deck, QuizSession (no UI code)
├── storage/    # DeckStorage (one file), DeckRepository (folder of decks)
└── view/       # Swing screens: DeckListPanel, EditorPanel, QuizPanel, MainFrame
```

## Screenshots
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/73613872-246a-4be3-93ae-0236206c1580" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/d544a660-cb01-4c12-9e73-11f3b11a8e2b" />
