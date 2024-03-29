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
package jexer.session;

/**
 * SessionInfo is used to store per-session properties that are determined at
 * different layers of the communication stack.
 */
public interface SessionInfo {

    /**
     * Username getter.
     *
     * @return the username
     */
    public String getUsername();

    /**
     * Username setter.
     *
     * @param username the value
     */
    public void setUsername(String username);

    /**
     * Language getter.
     *
     * @return the language
     */
    public String getLanguage();

    /**
     * Language setter.
     *
     * @param language the value
     */
    public void setLanguage(String language);

    /**
     * Text window width getter.
     *
     * @return the window width
     */
    public int getWindowWidth();

    /**
     * Text window height getter.
     *
     * @return the window height
     */
    public int getWindowHeight();

    /**
     * Re-query the text window size.
     */
    public void queryWindowSize();
}
