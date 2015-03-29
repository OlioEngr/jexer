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
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

/**
 * TDirectoryTreeItem is a single item in a disk directory tree view.
 */
public class TDirectoryTreeItem extends TTreeItem {

    /**
     * File corresponding to this list item.
     */
    private File file;

    /**
     * Get the File corresponding to this list item.
     *
     * @return the File
     */
    public final File getFile() {
        return file;
    }

    /**
     * Called when this item is expanded or collapsed.  this.expanded will be
     * true if this item was just expanded from a mouse click or keypress.
     */
    @Override
    public void onExpand() {
        // System.err.printf("onExpand() %s\n", file);

        if (file == null) {
            return;
        }
        getChildren().clear();

        // Make sure we can read it before trying to.
        if (file.canRead()) {
            setSelectable(true);
        } else {
            setSelectable(false);
        }
        assert (file.isDirectory());
        setExpandable(true);

        if ((isExpanded() == false) || (isExpandable() == false)) {
            getTreeView().reflow();
            return;
        }

        for (File f: file.listFiles()) {
            // System.err.printf("   -> file %s %s\n", file, file.getName());

            if (f.getName().startsWith(".")) {
                // Hide dot-files
                continue;
            }
            if (!f.isDirectory()) {
                continue;
            }

            try {
                TDirectoryTreeItem item = new TDirectoryTreeItem(getTreeView(),
                    f.getCanonicalPath(), false, false);

                item.level = this.level + 1;
                getChildren().add(item);
            } catch (IOException e) {
                continue;
            }
        }
        Collections.sort(getChildren());

        getTreeView().reflow();
    }

    /**
     * Add a child item.  This method should never be used, it will throw an
     * IllegalArgumentException every time.
     *
     * @param text text for this item
     * @param expanded if true, have it expanded immediately
     * @return the new item
     * @throws IllegalArgumentException if this function is called
     */
    @Override
    public final TTreeItem addChild(final String text, final boolean expanded) {
        throw new IllegalArgumentException("Do not call addChild(), use onExpand() instead");
    }

    /**
     * Public constructor.
     *
     * @param view root TTreeView
     * @param text text for this item
     */
    public TDirectoryTreeItem(final TTreeView view,
        final String text) throws IOException {

        this(view, text, false, true);
    }

    /**
     * Public constructor.
     *
     * @param view root TTreeView
     * @param text text for this item
     * @param expanded if true, have it expanded immediately
     */
    public TDirectoryTreeItem(final TTreeView view, final String text,
        final boolean expanded) throws IOException {

        this(view, text, expanded, true);
    }

    /**
     * Public constructor.
     *
     * @param view root TTreeView
     * @param text text for this item
     * @param expanded if true, have it expanded immediately
     * @param openParents if true, expand all paths up the root path and
     * return the root path entry
     */
    public TDirectoryTreeItem(final TTreeView view, final String text,
        final boolean expanded, final boolean openParents) throws IOException {

        super(view, text, false);

        List<String> parentFiles = new LinkedList<String>();
        boolean oldExpanded = expanded;

        // Convert to canonical path
        File rootFile = new File(text);
        rootFile = rootFile.getCanonicalFile();

        if (openParents == true) {
            setExpanded(true);

            // Go up the directory tree
            File parent = rootFile.getParentFile();
            while (parent != null) {
                parentFiles.add(rootFile.getName());
                rootFile = rootFile.getParentFile();
                parent = rootFile.getParentFile();
            }
        }
        file = rootFile;
        if (rootFile.getParentFile() == null) {
            // This is a filesystem root, use its full name
            setText(rootFile.getCanonicalPath());
        } else {
            // This is a relative path.  We got here because openParents was
            // false.
            assert (openParents == false);
            setText(rootFile.getName());
        }
        onExpand();

        if (openParents == true) {
            TDirectoryTreeItem childFile = this;
            Collections.reverse(parentFiles);
            for (String p: parentFiles) {
                for (TWidget widget: childFile.getChildren()) {
                    TDirectoryTreeItem child = (TDirectoryTreeItem) widget;
                    if (child.getText().equals(p)) {
                        childFile = child;
                        childFile.setExpanded(true);
                        childFile.onExpand();
                        break;
                    }
                }
            }
            unselect();
            getTreeView().setSelected(childFile);
            setExpanded(oldExpanded);
        }
        getTreeView().reflow();
    }
}
