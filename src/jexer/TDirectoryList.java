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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TDirectoryList shows the files within a directory.
 */
public final class TDirectoryList extends TList {

    /**
     * Files in the directory.
     */
    private List<File> files;

    /**
     * Root path containing files to display.
     */
    private File path;

    /**
     * Set the new path to display.
     *
     * @param path new path to list files for
     */
    public void setPath(final String path) {
        this.path = new File(path);

        List<String> newStrings = new ArrayList<String>();
        files.clear();

        // Build a list of files in this directory
        File [] newFiles = this.path.listFiles();
        if (newFiles != null) {
            for (int i = 0; i < newFiles.length; i++) {
                if (newFiles[i].getName().startsWith(".")) {
                    continue;
                }
                if (newFiles[i].isDirectory()) {
                    continue;
                }
                files.add(newFiles[i]);
                newStrings.add(renderFile(files.size() - 1));
            }
        }
        setList(newStrings);
    }

    /**
     * Get the path that is being displayed.
     *
     * @return the path
     */
    public File getPath() {
        path = files.get(getSelectedIndex());
        return path;
    }

    /**
     * Format one of the entries for drawing on the screen.
     *
     * @param index index into files
     * @return the line to draw
     */
    private String renderFile(final int index) {
        File file = files.get(index);
        String name = file.getName();
        if (name.length() > 20) {
            name = name.substring(0, 17) + "...";
        }
        return String.format("%-20s %5dk", name, (file.length() / 1024));
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param path directory path, must be a directory
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     */
    public TDirectoryList(final TWidget parent, final String path, final int x,
        final int y, final int width, final int height) {

        this(parent, path, x, y, width, height, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param path directory path, must be a directory
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     * @param action action to perform when an item is selected
     */
    public TDirectoryList(final TWidget parent, final String path, final int x,
        final int y, final int width, final int height, final TAction action) {

        super(parent, null, x, y, width, height, action);
        files = new ArrayList<File>();
        setPath(path);
    }

}
