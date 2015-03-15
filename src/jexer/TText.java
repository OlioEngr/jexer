/**
 * Jexer - Java Text User Interface
 *
 * License: LGPLv3 or later
 *
 * This module is licensed under the GNU Lesser General Public License
 * Version 3.  Please see the file "COPYING" in this directory for more
 * information about the GNU Lesser General Public License Version 3.
 *
 *     Copyright (C) 2015  Kevin Lamonte
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see
 * http://www.gnu.org/licenses/, or write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author Kevin Lamonte [kevin.lamonte@gmail.com]
 * @version 1
 */
package jexer;

import java.util.LinkedList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TText implements a simple text windget.
 */
public final class TText extends TWidget {

    /**
     * Text to display.
     */
    private String text;

    /**
     * Text converted to lines.
     */
    private List<String> lines;

    /**
     * Text color.
     */
    private String colorKey;

    /**
     * Vertical scrollbar.
     */
    private TVScroller vScroller;

    /**
     * Horizontal scrollbar.
     */
    private THScroller hScroller;

    /**
     * Maximum width of a single line.
     */
    private int maxLineWidth;

    /**
     * Number of lines between each paragraph.
     */
    private int lineSpacing = 1;

    /**
     * Convenience method used by TWindowLoggerOutput.
     *
     * @param line new line to add
     */
    public void addLine(final String line) {
        if (text.length() == 0) {
            text = line;
        } else {
            text += "\n\n";
            text += line;
        }
        reflow();
    }

    /**
     * Recompute the bounds for the scrollbars.
     */
    private void computeBounds() {
        maxLineWidth = 0;
        for (String line: lines) {
            if (line.length() > maxLineWidth) {
                maxLineWidth = line.length();
            }
        }

        vScroller.setBottomValue(lines.size() - getHeight() + 1);
        if (vScroller.getBottomValue() < 0) {
            vScroller.setBottomValue(0);
        }
        if (vScroller.getValue() > vScroller.getBottomValue()) {
            vScroller.setValue(vScroller.getBottomValue());
        }

        hScroller.setRightValue(maxLineWidth - getWidth() + 1);
        if (hScroller.getRightValue() < 0) {
            hScroller.setRightValue(0);
        }
        if (hScroller.getValue() > hScroller.getRightValue()) {
            hScroller.setValue(hScroller.getRightValue());
        }
    }

    /**
     * Insert newlines into a string to wrap it to a maximum column.
     * Terminate the final string with a newline.  Note that interior
     * newlines are converted to spaces.
     *
     * @param str the string
     * @param n the maximum number of characters in a line
     * @return the wrapped string
     */
    private String wrap(final String str, final int n) {
        assert (n > 0);

        StringBuilder sb = new StringBuilder();
        StringBuilder word = new StringBuilder();
        int col = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\n') {
                ch = ' ';
            }
            if (ch == ' ') {
                sb.append(word.toString());
                sb.append(ch);
                if (word.length() >= n - 1) {
                    sb.append('\n');
                    col = 0;
                }
                word = new StringBuilder();
            } else {
                word.append(ch);
            }

            col++;
            if (col >= n - 1) {
                sb.append('\n');
                col = 0;
            }
        }
        sb.append(word.toString());
        sb.append('\n');
        return sb.toString();
    }


    /**
     * Resize text and scrollbars for a new width/height.
     */
    public void reflow() {
        // Reset the lines
        lines.clear();

        // Break up text into paragraphs
        String [] paragraphs = text.split("\n\n");
        for (String p: paragraphs) {
            String paragraph = wrap(p, getWidth() - 1);
            for (String line: paragraph.split("\n")) {
                lines.add(line);
            }
            for (int i = 0; i < lineSpacing; i++) {
                lines.add("");
            }
        }

        // Start at the top
        if (vScroller == null) {
            vScroller = new TVScroller(this, getWidth() - 1, 0,
                getHeight() - 1);
            vScroller.setTopValue(0);
            vScroller.setValue(0);
        } else {
            vScroller.setX(getWidth() - 1);
            vScroller.setHeight(getHeight() - 1);
        }
        vScroller.setBigChange(getHeight() - 1);

        // Start at the left
        if (hScroller == null) {
            hScroller = new THScroller(this, 0, getHeight() - 1,
                getWidth() - 1);
            hScroller.setLeftValue(0);
            hScroller.setValue(0);
        } else {
            hScroller.setY(getHeight() - 1);
            hScroller.setWidth(getWidth() - 1);
        }
        hScroller.setBigChange(getWidth() - 1);

        computeBounds();
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param text text on the screen
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     */
    public TText(final TWidget parent, final String text, final int x,
        final int y, final int width, final int height) {

        this(parent, text, x, y, width, height, "ttext");
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param text text on the screen
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     * @param colorKey ColorTheme key color to use for foreground text.
     * Default is "ttext"
     */
    public TText(final TWidget parent, final String text, final int x,
        final int y, final int width, final int height, final String colorKey) {

        // Set parent and window
        super(parent);

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        this.text = text;
        this.colorKey = colorKey;

        lines = new LinkedList<String>();

        reflow();
    }

    /**
     * Draw the text box.
     */
    @Override
    public void draw() {
        // Setup my color
        CellAttributes color = getTheme().getColor(colorKey);

        int begin = vScroller.getValue();
        int topY = 0;
        for (int i = begin; i < lines.size(); i++) {
            String line = lines.get(i);
            if (hScroller.getValue() < line.length()) {
                line = line.substring(hScroller.getValue());
            } else {
                line = "";
            }
            String formatString = "%-" + Integer.toString(getWidth() - 1) + "s";
            getScreen().putStrXY(0, topY, String.format(formatString, line),
                color);
            topY++;

            if (topY >= getHeight() - 1) {
                break;
            }
        }

        // Pad the rest with blank lines
        for (int i = topY; i < getHeight() - 1; i++) {
            getScreen().hLineXY(0, i, getWidth() - 1, ' ', color);
        }

    }

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (mouse.getMouseWheelUp()) {
            vScroller.decrement();
            return;
        }
        if (mouse.getMouseWheelDown()) {
            vScroller.increment();
            return;
        }

        // Pass to children
        super.onMouseDown(mouse);
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbLeft)) {
            hScroller.decrement();
        } else if (keypress.equals(kbRight)) {
            hScroller.increment();
        } else if (keypress.equals(kbUp)) {
            vScroller.decrement();
        } else if (keypress.equals(kbDown)) {
            vScroller.increment();
        } else if (keypress.equals(kbPgUp)) {
            vScroller.bigDecrement();
        } else if (keypress.equals(kbPgDn)) {
            vScroller.bigIncrement();
        } else if (keypress.equals(kbHome)) {
            vScroller.toTop();
        } else if (keypress.equals(kbEnd)) {
            vScroller.toBottom();
        } else {
            // Pass other keys (tab etc.) on
            super.onKeypress(keypress);
        }
    }

}
