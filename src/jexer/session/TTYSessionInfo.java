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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * TTYSessionInfo queries environment variables and the tty window size for
 * the session information.  The username is taken from
 * getpwuid(geteuid()).pw_name, language is taken from LANG, and text window
 * size from ioctl(TIOCGWINSIZ).
 */
public class TTYSessionInfo implements SessionInfo {

    /**
     * User name
     */
    private String username = "";

    /**
     * Language
     */
    private String language = "";

    /**
     * Text window width.  Default is 80x24 (same as VT100-ish terminals).
     */
    private int windowWidth = 80;

    /**
     * Text window height.  Default is 80x24 (same as VT100-ish terminals).
     */
    private int windowHeight = 24;

    /**
     * Time at which the window size was refreshed
     */
    private Date lastQueryWindowTime;
    
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
     * Call 'stty size' to obtain the tty window size.  windowWidth and
     * windowHeight are set automatically.
     */
    private void sttyWindowSize() {
	String [] cmd = {
	    "/bin/sh", "-c", "stty size < /dev/tty"
	};
	try {
	    System.out.println("spawn stty");

	    Process process = Runtime.getRuntime().exec(cmd);
	    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
	    String line = in.readLine();
	    if ((line != null) && (line.length() > 0)) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		windowHeight = Integer.parseInt(tokenizer.nextToken());
		windowWidth = Integer.parseInt(tokenizer.nextToken());
	    }
	    while (true) {
		BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
		line = err.readLine();
		if ((line != null) && (line.length() > 0)) {
		    System.err.println("Error output from stty: " + line);
		}
		try{
		    process.waitFor();
		    break;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    int rc = process.exitValue();
	    if (rc != 0) {
		System.err.println("stty returned error code: " + rc);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Text window width getter
     *
     * @return the window width
     */
    public int getWindowWidth() {
	if (System.getProperty("os.name").startsWith("Windows")) {
	    // Always use 80x25 for Windows (same as DOS)
	    return 80;
	}
	return windowWidth;
    }

    /**
     * Text window height getter
     *
     * @return the window height
     */
    public int getWindowHeight() {
	if (System.getProperty("os.name").startsWith("Windows")) {
	    // Always use 80x25 for Windows (same as DOS)
	    return 25;
	}
	return windowHeight;
    }

    /**
     * Re-query the text window size
     */
    public void queryWindowSize() {
	if (lastQueryWindowTime == null) {
	    lastQueryWindowTime = new Date();
	} else {
	    Date now = new Date();
	    if (now.getTime() - lastQueryWindowTime.getTime() < 3000) {
		// Don't re-spawn stty, it's been too soon.
		return;
	    }
	}
	if (System.getProperty("os.name").startsWith("Linux") ||
	    System.getProperty("os.name").startsWith("Mac OS X") ||
	    System.getProperty("os.name").startsWith("SunOS") ||
	    System.getProperty("os.name").startsWith("FreeBSD")
	) {
	    // Use stty to get the window size
	    sttyWindowSize();
	}
    }

    /**
     * Public constructor
     */
    public TTYSessionInfo() {
	// Populate lang and user from the environment
	username = System.getProperty("user.name");
	language = System.getProperty("user.language");
	queryWindowSize();
    }
}
