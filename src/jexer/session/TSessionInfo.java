/**
 * Jexer - Java Text User Interface
 *
 * Version: $Id$
 *
 * Author: Kevin Lamonte, <a href="mailto:kevin.lamonte@gmail.com">kevin.lamonte@gmail.com</a>
 *
 * License: LGPLv3 or later
 *
 * Copyright: This module is licensed under the GNU Lesser General
 * Public License Version 3.  Please see the file "COPYING" in this
 * directory for more information about the GNU Lesser General Public
 * License Version 3.
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
 */
package jexer.session;

/**
 * TSessionInfo provides a default session implementation.  The username is
 * blank, language is "en_US", with a 80x24 text window.
 */
public class TSessionInfo implements SessionInfo {

    /**
     * User name
     */
    private String username = "";

    /**
     * Language
     */
    private String language = "en_US";

    /**
     * Text window width
     */
    private int windowWidth = 80;

    /**
     * Text window height
     */
    private int windowHeight = 24;

    /**
     * Username getter
     *
     * @return the username
     */
    public String getUsername() {
	return this.username;
    }

    /**
     * Username setter
     *
     * @param username the value
     */
    public void setUsername(String username) {
	this.username = username;
    }

    /**
     * Language getter
     *
     * @return the language
     */
    public String getLanguage() {
	return this.language;
    }

    /**
     * Language setter
     *
     * @param language the value
     */
    public void setLanguage(String language) {
	this.language = language;
    }

    /**
     * Text window width getter
     */
    public int getWindowWidth() {
	return windowWidth;
    }

    /**
     * Text window height getter
     */
    public int getWindowHeight() {
	return windowHeight;
    }
}
