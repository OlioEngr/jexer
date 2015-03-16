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
package jexer.session;

import java.awt.Frame;
import java.awt.Insets;

/**
 * AWTSessionInfo provides a session implementation with a callback into an
 * AWT Frame to support queryWindowSize().  The username is blank, language
 * is "en_US", with a 80x24 text window.
 */
public final class AWTSessionInfo implements SessionInfo {

    /**
     * The AWT Frame.
     */
    private Frame frame;

    /**
     * The width of a text cell in pixels.
     */
    private int textWidth;

    /**
     * The height of a text cell in pixels.
     */
    private int textHeight;

    /**
     * User name.
     */
    private String username = "";

    /**
     * Language.
     */
    private String language = "en_US";

    /**
     * Text window width.
     */
    private int windowWidth = 80;

    /**
     * Text window height.
     */
    private int windowHeight = 24;

    /**
     * Username getter.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Username setter.
     *
     * @param username the value
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Language getter.
     *
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Language setter.
     *
     * @param language the value
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Text window width getter.
     *
     * @return the window width
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * Text window height getter.
     *
     * @return the window height
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * Public constructor.
     *
     * @param frame the AWT Frame
     * @param textWidth the width of a cell in pixels
     * @param textHeight the height of a cell in pixels
     */
    public AWTSessionInfo(final Frame frame, final int textWidth,
        final int textHeight) {

        this.frame      = frame;
        this.textWidth  = textWidth;
        this.textHeight = textHeight;
    }

    /**
     * Re-query the text window size.
     */
    public void queryWindowSize() {
        Insets insets = frame.getInsets();
        int height = frame.getHeight() - insets.top - insets.bottom;
        int width = frame.getWidth() - insets.left - insets.right;
        windowWidth = width / textWidth;
        windowHeight = height / textHeight;

        /*
        System.err.printf("queryWindowSize(): frame %d %d window %d %d\n",
            frame.getWidth(), frame.getHeight(),
            windowWidth, windowHeight);
         */

    }

    /**
     * Convert pixel column position to text cell column position.
     *
     * @param x pixel column position
     * @return text cell column position
     */
    public int textColumn(final int x) {
        Insets insets = frame.getInsets();
        return ((x - insets.left) / textWidth);
    }

    /**
     * Convert pixel row position to text cell row position.
     *
     * @param y pixel row position
     * @return text cell row position
     */
    public int textRow(final int y) {
        Insets insets = frame.getInsets();
        return ((y - insets.top) / textHeight);
    }

}
