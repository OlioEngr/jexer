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
import java.io.IOException;

import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import static jexer.TKeypress.*;

/**
 * TFileOpenBox is a system-modal dialog for selecting a file to open.  Call
 * it like:
 *
 * <p>
 * <pre>
 * {@code
 *     filename = application.fileOpenBox("/path/to/file.ext",
 *         TFileOpenBox.Type.OPEN);
 *     if (filename != null) {
 *         ... the user selected a file, go open it ...
 *     }
 * }
 * </pre>
 *
 */
public final class TFileOpenBox extends TWindow {

    /**
     * TFileOpenBox can be called for either Open or Save actions.
     */
    public enum Type {
        OPEN,
        SAVE
    }

    /**
     * String to return, or null if the user canceled.
     */
    private String filename = null;

    /**
     * Get the return string.
     *
     * @return the filename the user selected, or null if they canceled.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * The left-side tree view pane.
     */
    private TTreeView treeView;

    /**
     * The data behind treeView.
     */
    private TDirectoryTreeItem treeViewRoot;

    /**
     * The right-side directory list pane.
     */
    @SuppressWarnings("unused")
    private TDirectoryList directoryList;

    /**
     * The top row text field.
     */
    private TField entryField;

    /**
     * The Open or Save button.
     */
    private TButton openButton;

    /**
     * Update the fields in response to other field updates.
     *
     * @param enter if true, the user manually entered a filename
     */
    @SuppressWarnings("unused")
    private void onUpdate(boolean enter) throws IOException {
        String newFilename = entryField.getText();
        File newFile = new File(newFilename);
        if (newFile.exists()) {
            if (enter) {
                if (newFile.isFile()) {
                    filename = entryField.getText();
                    getApplication().closeWindow(this);
                }
                if (newFile.isDirectory()) {
                    treeViewRoot = new TDirectoryTreeItem(treeView,
                        entryField.getText(), true);
                    treeView.setTreeRoot(treeViewRoot, true);
                    treeView.reflow();
                }
                openButton.setEnabled(false);
            } else {
                if (newFile.isFile()) {
                    openButton.setEnabled(true);
                } else {
                    openButton.setEnabled(false);
                }
            }
        } else {
            openButton.setEnabled(false);
        }
    }

    /**
     * Public constructor.  The file open box will be centered on screen.
     *
     * @param application the TApplication that manages this window
     * @param path path of selected file
     * @param type one of the Type constants
     */
    public TFileOpenBox(final TApplication application, final String path,
        final Type type) throws IOException {

        // Register with the TApplication
        super(application, "", 0, 0, 76, 22, MODAL);

        // Add text field
        entryField = addField(1, 1, getWidth() - 4, false,
            (new File(path)).getCanonicalPath(),
            new TAction() {
                public void DO() {}
            }, null);

        // Add directory treeView
        treeView = addTreeView(1, 3, 30, getHeight() - 6,
            new TAction() {
                public void DO() {}
            }
        );
        treeViewRoot = new TDirectoryTreeItem(treeView, path, true);

        // Add directory files list
        directoryList = addDirectoryList(path, 34, 3, 28, getHeight() - 6,
            new TAction() {
                public void DO() {}
            }
        );

        String openLabel = "";
        switch (type) {
        case OPEN:
            openLabel = " &Open ";
            setTitle("Open File...");
            break;
        case SAVE:
            openLabel = " &Save ";
            setTitle("Save File...");
            break;
        default:
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Setup button actions
        openButton = addButton(openLabel, this.getWidth() - 12, 3,
            new TAction() {
                public void DO() {}
            }
        );
        openButton.setEnabled(false);

        addButton("&Cancel", getWidth() - 12, 5,
            new TAction() {
                public void DO() {
                    filename = null;
                    getApplication().closeWindow(TFileOpenBox.this);
                }
            }
        );

        // Set the secondaryFiber to run me
        getApplication().enableSecondaryEventReceiver(this);

        // Yield to the secondary thread.  When I come back from the
        // constructor response will already be set.
        getApplication().yield();
    }

    /**
     * Draw me on screen.
     */
    @Override
    public void draw() {
        super.draw();
        getScreen().vLineXY(33, 4, getHeight() - 6, GraphicsChars.WINDOW_SIDE,
            getBackground());
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
            // Close window
            filename = null;
            getApplication().closeWindow(this);
            return;
        }

        // Pass to my parent
        super.onKeypress(keypress);
    }

}
