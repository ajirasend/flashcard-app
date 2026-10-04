# Flashcard App

A desktop flashcard quiz app built with pure Java (Swing). Create decks, flip cards, and track your quiz score.

> Work in progress

## Features (planned)
- [ ] Create, edit and delete decks and cards
- [ ] Flip cards and self-grade ("I knew it" / "I didn't")
- [ ] Score summary at the end of a quiz
- [x] Decks saved to a local file
- [ ] Shuffle mode and keyboard shortcuts

## Requirements
- Java 25 (JDK)
- Maven 3.9+

## Build and run
```bash
git clone https://github.com/<your-username>/flashcard-app.git
cd flashcard-app
mvn test
mvn package
java -jar target/flashcard-app-0.1.0-SNAPSHOT.jar
```

## Project structure
```
src/main/java/com/bagas/flashcards/
├── Main.java
└── model/    # Card, Deck
```

## Screenshots
_Coming soon_
