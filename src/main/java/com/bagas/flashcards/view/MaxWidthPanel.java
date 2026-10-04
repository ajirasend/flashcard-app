package com.bagas.flashcards.view;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Centers a single child horizontally and caps its width, like a centered column on a website.
 * Keeps screens readable on wide monitors instead of stretching edge to edge.
 */
public class MaxWidthPanel extends JPanel {
    private final JComponent content;
    private final int maxWidth;

    public MaxWidthPanel(JComponent content, int maxWidth) {
        super(null);
        this.content = content;
        this.maxWidth = maxWidth;
        add(content);
    }

    @Override
    public void doLayout() {
        Insets in = getInsets();
        int availableWidth = getWidth() - in.left - in.right;
        int availableHeight = getHeight() - in.top - in.bottom;
        int width = Math.min(availableWidth, maxWidth);
        int x = in.left + (availableWidth - width) / 2;
        content.setBounds(x, in.top, width, availableHeight);
    }

    @Override
    public Dimension getPreferredSize() {
        return withInsets(content.getPreferredSize());
    }

    @Override
    public Dimension getMinimumSize() {
        return withInsets(content.getMinimumSize());
    }

    private Dimension withInsets(Dimension d) {
        Insets in = getInsets();
        return new Dimension(Math.min(d.width, maxWidth) + in.left + in.right, d.height + in.top + in.bottom);
    }
}
