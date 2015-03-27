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
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

/**
 * TDirectoryTreeItem is a single item in a disk directory tree view.
 */
public class TDirectoryTreeItem extends TTreeItem {

    /**
     * Directory entry corresponding to this list item.
     */
    File dir;

    /**
     * Called when this item is expanded or collapsed.  this.expanded will be
     * true if this item was just expanded from a mouse click or keypress.
     */
    @Override
    public void onExpand() {
        if (dir == null) {
            return;
        }
        getChildren().clear();

        // Make sure we can read it before trying to.
        if (dir.canRead()) {
            setSelectable(true);
        } else {
            setSelectable(false);
        }
        assert (dir.isDirectory());
        setExpandable(true);

        if ((isExpanded() == false) || (isExpandable() == false)) {
            getTreeView().reflow();
            return;
        }

        // Refresh my child list
        for (File file: dir.listFiles()) {
            if (file.getName().equals(".")) {
                continue;
            }
            if (!file.isDirectory()) {
                continue;
            }

            TDirectoryTreeItem item = new TDirectoryTreeItem(getTreeView(),
                file.getName(), false, false);

            item.level = this.level + 1;
            getChildren().add(item);
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
    public TDirectoryTreeItem(final TTreeView view, final String text) {
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
        final boolean expanded) {

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
        final boolean expanded, final boolean openParents) {

        super(view, text, false);

        List<TDirectoryTreeItem> parentItems = new LinkedList<TDirectoryTreeItem>();
        List<String> parentPaths = new LinkedList<String>();
        boolean oldExpanded = expanded;

        if (openParents == true) {
            setExpanded(true);

            // Go up the directory tree
            File rootPath = new File(text);
            File parent = rootPath.getParentFile();
            while (parent != null) {
                parentPaths.add(rootPath.getName());
                rootPath = rootPath.getParentFile();
                parent = rootPath.getParentFile();
            }
            setText(rootPath.getName());
        } else {
            setText(text);
        }

        dir = new File(getText());
        onExpand();

        if (openParents == true) {
            TDirectoryTreeItem childPath = this;
            Collections.reverse(parentPaths);
            for (String p: parentPaths) {
                for (TWidget widget: childPath.getChildren()) {
                    TDirectoryTreeItem child = (TDirectoryTreeItem) widget;
                    if (child.getText().equals(p)) {
                        childPath = child;
                        childPath.setExpanded(true);
                        childPath.onExpand();
                        break;
                    }
                }
            }
            unselect();
            getTreeView().setSelected(childPath);
            setExpanded(oldExpanded);
        }
        getTreeView().reflow();
    }
}
