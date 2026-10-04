package com.bagas.flashcards.view;

import com.bagas.flashcards.model.Card;
import com.bagas.flashcards.model.Deck;
import com.bagas.flashcards.storage.DeckRepository;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/** Add, edit and delete the cards of one deck. Every change is saved immediately. */
public class EditorPanel extends JPanel {
    private final Deck deck;
    private final DeckRepository repository;

    private final DefaultListModel<Card> model = new DefaultListModel<>();
    private final JList<Card> list = new JList<>(model);
    private final JTextField questionField = new JTextField();
    private final JTextField answerField = new JTextField();

    public EditorPanel(Deck deck, DeckRepository repository, Navigator navigator) {
        this.deck = deck;
        this.repository = repository;

        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        content.add(buildHeader(navigator), BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(list.getFont().deriveFont(Font.PLAIN, 15f));
        list.setFixedCellHeight(38);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(l, value, index, selected, focus);
                label.putClientProperty("html.disable", Boolean.TRUE); // show user text literally
                label.setText((index + 1) + ". " + ((Card) value).getQuestion());
                label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return label;
            }
        });
        list.addListSelectionListener(e -> {
            Card selected = list.getSelectedValue();
            if (!e.getValueIsAdjusting() && selected != null) {
                questionField.setText(selected.getQuestion());
                answerField.setText(selected.getAnswer());
            }
        });
        content.add(new JScrollPane(list), BorderLayout.CENTER);

        content.add(buildForm(), BorderLayout.SOUTH);

        add(new MaxWidthPanel(content, 800), BorderLayout.CENTER);

        reloadList();
    }

    private JPanel buildHeader(Navigator navigator) {
        JPanel header = new JPanel(new BorderLayout());
        JButton back = new JButton("< Back");
        back.addActionListener(e -> navigator.showDeckList());
        JLabel title = new JLabel("Editing: " + deck.getName(), SwingConstants.CENTER);
        title.putClientProperty("html.disable", Boolean.TRUE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        header.add(back, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(Box.createHorizontalStrut(back.getPreferredSize().width), BorderLayout.EAST);
        return header;
    }

    private JPanel buildForm() {
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0;
        JLabel qLabel = new JLabel("Question:");
        qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD, 14f));
        fields.add(qLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        questionField.setFont(questionField.getFont().deriveFont(Font.PLAIN, 15f));
        fields.add(questionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel aLabel = new JLabel("Answer:");
        aLabel.setFont(aLabel.getFont().deriveFont(Font.BOLD, 14f));
        fields.add(aLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        answerField.setFont(answerField.getFont().deriveFont(Font.PLAIN, 15f));
        fields.add(answerField, gbc);

        JButton addButton = new JButton("Add card");
        JButton updateButton = new JButton("Update selected");
        JButton deleteButton = new JButton("Delete selected");
        addButton.addActionListener(e -> addCard());
        updateButton.addActionListener(e -> updateCard());
        deleteButton.addActionListener(e -> deleteCard());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        for (JButton b : new JButton[] {addButton, updateButton, deleteButton}) {
            b.setFont(b.getFont().deriveFont(Font.PLAIN, 14f));
            b.setMargin(new Insets(8, 16, 8, 16));
            buttons.add(b);
        }

        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.add(fields, BorderLayout.CENTER);
        form.add(buttons, BorderLayout.SOUTH);
        return form;
    }

    private void addCard() {
        try {
            deck.addCard(new Card(questionField.getText(), answerField.getText()));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        questionField.setText("");
        answerField.setText("");
        persistAndReload();
    }

    private void updateCard() {
        Card selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a card to update first.");
            return;
        }
        try {
            deck.replaceCard(selected, new Card(questionField.getText(), answerField.getText()));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        persistAndReload();
    }

    private void deleteCard() {
        Card selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a card to delete first.");
            return;
        }
        deck.removeCard(selected);
        questionField.setText("");
        answerField.setText("");
        persistAndReload();
    }

    private void persistAndReload() {
        try {
            repository.save(deck);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save deck: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        reloadList();
    }

    private void reloadList() {
        model.clear();
        for (Card card : deck.getCards()) {
            model.addElement(card);
        }
    }
}
