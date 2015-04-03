/*
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

import java.util.List;

import jexer.bits.Color;
import jexer.bits.ColorTheme;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TEditColorThemeWindow provides an easy UI for users to alter the running
 * color theme.
 *
 */
public final class TEditColorThemeWindow extends TWindow {

    /**
     * The foreground color picker.
     */
    class ForegroundPicker extends TWidget {

        /**
         * The selected color.
         */
        Color color;

        /**
         * The bold flag.
         */
        boolean bold;

        /**
         * Public constructor.
         *
         * @param parent parent widget
         * @param x column relative to parent
         * @param y row relative to parent
         * @param width width of text area
         * @param height height of text area
         */
        public ForegroundPicker(final TWidget parent, final int x,
            final int y, final int width, final int height) {

            super(parent, x, y, width, height);
        }

        /**
         * Get the X grid coordinate for this color.
         *
         * @param color the Color value
         * @return the X coordinate
         */
        private int getXColorPosition(final Color color) {
            if (color.equals(Color.BLACK)) {
                return 2;
            } else if (color.equals(Color.BLUE)) {
                return 5;
            } else if (color.equals(Color.GREEN)) {
                return 8;
            } else if (color.equals(Color.CYAN)) {
                return 11;
            } else if (color.equals(Color.RED)) {
                return 2;
            } else if (color.equals(Color.MAGENTA)) {
                return 5;
            } else if (color.equals(Color.YELLOW)) {
                return 8;
            } else if (color.equals(Color.WHITE)) {
                return 11;
            }
            throw new IllegalArgumentException("Invalid color: " + color);
        }

        /**
         * Get the Y grid coordinate for this color.
         *
         * @param color the Color value
         * @param bold if true use bold color
         * @return the Y coordinate
         */
        private int getYColorPosition(final Color color, final boolean bold) {
            int dotY = 1;
            if (color.equals(Color.RED)) {
                dotY = 2;
            } else if (color.equals(Color.MAGENTA)) {
                dotY = 2;
            } else if (color.equals(Color.YELLOW)) {
                dotY = 2;
            } else if (color.equals(Color.WHITE)) {
                dotY = 2;
            }
            if (bold) {
                dotY += 2;
            }
            return dotY;
        }

        /**
         * Get the bold value based on Y grid coordinate.
         *
         * @param dotY the Y coordinate
         * @return the bold value
         */
        private boolean getBoldFromPosition(final int dotY) {
            if (dotY > 2) {
                return true;
            }
            return false;
        }

        /**
         * Get the color based on (X, Y) grid coordinate.
         *
         * @param dotX the X coordinate
         * @param dotY the Y coordinate
         * @return the Color value
         */
        private Color getColorFromPosition(final int dotX, final int dotY) {
            int y = dotY;
            if (y > 2) {
                y -= 2;
            }
            if ((1 <= dotX) && (dotX <= 3) && (y == 1)) {
                return Color.BLACK;
            }
            if ((4 <= dotX) && (dotX <= 6) && (y == 1)) {
                return Color.BLUE;
            }
            if ((7 <= dotX) && (dotX <= 9) && (y == 1)) {
                return Color.GREEN;
            }
            if ((10 <= dotX) && (dotX <= 12) && (y == 1)) {
                return Color.CYAN;
            }
            if ((1 <= dotX) && (dotX <= 3) && (y == 2)) {
                return Color.RED;
            }
            if ((4 <= dotX) && (dotX <= 6) && (y == 2)) {
                return Color.MAGENTA;
            }
            if ((7 <= dotX) && (dotX <= 9) && (y == 2)) {
                return Color.YELLOW;
            }
            if ((10 <= dotX) && (dotX <= 12) && (y == 2)) {
                return Color.WHITE;
            }

            throw new IllegalArgumentException("Invalid coordinates: "
                + dotX + ", " + dotY);
        }

        /**
         * Draw the foreground colors grid.
         */
        @Override
        public void draw() {
            CellAttributes border = getWindow().getBorder();
            CellAttributes background = getWindow().getBackground();
            CellAttributes attr = new CellAttributes();

            getScreen().drawBox(0, 0, getWidth(), getHeight(), border,
                background, 1, false);

            attr.setTo(getTheme().getColor("twindow.background.modal"));
            if (isActive()) {
                attr.setForeColor(getTheme().getColor("tlabel").getForeColor());
                attr.setBold(getTheme().getColor("tlabel").isBold());
            }
            getScreen().putStringXY(1, 0, " Foreground ", attr);

            // Have to draw the colors manually because the int value matches
            // SGR, not CGA.
            attr.reset();
            attr.setReverse(true);
            attr.setForeColor(Color.BLACK);
            putStringXY(1, 1, "   ", attr);
            attr.setForeColor(Color.BLUE);
            putStringXY(4, 1, "   ", attr);
            attr.setForeColor(Color.GREEN);
            putStringXY(7, 1, "   ", attr);
            attr.setForeColor(Color.CYAN);
            putStringXY(10, 1, "   ", attr);
            attr.setForeColor(Color.RED);
            putStringXY(1, 2, "   ", attr);
            attr.setForeColor(Color.MAGENTA);
            putStringXY(4, 2, "   ", attr);
            attr.setForeColor(Color.YELLOW);
            putStringXY(7, 2, "   ", attr);
            attr.setForeColor(Color.WHITE);
            putStringXY(10, 2, "   ", attr);

            attr.setBold(true);
            attr.setForeColor(Color.BLACK);
            putStringXY(1, 3, "   ", attr);
            attr.setForeColor(Color.BLUE);
            putStringXY(4, 3, "   ", attr);
            attr.setForeColor(Color.GREEN);
            putStringXY(7, 3, "   ", attr);
            attr.setForeColor(Color.CYAN);
            putStringXY(10, 3, "   ", attr);
            attr.setForeColor(Color.RED);
            putStringXY(1, 4, "   ", attr);
            attr.setForeColor(Color.MAGENTA);
            putStringXY(4, 4, "   ", attr);
            attr.setForeColor(Color.YELLOW);
            putStringXY(7, 4, "   ", attr);
            attr.setForeColor(Color.WHITE);
            putStringXY(10, 4, "   ", attr);

            // Draw the dot
            int dotX = getXColorPosition(color);
            int dotY = getYColorPosition(color, bold);
            if (color.equals(Color.BLACK) && !bold) {
                // Use white-on-black for black.  All other colors use
                // black-on-whatever.
                attr.reset();
                getScreen().putCharXY(dotX, dotY, GraphicsChars.CP437[0x07],
                    attr);
            } else {
                getScreen().putCharXY(dotX, dotY, GraphicsChars.CP437[0x07]);
            }
        }

        /**
         * Handle keystrokes.
         *
         * @param keypress keystroke event
         */
        @Override
        public void onKeypress(final TKeypressEvent keypress) {
            if (keypress.equals(kbRight)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotX < 10) {
                    dotX += 3;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (keypress.equals(kbLeft)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotX > 3) {
                    dotX -= 3;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (keypress.equals(kbUp)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotY > 1) {
                    dotY--;
                }
                color = getColorFromPosition(dotX, dotY);
                bold = getBoldFromPosition(dotY);
            } else if (keypress.equals(kbDown)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotY < 4) {
                    dotY++;
                }
                color = getColorFromPosition(dotX, dotY);
                bold = getBoldFromPosition(dotY);
            } else {
                // Pass to my parent
                super.onKeypress(keypress);
                return;
            }

            // Save this update to the local theme.
            ((TEditColorThemeWindow) getWindow()).saveToEditTheme();
        }

        /**
         * Handle mouse press events.
         *
         * @param mouse mouse button press event
         */
        @Override
        public void onMouseDown(final TMouseEvent mouse) {
            if (mouse.isMouseWheelUp()) {
                // Do this like kbUp
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotY > 1) {
                    dotY--;
                }
                color = getColorFromPosition(dotX, dotY);
                bold = getBoldFromPosition(dotY);
            } else if (mouse.isMouseWheelDown()) {
                // Do this like kbDown
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color, bold);
                if (dotY < 4) {
                    dotY++;
                }
                color = getColorFromPosition(dotX, dotY);
                bold = getBoldFromPosition(dotY);
            } else if ((mouse.getX() > 0)
                && (mouse.getX() < getWidth() - 1)
                && (mouse.getY() > 0)
                && (mouse.getY() < getHeight() - 1)
            ) {
                color = getColorFromPosition(mouse.getX(), mouse.getY());
                bold = getBoldFromPosition(mouse.getY());
            } else {
                // Let parent class handle it.
                super.onMouseDown(mouse);
                return;
            }

            // Save this update to the local theme.
            ((TEditColorThemeWindow) getWindow()).saveToEditTheme();
        }

    }

    /**
     * The background color picker.
     */
    class BackgroundPicker extends TWidget {

        /**
         * The selected color.
         */
        Color color;

        /**
         * Public constructor.
         *
         * @param parent parent widget
         * @param x column relative to parent
         * @param y row relative to parent
         * @param width width of text area
         * @param height height of text area
         */
        public BackgroundPicker(final TWidget parent, final int x,
            final int y, final int width, final int height) {

            super(parent, x, y, width, height);
        }

        /**
         * Get the X grid coordinate for this color.
         *
         * @param color the Color value
         * @return the X coordinate
         */
        private int getXColorPosition(final Color color) {
            if (color.equals(Color.BLACK)) {
                return 2;
            } else if (color.equals(Color.BLUE)) {
                return 5;
            } else if (color.equals(Color.GREEN)) {
                return 8;
            } else if (color.equals(Color.CYAN)) {
                return 11;
            } else if (color.equals(Color.RED)) {
                return 2;
            } else if (color.equals(Color.MAGENTA)) {
                return 5;
            } else if (color.equals(Color.YELLOW)) {
                return 8;
            } else if (color.equals(Color.WHITE)) {
                return 11;
            }
            throw new IllegalArgumentException("Invalid color: " + color);
        }

        /**
         * Get the Y grid coordinate for this color.
         *
         * @param color the Color value
         * @return the Y coordinate
         */
        private int getYColorPosition(final Color color) {
            int dotY = 1;
            if (color.equals(Color.RED)) {
                dotY = 2;
            } else if (color.equals(Color.MAGENTA)) {
                dotY = 2;
            } else if (color.equals(Color.YELLOW)) {
                dotY = 2;
            } else if (color.equals(Color.WHITE)) {
                dotY = 2;
            }
            return dotY;
        }

        /**
         * Get the color based on (X, Y) grid coordinate.
         *
         * @param dotX the X coordinate
         * @param dotY the Y coordinate
         * @return the Color value
         */
        private Color getColorFromPosition(final int dotX, final int dotY) {
            if ((1 <= dotX) && (dotX <= 3) && (dotY == 1)) {
                return Color.BLACK;
            }
            if ((4 <= dotX) && (dotX <= 6) && (dotY == 1)) {
                return Color.BLUE;
            }
            if ((7 <= dotX) && (dotX <= 9) && (dotY == 1)) {
                return Color.GREEN;
            }
            if ((10 <= dotX) && (dotX <= 12) && (dotY == 1)) {
                return Color.CYAN;
            }
            if ((1 <= dotX) && (dotX <= 3) && (dotY == 2)) {
                return Color.RED;
            }
            if ((4 <= dotX) && (dotX <= 6) && (dotY == 2)) {
                return Color.MAGENTA;
            }
            if ((7 <= dotX) && (dotX <= 9) && (dotY == 2)) {
                return Color.YELLOW;
            }
            if ((10 <= dotX) && (dotX <= 12) && (dotY == 2)) {
                return Color.WHITE;
            }

            throw new IllegalArgumentException("Invalid coordinates: "
                + dotX + ", " + dotY);
        }

        /**
         * Draw the background colors grid.
         */
        @Override
        public void draw() {
            CellAttributes border = getWindow().getBorder();
            CellAttributes background = getWindow().getBackground();
            CellAttributes attr = new CellAttributes();

            getScreen().drawBox(0, 0, getWidth(), getHeight(), border,
                background, 1, false);

            attr.setTo(getTheme().getColor("twindow.background.modal"));
            if (isActive()) {
                attr.setForeColor(getTheme().getColor("tlabel").getForeColor());
                attr.setBold(getTheme().getColor("tlabel").isBold());
            }
            getScreen().putStringXY(1, 0, " Background ", attr);

            // Have to draw the colors manually because the int value matches
            // SGR, not CGA.
            attr.reset();
            attr.setReverse(true);
            attr.setForeColor(Color.BLACK);
            putStringXY(1, 1, "   ", attr);
            attr.setForeColor(Color.BLUE);
            putStringXY(4, 1, "   ", attr);
            attr.setForeColor(Color.GREEN);
            putStringXY(7, 1, "   ", attr);
            attr.setForeColor(Color.CYAN);
            putStringXY(10, 1, "   ", attr);
            attr.setForeColor(Color.RED);
            putStringXY(1, 2, "   ", attr);
            attr.setForeColor(Color.MAGENTA);
            putStringXY(4, 2, "   ", attr);
            attr.setForeColor(Color.YELLOW);
            putStringXY(7, 2, "   ", attr);
            attr.setForeColor(Color.WHITE);
            putStringXY(10, 2, "   ", attr);

            // Draw the dot
            int dotX = getXColorPosition(color);
            int dotY = getYColorPosition(color);
            if (color.equals(Color.BLACK)) {
                // Use white-on-black for black.  All other colors use
                // black-on-whatever.
                attr.reset();
                getScreen().putCharXY(dotX, dotY, GraphicsChars.CP437[0x07],
                    attr);
            } else {
                getScreen().putCharXY(dotX, dotY, GraphicsChars.CP437[0x07]);
            }

        }

        /**
         * Handle keystrokes.
         *
         * @param keypress keystroke event
         */
        @Override
        public void onKeypress(final TKeypressEvent keypress) {
            if (keypress.equals(kbRight)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotX < 10) {
                    dotX += 3;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (keypress.equals(kbLeft)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotX > 3) {
                    dotX -= 3;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (keypress.equals(kbUp)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotY == 2) {
                    dotY--;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (keypress.equals(kbDown)) {
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotY == 1) {
                    dotY++;
                }
                color = getColorFromPosition(dotX, dotY);
            } else {
                // Pass to my parent
                super.onKeypress(keypress);
            }

            // Save this update to the local theme.
            ((TEditColorThemeWindow) getWindow()).saveToEditTheme();
        }

        /**
         * Handle mouse press events.
         *
         * @param mouse mouse button press event
         */
        @Override
        public void onMouseDown(final TMouseEvent mouse) {
            if (mouse.isMouseWheelUp()) {
                // Do this like kbUp
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotY == 2) {
                    dotY--;
                }
                color = getColorFromPosition(dotX, dotY);
            } else if (mouse.isMouseWheelDown()) {
                // Do this like kbDown
                int dotX = getXColorPosition(color);
                int dotY = getYColorPosition(color);
                if (dotY == 1) {
                    dotY++;
                }
                color = getColorFromPosition(dotX, dotY);
                return;
            } else if ((mouse.getX() > 0)
                && (mouse.getX() < getWidth() - 1)
                && (mouse.getY() > 0)
                && (mouse.getY() < getHeight() - 1)
            ) {
                color = getColorFromPosition(mouse.getX(), mouse.getY());
            } else {
                // Let parent class handle it.
                super.onMouseDown(mouse);
                return;
            }

            // Save this update to the local theme.
            ((TEditColorThemeWindow) getWindow()).saveToEditTheme();
        }

    }

    /**
     * The current editing theme.
     */
    private ColorTheme editTheme;

    /**
     * The left-side list of colors pane.
     */
    private TList colorNames;

    /**
     * The foreground color.
     */
    private ForegroundPicker foreground;

    /**
     * The background color.
     */
    private BackgroundPicker background;

    /**
     * Set various widgets/values to the editing theme color.
     *
     * @param colorName name of color from theme
     */
    private void refreshFromTheme(final String colorName) {
        CellAttributes attr = editTheme.getColor(colorName);
        foreground.color = attr.getForeColor();
        foreground.bold = attr.isBold();
        background.color = attr.getBackColor();
    }

    /**
     * Examines foreground, background, and colorNames and sets the color in
     * editTheme.
     */
    private void saveToEditTheme() {
        String colorName = colorNames.getSelected();
        if (colorName == null) {
            return;
        }
        CellAttributes attr = editTheme.getColor(colorName);
        attr.setForeColor(foreground.color);
        attr.setBold(foreground.bold);
        attr.setBackColor(background.color);
        editTheme.setColor(colorName, attr);
    }

    /**
     * Public constructor.  The file open box will be centered on screen.
     *
     * @param application the TApplication that manages this window
     */
    public TEditColorThemeWindow(final TApplication application) {

        // Register with the TApplication
        super(application, "Colors", 0, 0, 60, 18, MODAL);

        // Initialize with the first color
        List<String> colors = getTheme().getColorNames();
        assert (colors.size() > 0);
        editTheme = new ColorTheme();
        for (String key: colors) {
            CellAttributes attr = new CellAttributes();
            attr.setTo(getTheme().getColor(key));
            editTheme.setColor(key, attr);
        }

        colorNames = addList(colors, 2, 2, 38, getHeight() - 7,
            new TAction() {
                // When the user presses Enter
                public void DO() {
                    refreshFromTheme(colorNames.getSelected());
                }
            },
            new TAction() {
                // When the user navigates with keyboard
                public void DO() {
                    refreshFromTheme(colorNames.getSelected());
                }
            }
        );
        foreground = new ForegroundPicker(this, 42, 1, 14, 6);
        background = new BackgroundPicker(this, 42, 7, 14, 4);
        refreshFromTheme(colors.get(0));
        colorNames.setSelectedIndex(0);

        addButton("  &OK  ", getWidth() - 37, getHeight() - 4,
            new TAction() {
                public void DO() {
                    ColorTheme global = getTheme();
                    List<String> colors = editTheme.getColorNames();
                    for (String key: colors) {
                        CellAttributes attr = new CellAttributes();
                        attr.setTo(editTheme.getColor(key));
                        global.setColor(key, attr);
                    }
                    getApplication().closeWindow(TEditColorThemeWindow.this);
                }
            }
        );

        addButton("&Cancel", getWidth() - 25, getHeight() - 4,
            new TAction() {
                public void DO() {
                    getApplication().closeWindow(TEditColorThemeWindow.this);
                }
            }
        );

        // Default to the color list
        activate(colorNames);

    }

    /**
     * Draw me on screen.
     */
    @Override
    public void draw() {
        super.draw();
        CellAttributes attr = new CellAttributes();

        // Draw the label on colorNames
        attr.setTo(getTheme().getColor("twindow.background.modal"));
        if (colorNames.isActive()) {
            attr.setForeColor(getTheme().getColor("tlabel").getForeColor());
            attr.setBold(getTheme().getColor("tlabel").isBold());
        }
        getScreen().putStringXY(3, 2, "Color Name", attr);

        // Draw the sample text box
        attr.reset();
        attr.setForeColor(foreground.color);
        attr.setBold(foreground.bold);
        attr.setBackColor(background.color);
        getScreen().putStringXY(getWidth() - 17, getHeight() - 6,
            "Text Text Text", attr);
        getScreen().putStringXY(getWidth() - 17, getHeight() - 5,
            "Text Text Text", attr);
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        // Escape - behave like cancel
        if (keypress.equals(kbEsc)) {
            getApplication().closeWindow(this);
            return;
        }

        // Pass to my parent
        super.onKeypress(keypress);
    }

}
