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
package jexer.io;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.io.InputStream;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jexer.bits.Cell;
import jexer.bits.CellAttributes;
import jexer.session.SwingSessionInfo;

/**
 * This Screen implementation draws to a Java Swing JFrame.
 */
public final class SwingScreen extends Screen {

    /**
     * If true, use double buffering thread.
     */
    private static final boolean doubleBuffer = true;

    /**
     * Cursor style to draw.
     */
    public enum CursorStyle {
        /**
         * Use an underscore for the cursor.
         */
        UNDERLINE,

        /**
         * Use a solid block for the cursor.
         */
        BLOCK,

        /**
         * Use an outlined block for the cursor.
         */
        OUTLINE
    }

    private static Color MYBLACK;
    private static Color MYRED;
    private static Color MYGREEN;
    private static Color MYYELLOW;
    private static Color MYBLUE;
    private static Color MYMAGENTA;
    private static Color MYCYAN;
    private static Color MYWHITE;

    private static Color MYBOLD_BLACK;
    private static Color MYBOLD_RED;
    private static Color MYBOLD_GREEN;
    private static Color MYBOLD_YELLOW;
    private static Color MYBOLD_BLUE;
    private static Color MYBOLD_MAGENTA;
    private static Color MYBOLD_CYAN;
    private static Color MYBOLD_WHITE;

    private static boolean dosColors = false;

    /**
     * Setup Swing colors to match DOS color palette.
     */
    private static void setDOSColors() {
        if (dosColors) {
            return;
        }
        MYBLACK        = new Color(0x00, 0x00, 0x00);
        MYRED          = new Color(0xa8, 0x00, 0x00);
        MYGREEN        = new Color(0x00, 0xa8, 0x00);
        MYYELLOW       = new Color(0xa8, 0x54, 0x00);
        MYBLUE         = new Color(0x00, 0x00, 0xa8);
        MYMAGENTA      = new Color(0xa8, 0x00, 0xa8);
        MYCYAN         = new Color(0x00, 0xa8, 0xa8);
        MYWHITE        = new Color(0xa8, 0xa8, 0xa8);
        MYBOLD_BLACK   = new Color(0x54, 0x54, 0x54);
        MYBOLD_RED     = new Color(0xfc, 0x54, 0x54);
        MYBOLD_GREEN   = new Color(0x54, 0xfc, 0x54);
        MYBOLD_YELLOW  = new Color(0xfc, 0xfc, 0x54);
        MYBOLD_BLUE    = new Color(0x54, 0x54, 0xfc);
        MYBOLD_MAGENTA = new Color(0xfc, 0x54, 0xfc);
        MYBOLD_CYAN    = new Color(0x54, 0xfc, 0xfc);
        MYBOLD_WHITE   = new Color(0xfc, 0xfc, 0xfc);

        dosColors = true;
    }

    /**
     * SwingFrame is our top-level hook into the Swing system.
     */
    class SwingFrame extends JFrame {

        /**
         * Serializable version.
         */
        private static final long serialVersionUID = 1;

        /**
         * The terminus font resource filename.
         */
        private static final String FONTFILE = "terminus-ttf-4.39/TerminusTTF-Bold-4.39.ttf";

        /**
         * The BufferStrategy object needed for double-buffering.
         */
        private BufferStrategy bufferStrategy;

        /**
         * The TUI Screen data.
         */
        SwingScreen screen;

        /**
         * Width of a character cell.
         */
        private int textWidth = 1;

        /**
         * Height of a character cell.
         */
        private int textHeight = 1;

        /**
         * Descent of a character cell.
         */
        private int maxDescent = 0;

        /**
         * System-dependent Y adjustment for text in the  character cell.
         */
        private int textAdjustY = 0;

        /**
         * System-dependent X adjustment for text in the  character cell.
         */
        private int textAdjustX = 0;

        /**
         * Top pixel absolute location.
         */
        private int top = 30;

        /**
         * Left pixel absolute location.
         */
        private int left = 30;

        /**
         * The cursor style to draw.
         */
        private CursorStyle cursorStyle = CursorStyle.UNDERLINE;

        /**
         * The number of millis to wait before switching the blink from
         * visible to invisible.
         */
        private long blinkMillis = 500;

        /**
         * If true, the cursor should be visible right now based on the blink
         * time.
         */
        private boolean cursorBlinkVisible = true;

        /**
         * The time that the blink last flipped from visible to invisible or
         * from invisible to visible.
         */
        private long lastBlinkTime = 0;

        /**
         * Convert a CellAttributes foreground color to an Swing Color.
         *
         * @param attr the text attributes
         * @return the Swing Color
         */
        private Color attrToForegroundColor(final CellAttributes attr) {
            if (attr.isBold()) {
                if (attr.getForeColor().equals(jexer.bits.Color.BLACK)) {
                    return MYBOLD_BLACK;
                } else if (attr.getForeColor().equals(jexer.bits.Color.RED)) {
                    return MYBOLD_RED;
                } else if (attr.getForeColor().equals(jexer.bits.Color.BLUE)) {
                    return MYBOLD_BLUE;
                } else if (attr.getForeColor().equals(jexer.bits.Color.GREEN)) {
                    return MYBOLD_GREEN;
                } else if (attr.getForeColor().equals(jexer.bits.Color.YELLOW)) {
                    return MYBOLD_YELLOW;
                } else if (attr.getForeColor().equals(jexer.bits.Color.CYAN)) {
                    return MYBOLD_CYAN;
                } else if (attr.getForeColor().equals(jexer.bits.Color.MAGENTA)) {
                    return MYBOLD_MAGENTA;
                } else if (attr.getForeColor().equals(jexer.bits.Color.WHITE)) {
                    return MYBOLD_WHITE;
                }
            } else {
                if (attr.getForeColor().equals(jexer.bits.Color.BLACK)) {
                    return MYBLACK;
                } else if (attr.getForeColor().equals(jexer.bits.Color.RED)) {
                    return MYRED;
                } else if (attr.getForeColor().equals(jexer.bits.Color.BLUE)) {
                    return MYBLUE;
                } else if (attr.getForeColor().equals(jexer.bits.Color.GREEN)) {
                    return MYGREEN;
                } else if (attr.getForeColor().equals(jexer.bits.Color.YELLOW)) {
                    return MYYELLOW;
                } else if (attr.getForeColor().equals(jexer.bits.Color.CYAN)) {
                    return MYCYAN;
                } else if (attr.getForeColor().equals(jexer.bits.Color.MAGENTA)) {
                    return MYMAGENTA;
                } else if (attr.getForeColor().equals(jexer.bits.Color.WHITE)) {
                    return MYWHITE;
                }
            }
            throw new IllegalArgumentException("Invalid color: " + attr.getForeColor().getValue());
        }

        /**
         * Convert a CellAttributes background color to an Swing Color.
         *
         * @param attr the text attributes
         * @return the Swing Color
         */
        private Color attrToBackgroundColor(final CellAttributes attr) {
            if (attr.getBackColor().equals(jexer.bits.Color.BLACK)) {
                return MYBLACK;
            } else if (attr.getBackColor().equals(jexer.bits.Color.RED)) {
                return MYRED;
            } else if (attr.getBackColor().equals(jexer.bits.Color.BLUE)) {
                return MYBLUE;
            } else if (attr.getBackColor().equals(jexer.bits.Color.GREEN)) {
                return MYGREEN;
            } else if (attr.getBackColor().equals(jexer.bits.Color.YELLOW)) {
                return MYYELLOW;
            } else if (attr.getBackColor().equals(jexer.bits.Color.CYAN)) {
                return MYCYAN;
            } else if (attr.getBackColor().equals(jexer.bits.Color.MAGENTA)) {
                return MYMAGENTA;
            } else if (attr.getBackColor().equals(jexer.bits.Color.WHITE)) {
                return MYWHITE;
            }
            throw new IllegalArgumentException("Invalid color: " + attr.getBackColor().getValue());
        }

        /**
         * Public constructor.
         *
         * @param screen the Screen that Backend talks to
         */
        public SwingFrame(final SwingScreen screen) {
            this.screen = screen;
            setDOSColors();

            // Figure out my cursor style
            String cursorStyleString = System.getProperty("jexer.Swing.cursorStyle",
                "underline").toLowerCase();

            if (cursorStyleString.equals("underline")) {
                cursorStyle = CursorStyle.UNDERLINE;
            } else if (cursorStyleString.equals("outline")) {
                cursorStyle = CursorStyle.OUTLINE;
            } else if (cursorStyleString.equals("block")) {
                cursorStyle = CursorStyle.BLOCK;
            }

            setTitle("Jexer Application");
            setBackground(Color.black);

            try {
                // Always try to use Terminus, the one decent font.
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream in = loader.getResourceAsStream(FONTFILE);
                Font terminusRoot = Font.createFont(Font.TRUETYPE_FONT, in);
                Font terminus = terminusRoot.deriveFont(Font.PLAIN, 22);
                setFont(terminus);
            } catch (Exception e) {
                e.printStackTrace();
                // setFont(new Font("Liberation Mono", Font.PLAIN, 24));
                setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
            }
            pack();

            // Kill the X11 cursor
            // Transparent 16 x 16 pixel cursor image.
            BufferedImage cursorImg = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_ARGB);
            // Create a new blank cursor.
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
            setCursor(blankCursor);

            // Be capable of seeing Tab / Shift-Tab
            setFocusTraversalKeysEnabled(false);

            // Save the text cell width/height
            getFontDimensions();

            // Setup double-buffering
            if (SwingScreen.doubleBuffer) {
                setIgnoreRepaint(true);
                createBufferStrategy(2);
                bufferStrategy = getBufferStrategy();
            }
        }

        /**
         * Figure out my font dimensions.
         */
        private void getFontDimensions() {
            Graphics gr = getGraphics();
            FontMetrics fm = gr.getFontMetrics();
            maxDescent = fm.getMaxDescent();
            Rectangle2D bounds = fm.getMaxCharBounds(gr);
            int leading = fm.getLeading();
            textWidth = (int)Math.round(bounds.getWidth());
            textHeight = (int)Math.round(bounds.getHeight()) - maxDescent;
            // This also produces the same number, but works better for ugly
            // monospace.
            textHeight = fm.getMaxAscent() + maxDescent - leading;

            if (System.getProperty("os.name").startsWith("Windows")) {
                textAdjustY = -1;
                textAdjustX = 0;
            }
        }

        /**
         * Resize to font dimensions.
         */
        public void resizeToScreen() {
            // Figure out the thickness of borders and use that to set the
            // final size.
            Insets insets = getInsets();
            left = insets.left;
            top = insets.top;

            setSize(textWidth * screen.width + insets.left + insets.right,
                textHeight * screen.height + insets.top + insets.bottom);
        }

        /**
         * Update redraws the whole screen.
         *
         * @param gr the Swing Graphics context
         */
        @Override
        public void update(final Graphics gr) {
            // The default update clears the area.  Don't do that, instead
            // just paint it directly.
            paint(gr);
        }

        /**
         * Paint redraws the whole screen.
         *
         * @param gr the Swing Graphics context
         */
        @Override
        public void paint(final Graphics gr) {
            // Do nothing until the screen reference has been set.
            if (screen == null) {
                return;
            }
            if (screen.frame == null) {
                return;
            }

            // See if it is time to flip the blink time.
            long nowTime = (new Date()).getTime();
            if (nowTime > blinkMillis + lastBlinkTime) {
                lastBlinkTime = nowTime;
                cursorBlinkVisible = !cursorBlinkVisible;
            }

            int xCellMin = 0;
            int xCellMax = screen.width;
            int yCellMin = 0;
            int yCellMax = screen.height;

            Rectangle bounds = gr.getClipBounds();
            if (bounds != null) {
                // Only update what is in the bounds
                xCellMin = screen.textColumn(bounds.x);
                xCellMax = screen.textColumn(bounds.x + bounds.width);
                if (xCellMax > screen.width) {
                    xCellMax = screen.width;
                }
                if (xCellMin >= xCellMax) {
                    xCellMin = xCellMax - 2;
                }
                if (xCellMin < 0) {
                    xCellMin = 0;
                }
                yCellMin = screen.textRow(bounds.y);
                yCellMax = screen.textRow(bounds.y + bounds.height);
                if (yCellMax > screen.height) {
                    yCellMax = screen.height;
                }
                if (yCellMin >= yCellMax) {
                    yCellMin = yCellMax - 2;
                }
                if (yCellMin < 0) {
                    yCellMin = 0;
                }
            } else {
                // We need a total repaint
                reallyCleared = true;
            }

            // Prevent updates to the screen's data from the TApplication
            // threads.
            synchronized (screen) {
                /*
                System.err.printf("bounds %s X %d %d Y %d %d\n",
                    bounds, xCellMin, xCellMax, yCellMin, yCellMax);
                 */

                for (int y = yCellMin; y < yCellMax; y++) {
                    for (int x = xCellMin; x < xCellMax; x++) {

                        int xPixel = x * textWidth + left;
                        int yPixel = y * textHeight + top;

                        Cell lCell = screen.logical[x][y];
                        Cell pCell = screen.physical[x][y];

                        if (!lCell.equals(pCell)
                            || lCell.isBlink()
                            || reallyCleared) {

                            Cell lCellColor = new Cell();
                            lCellColor.setTo(lCell);

                            // Check for reverse
                            if (lCell.isReverse()) {
                                lCellColor.setForeColor(lCell.getBackColor());
                                lCellColor.setBackColor(lCell.getForeColor());
                            }

                            // Draw the background rectangle, then the
                            // foreground character.
                            gr.setColor(attrToBackgroundColor(lCellColor));
                            gr.fillRect(xPixel, yPixel, textWidth, textHeight);

                            // Handle blink and underline
                            if (!lCell.isBlink()
                                || (lCell.isBlink() && cursorBlinkVisible)
                            ) {
                                gr.setColor(attrToForegroundColor(lCellColor));
                                char [] chars = new char[1];
                                chars[0] = lCell.getChar();
                                gr.drawChars(chars, 0, 1, xPixel + textAdjustX,
                                    yPixel + textHeight - maxDescent
                                    + textAdjustY);

                                if (lCell.isUnderline()) {
                                    gr.fillRect(xPixel, yPixel + textHeight - 2,
                                        textWidth, 2);
                                }
                            }

                            // Physical is always updated
                            physical[x][y].setTo(lCell);
                        }
                    }
                }

                // Draw the cursor if it is visible
                if (cursorVisible
                    && (cursorY <= screen.height - 1)
                    && (cursorX <= screen.width - 1)
                    && cursorBlinkVisible
                ) {
                    int xPixel = cursorX * textWidth + left;
                    int yPixel = cursorY * textHeight + top;
                    Cell lCell = screen.logical[cursorX][cursorY];
                    gr.setColor(attrToForegroundColor(lCell));
                    switch (cursorStyle) {
                    default:
                        // Fall through...
                    case UNDERLINE:
                        gr.fillRect(xPixel, yPixel + textHeight - 2,
                            textWidth, 2);
                        break;
                    case BLOCK:
                        gr.fillRect(xPixel, yPixel, textWidth, textHeight);
                        break;
                    case OUTLINE:
                        gr.drawRect(xPixel, yPixel, textWidth - 1,
                            textHeight - 1);
                        break;
                    }
                }

                dirty = false;
                reallyCleared = false;
            } // synchronized (screen)
        }

    } // class SwingFrame

    /**
     * The raw Swing JFrame.  Note package private access.
     */
    SwingFrame frame;

    /**
     * Restore terminal to normal state.
     */
    public void shutdown() {
        frame.dispose();
    }

    /**
     * Public constructor.
     */
    public SwingScreen() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    SwingScreen.this.frame = new SwingFrame(SwingScreen.this);
                    SwingScreen.this.sessionInfo =
                        new SwingSessionInfo(SwingScreen.this.frame,
                            frame.textWidth,
                            frame.textHeight);

                    SwingScreen.this.setDimensions(sessionInfo.getWindowWidth(),
                        sessionInfo.getWindowHeight());

                    SwingScreen.this.frame.resizeToScreen();
                    SwingScreen.this.frame.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The sessionInfo.
     */
    private SwingSessionInfo sessionInfo;

    /**
     * Create the SwingSessionInfo.  Note package private access.
     *
     * @return the sessionInfo
     */
    SwingSessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Push the logical screen to the physical device.
     */
    @Override
    public void flushPhysical() {

        if (reallyCleared) {
            // Really refreshed, do it all
            if (SwingScreen.doubleBuffer) {
                Graphics gr = frame.bufferStrategy.getDrawGraphics();
                frame.paint(gr);
                gr.dispose();
                frame.bufferStrategy.show();
                Toolkit.getDefaultToolkit().sync();
            } else {
                frame.repaint();
            }
            return;
        }

        // Do nothing if nothing happened.
        if (!dirty) {
            return;
        }

        // Request a repaint, let the frame's repaint/update methods do the
        // right thing.

        // Find the minimum-size damaged region.
        int xMin = frame.getWidth();
        int xMax = 0;
        int yMin = frame.getHeight();
        int yMax = 0;

        synchronized (this) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Cell lCell = logical[x][y];
                    Cell pCell = physical[x][y];

                    int xPixel = x * frame.textWidth + frame.left;
                    int yPixel = y * frame.textHeight + frame.top;

                    if (!lCell.equals(pCell)
                        || ((x == cursorX)
                            && (y == cursorY)
                            && cursorVisible)
                        || lCell.isBlink()
                    ) {
                        if (xPixel < xMin) {
                            xMin = xPixel;
                        }
                        if (xPixel + frame.textWidth > xMax) {
                            xMax = xPixel + frame.textWidth;
                        }
                        if (yPixel < yMin) {
                            yMin = yPixel;
                        }
                        if (yPixel + frame.textHeight > yMax) {
                            yMax = yPixel + frame.textHeight;
                        }
                    }
                }
            }
        }
        if (xMin + frame.textWidth >= xMax) {
            xMax += frame.textWidth;
        }
        if (yMin + frame.textHeight >= yMax) {
            yMax += frame.textHeight;
        }

        // Repaint the desired area
        // System.err.printf("REPAINT X %d %d Y %d %d\n", xMin, xMax,
        //     yMin, yMax);
        if (SwingScreen.doubleBuffer) {
            Graphics gr = frame.bufferStrategy.getDrawGraphics();
            Rectangle bounds = new Rectangle(xMin, yMin, xMax - xMin,
                yMax - yMin);
            gr.setClip(bounds);
            frame.paint(gr);
            gr.dispose();
            frame.bufferStrategy.show();
            Toolkit.getDefaultToolkit().sync();
        } else {
            frame.repaint(xMin, yMin, xMax - xMin, yMax - yMin);
        }
    }

    /**
     * Put the cursor at (x,y).
     *
     * @param visible if true, the cursor should be visible
     * @param x column coordinate to put the cursor on
     * @param y row coordinate to put the cursor on
     */
    @Override
    public void putCursor(final boolean visible, final int x, final int y) {

        if ((visible == cursorVisible) && ((x == cursorX) && (y == cursorY))) {
            // See if it is time to flip the blink time.
            long nowTime = (new Date()).getTime();
            if (nowTime < frame.blinkMillis + frame.lastBlinkTime) {
                // Nothing has changed, so don't do anything.
                return;
            }
        }

        if (cursorVisible
            && (cursorY <= height - 1)
            && (cursorX <= width - 1)
        ) {
            // Make the current cursor position dirty
            if (physical[cursorX][cursorY].getChar() == 'Q') {
                physical[cursorX][cursorY].setChar('X');
            } else {
                physical[cursorX][cursorY].setChar('Q');
            }
        }

        super.putCursor(visible, x, y);
    }

    /**
     * Convert pixel column position to text cell column position.
     *
     * @param x pixel column position
     * @return text cell column position
     */
    public int textColumn(final int x) {
        return ((x - frame.left) / frame.textWidth);
    }

    /**
     * Convert pixel row position to text cell row position.
     *
     * @param y pixel row position
     * @return text cell row position
     */
    public int textRow(final int y) {
        return ((y - frame.top) / frame.textHeight);
    }

}
