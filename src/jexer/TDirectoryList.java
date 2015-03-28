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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TDirectoryList shows the files within a directory.
 */
public class TDirectoryList extends TWidget {

    /**
     * Files in the directory.
     */
    private List<File> files;

    /**
     * Selected file.
     */
    private int selectedFile = -1;

    /**
     * Root path containing files to display.
     */
    public File path;

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
     * The action to perform when the user selects an item.
     */
    private TAction action = null;

    /**
     * Perform user selection action.
     */
    public void dispatch() {
        assert (selectedFile >= 0);
        assert (selectedFile < files.size());
        if (action != null) {
            action.DO();
        }
    }

    /**
     * Format one of the entries for drawing on the screen.
     *
     * @param index index into files
     * @return the line to draw
     */
    private String renderFile(int index) {
        File file = files.get(index);
        String name = file.getName();
        if (name.length() > 20) {
            name = name.substring(0, 17) + "...";
        }
        return String.format("%-20s %5dk", name, (file.length() / 1024));
    }

    /**
     * Resize for a new width/height.
     */
    public void reflow() {

        // Reset the lines
        selectedFile = -1;
        maxLineWidth = 0;
        files.clear();

        // Build a list of files in this directory
        File [] newFiles = path.listFiles();
        for (int i = 0; i < newFiles.length; i++) {
            if (newFiles[i].getName().startsWith(".")) {
                continue;
            }
            if (newFiles[i].isDirectory()) {
                continue;
            }
            files.add(newFiles[i]);
        }

        for (int i = 0; i < files.size(); i++) {
            String line = renderFile(i);
            if (line.length() > maxLineWidth) {
                maxLineWidth = line.length();
            }
        }

        // Start at the top
        if (vScroller == null) {
            vScroller = new TVScroller(this, getWidth() - 1, 0,
                getHeight() - 1);
        } else {
            vScroller.setX(getWidth() - 1);
            vScroller.setHeight(getHeight() - 1);
        }
        vScroller.setBottomValue(files.size() - getHeight() - 1);
        vScroller.setTopValue(0);
        vScroller.setValue(0);
        if (vScroller.getBottomValue() < 0) {
            vScroller.setBottomValue(0);
        }
        vScroller.setBigChange(getHeight() - 1);

        // Start at the left
        if (hScroller == null) {
            hScroller = new THScroller(this, 0, getHeight() - 1,
                getWidth() - 1);
        } else {
            hScroller.setY(getHeight() - 1);
            hScroller.setWidth(getWidth() - 1);
        }
        hScroller.setRightValue(maxLineWidth - getWidth() + 1);
        hScroller.setLeftValue(0);
        hScroller.setValue(0);
        if (hScroller.getRightValue() < 0) {
            hScroller.setRightValue(0);
        }
        hScroller.setBigChange(getWidth() - 1);
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

        this.path   = new File(path);
        this.action = action;
        files = new ArrayList<File>();
        reflow();
    }

    /**
     * Draw the files list.
     */
    @Override
    public void draw() {
        CellAttributes color = null;
        int begin = vScroller.getValue();
        int topY = 0;
        for (int i = begin; i < files.size() - 1; i++) {
            String line = renderFile(i);
            if (hScroller.getValue() < line.length()) {
                line = line.substring(hScroller.getValue());
            } else {
                line = "";
            }
            if (i == selectedFile) {
                color = getTheme().getColor("tdirectorylist.selected");
            } else if (isAbsoluteActive()) {
                color = getTheme().getColor("tdirectorylist");
            } else {
                color = getTheme().getColor("tdirectorylist.inactive");
            }
            String formatString = "%-" + Integer.toString(getWidth() - 1) + "s";
            getScreen().putStringXY(0, topY, String.format(formatString, line),
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
        if (mouse.isMouseWheelUp()) {
            vScroller.decrement();
            return;
        }
        if (mouse.isMouseWheelDown()) {
            vScroller.increment();
            return;
        }

        if ((mouse.getX() < getWidth() - 1)
            && (mouse.getY() < getHeight() - 1)) {
            if (vScroller.getValue() + mouse.getY() < files.size()) {
                selectedFile = vScroller.getValue() + mouse.getY();
            }
            path = files.get(selectedFile);
            dispatch();
            return;
        }

        // Pass to children
        super.onMouseDown(mouse);
    }

    /**
     * Handle mouse release events.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
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
