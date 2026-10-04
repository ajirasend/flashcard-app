# Flashcard App

A desktop flashcard quiz app built with pure Java (Swing). Create decks, flip cards, and track your quiz score.

> Work in progress

## Features
- [x] Create, edit and delete decks and cards
- [x] Flip cards and self-grade ("I knew it" / "I didn't")
- [x] Score summary at the end of a quiz
- [x] Decks saved automatically to `~/.flashcard-app/decks`
- [x] Cards are shuffled each quiz
- [x] Keyboard shortcuts
- [x] Modern look with [FlatLaf](https://www.formdev.com/flatlaf/)

## Keyboard shortcuts (quiz)
| Key | Action |
|-----|--------|
| `Space` | Flip the card |
| `→` | I knew it |
| `←` | I didn't |
| `Esc` | Back to the deck list |

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
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/3625c3f5-3fc3-4395-9cdf-55ab209f6a56" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/08ebe9e6-7ef4-49ac-9e66-d55ee3f374f9" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/49cc4568-5c50-45a1-b1ad-45c4cbd1e906" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/927339df-475d-4f20-85de-2edcfc74dd25" />
