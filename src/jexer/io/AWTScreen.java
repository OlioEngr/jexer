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
package jexer.io;

import jexer.bits.Cell;
import jexer.bits.CellAttributes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 * This Screen implementation draws to a Java AWT Frame.
 */
public final class AWTScreen extends Screen {

    /**
     * AWTFrame is our top-level hook into the AWT system.
     */
    class AWTFrame extends Frame {

        /**
         * The TUI Screen data.
         */
        AWTScreen screen;

        /**
         * Width of a character cell.
         */
        private int textWidth = 1;

        /**
         * Height of a character cell.
         */
        private int textHeight = 1;

        /**
         * Top pixel value.
         */
        private int top = 30;
        
        /**
         * Left pixel value.
         */
        private int left = 30;
         
        /**
         * Public constructor.
         */
        public AWTFrame() {
            setTitle("Jexer Application");
            setBackground(java.awt.Color.black);
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            setFont(new Font("Liberation Mono", Font.BOLD, 16));
            // setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            setSize(100, 100);
            setVisible(true);
        }

        /**
         * Resize to font dimensions.
         */
        public void resizeToScreen() {
            Graphics gr        = getGraphics();
            FontMetrics fm     = gr.getFontMetrics();
            textWidth          = fm.charWidth('m');
            textHeight         = fm.getHeight();
            setSize((textWidth + 1) * screen.width + (2 * left),
                (textHeight + 1) * screen.height + (2 * top));

            System.err.printf("W: %d H: %d\n", textWidth, textHeight);
        }

        /**
         * Paint redraws the whole screen.
         *
         * @param gr the AWT Graphics context
         */
        @Override
        public void paint(Graphics gr) {

            for (int y = 0; y < screen.height; y++) {
                for (int x = 0; x < screen.width; x++) {
                    Cell lCell = screen.logical[x][y];
                    Cell pCell = screen.physical[x][y];

                    int xPixel = x * (textWidth + 1) + left;
                    int yPixel = y * (textHeight + 1) + top - y;

                    if (!lCell.equals(pCell)) {
                        // Draw the background rectangle, then the foreground
                        // character.
                        if (lCell.getBackColor().equals(jexer.bits.Color.BLACK)) {
                            gr.setColor(Color.black);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.RED)) {
                            gr.setColor(Color.red);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.BLUE)) {
                            gr.setColor(Color.blue);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.GREEN)) {
                            gr.setColor(Color.green);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.YELLOW)) {
                            gr.setColor(Color.yellow);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.CYAN)) {
                            gr.setColor(Color.cyan);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.MAGENTA)) {
                            gr.setColor(Color.magenta);
                        } else if (lCell.getBackColor().equals(jexer.bits.Color.WHITE)) {
                            gr.setColor(Color.white);
                        }
                        gr.fillRect(xPixel, yPixel, textWidth + 1,
                            textHeight + 2);

                        if (lCell.getForeColor().equals(jexer.bits.Color.BLACK)) {
                            gr.setColor(Color.black);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.RED)) {
                            gr.setColor(Color.red);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.BLUE)) {
                            gr.setColor(Color.blue);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.GREEN)) {
                            gr.setColor(Color.green);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.YELLOW)) {
                            gr.setColor(Color.yellow);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.CYAN)) {
                            gr.setColor(Color.cyan);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.MAGENTA)) {
                            gr.setColor(Color.magenta);
                        } else if (lCell.getForeColor().equals(jexer.bits.Color.WHITE)) {
                            gr.setColor(Color.white);
                        }
                        char [] chars = new char[1];
                        chars[0] = lCell.getChar();
                        gr.drawChars(chars, 0, 1, xPixel,
                            yPixel + textHeight - 2);

                        // Physical is always updated
                        physical[x][y].setTo(lCell);
                    }
                }
            }
        }
    }

    /**
     * The raw AWT Frame.
     */
    private AWTFrame frame;

    /**
     * Public constructor.
     */
    public AWTScreen() {
        frame = new AWTFrame();
        frame.screen = this;
        frame.resizeToScreen();
    }

    /**
     * Push the logical screen to the physical device.
     */
    @Override
    public void flushPhysical() {
        Graphics gr = frame.getGraphics();
        frame.paint(gr);
    }
}
