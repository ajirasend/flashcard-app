# Flashcard App

A desktop flashcard quiz app built with pure Java (Swing). Create decks, flip cards, and track your quiz score.
## Features
- [x] Create, edit, and delete decks and cards
- [x] Flip cards and self-grade ("I knew it" / "I didn't")
- [x] Automatic session scoring and shuffle
- [x] Persistent storage saving decks automatically to `~/.flashcard-app/decks`
- [x] Full keyboard navigation
- [x] Modern, responsive look with [FlatLaf](https://www.formdev.com/flatlaf/)

## Keyboard Shortcuts (Quiz)
| Key | Action |
|-----|--------|
| `Space` | Flip the card |
| `→` | I knew it |
| `←` | I didn't |
| `Esc` | Back to the deck list |

## Quick Start (Pre-built)
If you just want to run the app without building from source:
1. Download `flashcard-app-0.1.0-SNAPSHOT.jar` from the **[Releases](https://github.com/ajirasend/flashcard-app/releases)** page.
2. Run it with:
```bash
java -jar flashcard-app-0.1.0-SNAPSHOT.jar
```
*(Or simply double-click the `.jar` file if Java is installed).*

## Build from Source
If you want to view, build, or run the test suite yourself:
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
