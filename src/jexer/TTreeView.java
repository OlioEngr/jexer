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

import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TTreeView implements a simple tree view.
 */
public class TTreeView extends TWidget {

    /**
     * Vertical scrollbar.
     */
    private TVScroller vScroller;

    /**
     * Horizontal scrollbar.  Note package private access.
     */
    THScroller hScroller;

    /**
     * Root of the tree.
     */
    private TTreeItem treeRoot;

    /**
     * Get the root of the tree.
     *
     * @return the root of the tree
     */
    public final TTreeItem getTreeRoot() {
        return treeRoot;
    }

    /**
     * Set the root of the tree.
     *
     * @param treeRoot the new root of the tree
     */
    public final void setTreeRoot(final TTreeItem treeRoot) {
        this.treeRoot = treeRoot;
    }

    /**
     * Maximum width of a single line.
     */
    private int maxLineWidth;

    /**
     * Only one of my children can be selected.
     */
    private TTreeItem selectedItem = null;

    /**
     * If true, move the window to put the selected item in view.  This
     * normally only happens once after setting treeRoot.
     */
    public boolean centerWindow = false;

    /**
     * The action to perform when the user selects an item.
     */
    private TAction action = null;

    /**
     * Set treeRoot.
     *
     * @param treeRoot ultimate root of tree
     * @param centerWindow if true, move the window to put the root in view
     */
    public void setTreeRoot(final TTreeItem treeRoot, final boolean centerWindow) {
        this.treeRoot = treeRoot;
        this.centerWindow = centerWindow;
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of tree view
     * @param height height of tree view
     */
    public TTreeView(final TWidget parent, final int x, final int y,
        final int width, final int height) {

        this(parent, x, y, width, height, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of tree view
     * @param height height of tree view
     * @param action action to perform when an item is selected
     */
    public TTreeView(final TWidget parent, final int x, final int y,
        final int width, final int height, final TAction action) {

        super(parent, x, y, width, height);
        this.action = action;
    }

    /**
     * Get the tree view item that was selected.
     *
     * @return the selected item, or null if no item is selected
     */
    public final TTreeItem getSelected() {
        return selectedItem;
    }

    /**
     * Set the new selected tree view item.  Note package private access.
     *
     * @param item new item that became selected
     */
    void setSelected(final TTreeItem item) {
        if (item != null) {
            item.setSelected(true);
        }
        if ((selectedItem != null) && (selectedItem != item)) {
            selectedItem.setSelected(false);
        }
        selectedItem = item;
    }

    /**
     * Perform user selection action.  Note package private access.
     */
    void dispatch() {
        if (action != null) {
            action.DO();
        }
    }

    /**
     * Update (or instantiate) vScroller and hScroller.
     */
    private void updateScrollers() {
        // Setup vertical scroller
        if (vScroller == null) {
            vScroller = new TVScroller(this, getWidth() - 1, 0,
                getHeight() - 1);
            vScroller.setValue(0);
            vScroller.setTopValue(0);
        }
        vScroller.setX(getWidth() - 1);
        vScroller.setHeight(getHeight() - 1);
        vScroller.setBigChange(getHeight() - 1);

        // Setup horizontal scroller
        if (hScroller == null) {
            hScroller = new THScroller(this, 0, getHeight() - 1,
                getWidth() - 1);
            hScroller.setValue(0);
            hScroller.setLeftValue(0);
        }
        hScroller.setY(getHeight() - 1);
        hScroller.setWidth(getWidth() - 1);
        hScroller.setBigChange(getWidth() - 1);
    }

    /**
     * Resize text and scrollbars for a new width/height.
     */
    public void reflow() {
        int selectedRow = 0;
        boolean foundSelectedRow = false;

        updateScrollers();
        if (treeRoot == null) {
            return;
        }

        // Make each child invisible/inactive to start, expandTree() will
        // reactivate the visible ones.
        for (TWidget widget: getChildren()) {
            if (widget instanceof TTreeItem) {
                TTreeItem item = (TTreeItem) widget;
                item.setInvisible(true);
                item.setEnabled(false);
            }
        }

        // Expand the tree into a linear list
        getChildren().clear();
        getChildren().addAll(treeRoot.expandTree("", true));
        for (TWidget widget: getChildren()) {
            TTreeItem item = (TTreeItem) widget;

            if (item == selectedItem) {
                foundSelectedRow = true;
            }
            if (foundSelectedRow == false) {
                selectedRow++;
            }

            int lineWidth = item.getText().length()
            + item.getPrefix().length() + 4;
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }
        if ((centerWindow) && (foundSelectedRow)) {
            if ((selectedRow < vScroller.getValue())
                || (selectedRow > vScroller.getValue() + getHeight() - 2)
            ) {
                vScroller.setValue(selectedRow);
                centerWindow = false;
            }
        }
        updatePositions();

        // Rescale the scroll bars
        vScroller.setBottomValue(getChildren().size() - getHeight() + 1);
        if (vScroller.getBottomValue() < 0) {
            vScroller.setBottomValue(0);
        }
        /*
        if (vScroller.getValue() > vScroller.getBottomValue()) {
            vScroller.setValue(vScroller.getBottomValue());
        }
         */
        hScroller.setRightValue(maxLineWidth - getWidth() + 3);
        if (hScroller.getRightValue() < 0) {
            hScroller.setRightValue(0);
        }
        /*
        if (hScroller.getValue() > hScroller.getRightValue()) {
            hScroller.setValue(hScroller.getRightValue());
        }
         */
        getChildren().add(hScroller);
        getChildren().add(vScroller);
    }

    /**
     * Update the Y positions of all the children items.
     */
    private void updatePositions() {
        if (treeRoot == null) {
            return;
        }

        int begin = vScroller.getValue();
        int topY = 0;
        for (int i = 0; i < getChildren().size(); i++) {
            if (!(getChildren().get(i) instanceof TTreeItem)) {
                // Skip
                continue;
            }
            TTreeItem item = (TTreeItem) getChildren().get(i);

            if (i < begin) {
                // Render invisible
                item.setEnabled(false);
                item.setInvisible(true);
                continue;
            }

            if (topY >= getHeight() - 1) {
                // Render invisible
                item.setEnabled(false);
                item.setInvisible(true);
                continue;
            }

            item.setY(topY);
            item.setEnabled(true);
            item.setInvisible(false);
            item.setWidth(getWidth() - 1);
            topY++;
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
        } else if (mouse.isMouseWheelDown()) {
            vScroller.increment();
        } else {
            // Pass to children
            super.onMouseDown(mouse);
        }

        // Update the screen after the scrollbars have moved
        reflow();
    }

    /**
     * Handle mouse release events.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(TMouseEvent mouse) {
        // Pass to children
        super.onMouseDown(mouse);

        // Update the screen after any thing has expanded/contracted
        reflow();
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
        } else if (keypress.equals(kbEnter)) {
            if (selectedItem != null) {
                dispatch();
            }
        } else {
            // Pass other keys (tab etc.) on
            super.onKeypress(keypress);
        }

        // Update the screen after any thing has expanded/contracted
        reflow();
    }

}
