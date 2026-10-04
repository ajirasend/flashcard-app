package com.bagas.flashcards.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Draws a flashcard that keeps an index-card shape (3:2) and stays centered,
 * whatever the window size. Text size scales with the card and shrinks if a long
 * answer would not fit.
 */
public class FlashCardView extends JPanel {
    private static final double RATIO = 3.0 / 2.0;
    private static final int PADDING_X = 32;
    private static final int PADDING_Y = 20;
    private static final Color QUESTION_COLOR = new Color(0x6B7280);
    private static final Color ANSWER_COLOR = new Color(0x15803D);

    private final JPanel card = new JPanel(new BorderLayout());
    private final JLabel sideLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel textLabel = new JLabel("", SwingConstants.CENTER);
    private String text = "";

    public FlashCardView() {
        super(null);
        setOpaque(false);

        // FlatLaf paints the rounded white background; the line border draws the matching outline
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: #FFFFFF");
        card.setBorder(BorderFactory.createCompoundBorder(
                new FlatLineBorder(new Insets(0, 0, 0, 0), new Color(0xD1D5DB), 1f, 24),
                BorderFactory.createEmptyBorder(PADDING_Y, PADDING_X, PADDING_Y, PADDING_X)));
        card.add(sideLabel, BorderLayout.NORTH);
        card.add(textLabel, BorderLayout.CENTER);
        add(card);
    }

    /** Shows one side of a card. */
    public void showSide(boolean answer, String text) {
        this.text = text;
        sideLabel.setText(answer ? "ANSWER" : "QUESTION");
        sideLabel.setForeground(answer ? ANSWER_COLOR : QUESTION_COLOR);
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        // Largest 3:2 rectangle that fits, up to a sensible maximum, centered
        int maxW = Math.min(w, 850);
        int maxH = Math.min(h, 550);
        int cardW = maxW;
        int cardH = (int) (cardW / RATIO);
        if (cardH > maxH) {
            cardH = maxH;
            cardW = (int) (cardH * RATIO);
        }
        card.setBounds((w - cardW) / 2, (h - cardH) / 2, cardW, cardH);

        float sideSize = clamp(cardW / 55f, 11f, 16f);
        setFontSize(sideLabel, sideSize, Font.BOLD);

        // Scale text with the card, then shrink until it fits vertically
        int textWidth = Math.max(50, cardW - 2 * PADDING_X - 24);
        int textHeight = cardH - 2 * PADDING_Y - sideLabel.getPreferredSize().height - 16;
        int size = (int) clamp(cardW / 26f, 16f, 32f);
        textLabel.setText(html(text, textWidth, size));
        while (size > 13 && textLabel.getPreferredSize().height > textHeight) {
            size -= 2;
            textLabel.setText(html(text, textWidth, size));
        }
        card.doLayout();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 300);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(300, 200);
    }

    private static void setFontSize(JLabel label, float size, int style) {
        Font current = label.getFont();
        if (current.getSize2D() != size || current.getStyle() != style) {
            label.setFont(current.deriveFont(style, size));
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /** HTML table width reliably forces Swing's HTML renderer to wrap text properly. */
    private static String html(String text, int width, int fontSize) {
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<html><table width='" + width + "'><tr><td align='center' style='font-size: "
                + fontSize + "pt; color: #1F2937;'>" + safe + "</td></tr></table></html>";
    }
}
