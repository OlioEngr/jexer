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

import java.util.List;
import java.util.LinkedList;

import jexer.event.TCommandEvent;
import jexer.event.TInputEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMenuEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.io.Screen;
import static jexer.TKeypress.*;

/**
 * TWidget is the base class of all objects that can be drawn on screen or
 * handle user input events.
 */
public abstract class TWidget {

    /**
     * Every widget has a parent widget that it may be "contained" in.  For
     * example, a TWindow might contain several TTextFields, or a TComboBox
     * may contain a TScrollBar.
     */
    protected TWidget parent = null;

    /**
     * Child widgets that this widget contains.
     */
    private List<TWidget> children;

    /**
     * The currently active child widget that will receive keypress events.
     */
    private TWidget activeChild = null;

    /**
     * If true, this widget will receive events.
     */
    protected boolean active = false;

    /**
     * The window that this widget draws to.
     */
    protected TWindow window = null;

    /**
     * Absolute X position of the top-left corner.
     */
    protected int x = 0;

    /**
     * Absolute Y position of the top-left corner.
     */
    protected int y = 0;

    /**
     * Width.
     */
    protected int width = 0;

    /**
     * Height.
     */
    protected int height = 0;

    /**
     * My tab order inside a window or containing widget.
     */
    private int tabOrder = 0;

    /**
     * If true, this widget can be tabbed to or receive events.
     */
    private boolean enabled = true;

    /**
     * Get enabled flag.
     *
     * @return if true, this widget can be tabbed to or receive events
     */
    public final boolean getEnabled() {
        return enabled;
    }

    /**
     * Set enabled flag.
     *
     * @param enabled if true, this widget can be tabbed to or receive events
     */
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        /*

        // TODO: get this working after scrollers are going again

        if (enabled == false) {
            active = false;
            // See if there are any active siblings to switch to
            boolean foundSibling = false;
            if (parent !is null) {
                foreach (w; parent.children) {
                    if ((w.enabled) &&
                        (!cast(THScroller)this) &&
                        (!cast(TVScroller)this)
                    ) {
                        parent.activate(w);
                        foundSibling = true;
                        break;
                    }
                }
                if (!foundSibling) {
                    parent.activeChild = null;
                }
            }
        }
         */
    }

    /**
     * If true, this widget has a cursor.
     */
    private boolean hasCursor = false;

    /**
     * Cursor column position in relative coordinates.
     */
    private int cursorX = 0;

    /**
     * Cursor row position in relative coordinates.
     */
    private int cursorY = 0;

    /**
     * Comparison operator sorts on tabOrder.
     *
     * @param that another TWidget instance
     * @return difference between this.tabOrder and that.tabOrder
     */
    public final int compare(final TWidget that) {
        return (this.tabOrder - that.tabOrder);
    }

    /**
     * See if this widget should render with the active color.
     *
     * @return true if this widget is active and all of its parents are
     * active.
     */
    public final boolean getAbsoluteActive() {
        if (parent == this) {
            return active;
        }
        return (active && parent.getAbsoluteActive());
    }

    /**
     * Returns the cursor X position.
     *
     * @return absolute screen column number for the cursor's X position
     */
    public final int getCursorAbsoluteX() {
        assert (hasCursor);
        return getAbsoluteX() + cursorX;
    }

    /**
     * Returns the cursor Y position.
     *
     * @return absolute screen row number for the cursor's Y position
     */
    public final int getCursorAbsoluteY() {
        assert (hasCursor);
        return getAbsoluteY() + cursorY;
    }

    /**
     * Compute my absolute X position as the sum of my X plus all my parent's
     * X's.
     *
     * @return absolute screen column number for my X position
     */
    public final int getAbsoluteX() {
        assert (parent != null);
        if (parent == this) {
            return x;
        }
        if ((parent instanceof TWindow) && !(parent instanceof TMenu)) {
            // Widgets on a TWindow have (0,0) as their top-left, but this is
            // actually the TWindow's (1,1).
            return parent.getAbsoluteX() + x + 1;
        }
        return parent.getAbsoluteX() + x;
    }

    /**
     * Compute my absolute Y position as the sum of my Y plus all my parent's
     * Y's.
     *
     * @return absolute screen row number for my Y position
     */
    public final int getAbsoluteY() {
        assert (parent != null);
        if (parent == this) {
            return y;
        }
        if ((parent instanceof TWindow) && !(parent instanceof TMenu)) {
            // Widgets on a TWindow have (0,0) as their top-left, but this is
            // actually the TWindow's (1,1).
            return parent.getAbsoluteY() + y + 1;
        }
        return parent.getAbsoluteY() + y;
    }

    /**
     * Draw my specific widget.  When called, the screen rectangle I draw
     * into is already setup (offset and clipping).
     */
    public void draw() {
        // Default widget draws nothing.
    }

    /**
     * Called by parent to render to TWindow.
     */
    public final void drawChildren() {
        // Set my clipping rectangle
        assert (window != null);
        assert (window.getScreen() != null);
        Screen screen = window.getScreen();

        screen.setClipRight(width);
        screen.setClipBottom(height);

        int absoluteRightEdge = window.getAbsoluteX() + screen.getWidth();
        int absoluteBottomEdge = window.getAbsoluteY() + screen.getHeight();
        if (!(this instanceof TWindow) && !(this instanceof TVScroller)) {
            absoluteRightEdge -= 1;
        }
        if (!(this instanceof TWindow) && !(this instanceof THScroller)) {
            absoluteBottomEdge -= 1;
        }
        int myRightEdge = getAbsoluteX() + width;
        int myBottomEdge = getAbsoluteY() + height;
        if (getAbsoluteX() > absoluteRightEdge) {
            // I am offscreen
            screen.setClipRight(0);
        } else if (myRightEdge > absoluteRightEdge) {
            screen.setClipRight(screen.getClipRight()
                - myRightEdge - absoluteRightEdge);
        }
        if (getAbsoluteY() > absoluteBottomEdge) {
            // I am offscreen
            screen.setClipBottom(0);
        } else if (myBottomEdge > absoluteBottomEdge) {
            screen.setClipBottom(screen.getClipBottom()
                - myBottomEdge - absoluteBottomEdge);
        }

        // Set my offset
        screen.setOffsetX(getAbsoluteX());
        screen.setOffsetY(getAbsoluteY());

        // Draw me
        draw();

        // Continue down the chain
        for (TWidget widget: children) {
            widget.drawChildren();
        }
    }

    /**
     * Subclasses need this constructor to setup children.
     */
    protected TWidget() {
        children = new LinkedList<TWidget>();
    }

    /**
     * Protected constructor.
     *
     * @param parent parent widget
     */
    protected TWidget(final TWidget parent) {
        this.parent = parent;
        this.window = parent.window;

        parent.addChild(this);
    }

    /**
     * Add a child widget to my list of children.  We set its tabOrder to 0
     * and increment the tabOrder of all other children.
     *
     * @param child TWidget to add
     */
    private void addChild(final TWidget child) {
        children.add(child);

        if ((child.enabled)
            && !(child instanceof THScroller)
            && !(child instanceof TVScroller)
        ) {
            for (TWidget widget: children) {
                widget.active = false;
            }
            child.active = true;
            activeChild = child;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).tabOrder = i;
        }
    }

    /**
     * Switch the active child.
     *
     * @param child TWidget to activate
     */
    public final void activate(final TWidget child) {
        assert (child.enabled);
        if ((child instanceof THScroller)
            || (child instanceof TVScroller)
        ) {
            return;
        }

        if (child != activeChild) {
            if (activeChild != null) {
                activeChild.active = false;
            }
            child.active = true;
            activeChild = child;
        }
    }

    /**
     * Switch the active child.
     *
     * @param tabOrder tabOrder of the child to activate.  If that child
     * isn't enabled, then the next enabled child will be activated.
     */
    public final void activate(final int tabOrder) {
        if (activeChild == null) {
            return;
        }
        TWidget child = null;
        for (TWidget widget: children) {
            if ((widget.enabled)
                && !(widget instanceof THScroller)
                && !(widget instanceof TVScroller)
                && (widget.tabOrder >= tabOrder)
            ) {
                child = widget;
                break;
            }
        }
        if ((child != null) && (child != activeChild)) {
            activeChild.active = false;
            assert (child.enabled);
            child.active = true;
            activeChild = child;
        }
    }

    /**
     * Switch the active widget with the next in the tab order.
     *
     * @param forward if true, then switch to the next enabled widget in the
     * list, otherwise switch to the previous enabled widget in the list
     */
    public final void switchWidget(final boolean forward) {

        // Only switch if there are multiple enabled widgets
        if ((children.size() < 2) || (activeChild == null)) {
            return;
        }

        int tabOrder = activeChild.tabOrder;
        do {
            if (forward) {
                tabOrder++;
            } else {
                tabOrder--;
            }
            if (tabOrder < 0) {

                // If at the end, pass the switch to my parent.
                if ((!forward) && (parent != this)) {
                    parent.switchWidget(forward);
                    return;
                }

                tabOrder = children.size() - 1;
            } else if (tabOrder == children.size()) {
                // If at the end, pass the switch to my parent.
                if ((forward) && (parent != this)) {
                    parent.switchWidget(forward);
                    return;
                }

                tabOrder = 0;
            }
            if (activeChild.tabOrder == tabOrder) {
                // We wrapped around
                break;
            }
        } while ((!children.get(tabOrder).enabled)
            && !(children.get(tabOrder) instanceof THScroller)
            && !(children.get(tabOrder) instanceof TVScroller));

        assert (children.get(tabOrder).enabled);

        activeChild.active = false;
        children.get(tabOrder).active = true;
        activeChild = children.get(tabOrder);

        // Refresh
        window.getApplication().setRepaint();
    }

    /**
     * Returns my active widget.
     *
     * @return widget that is active, or this if no children
     */
    public final TWidget getActiveChild() {
        if ((this instanceof THScroller)
            || (this instanceof TVScroller)
        ) {
            return parent;
        }

        for (TWidget widget: children) {
            if (widget.active) {
                return widget.getActiveChild();
            }
        }
        // No active children, return me
        return this;
    }

    /**
     * Method that subclasses can override to handle keystrokes.
     *
     * @param keypress keystroke event
     */
    public void onKeypress(final TKeypressEvent keypress) {

        if ((children.size() == 0)
            // || (cast(TTreeView)this)
            // || (cast(TText)this)
        ) {

            // Defaults:
            //   tab / shift-tab - switch to next/previous widget
            //   right-arrow or down-arrow: same as tab
            //   left-arrow or up-arrow: same as shift-tab
            if ((keypress.equals(kbTab))
                || (keypress.equals(kbRight))
                || (keypress.equals(kbDown))
            ) {
                parent.switchWidget(true);
                return;
            } else if ((keypress.equals(kbShiftTab))
                || (keypress.equals(kbBackTab))
                || (keypress.equals(kbLeft))
                || (keypress.equals(kbUp))
            ) {
                parent.switchWidget(false);
                return;
            }
        }

        // If I have any buttons on me AND this is an Alt-key that matches
        // its mnemonic, send it an Enter keystroke
        for (TWidget widget: children) {
            /*
            TODO

            if (TButton button = cast(TButton)w) {
                if (button.enabled &&
                    !keypress.key.isKey &&
                    keypress.key.alt &&
                    !keypress.key.ctrl &&
                    (toLowercase(button.mnemonic.shortcut) == toLowercase(keypress.key.ch))) {

                    w.handleEvent(new TKeypressEvent(kbEnter));
                    return;
                }
            }
             */
        }

        // Dispatch the keypress to an active widget
        for (TWidget widget: children) {
            if (widget.active) {
                window.getApplication().setRepaint();
                widget.handleEvent(keypress);
                return;
            }
        }
    }

    /**
     * Method that subclasses can override to handle mouse button presses.
     *
     * @param mouse mouse button event
     */
    public void onMouseDown(final TMouseEvent mouse) {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            if (widget.mouseWouldHit(mouse)) {
                // Dispatch to this child, also activate it
                activate(widget);

                // Set x and y relative to the child's coordinates
                mouse.setX(mouse.getAbsoluteX() - widget.getAbsoluteX());
                mouse.setY(mouse.getAbsoluteY() - widget.getAbsoluteY());
                widget.handleEvent(mouse);
                return;
            }
        }
    }

    /**
     * Method that subclasses can override to handle mouse button releases.
     *
     * @param mouse mouse button event
     */
    public void onMouseUp(final TMouseEvent mouse) {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            if (widget.mouseWouldHit(mouse)) {
                // Dispatch to this child, also activate it
                activate(widget);

                // Set x and y relative to the child's coordinates
                mouse.setX(mouse.getAbsoluteX() - widget.getAbsoluteX());
                mouse.setY(mouse.getAbsoluteY() - widget.getAbsoluteY());
                widget.handleEvent(mouse);
                return;
            }
        }
    }

    /**
     * Method that subclasses can override to handle mouse movements.
     *
     * @param mouse mouse motion event
     */
    public void onMouseMotion(final TMouseEvent mouse) {
        // Default: do nothing, pass it on to ALL of my children.  This way
        // the children can see the mouse "leaving" their area.
        for (TWidget widget: children) {
            // Set x and y relative to the child's coordinates
            mouse.setX(mouse.getAbsoluteX() - widget.getAbsoluteX());
            mouse.setY(mouse.getAbsoluteY() - widget.getAbsoluteY());
            widget.handleEvent(mouse);
        }
    }

    /**
     * Method that subclasses can override to handle window/screen resize
     * events.
     *
     * @param resize resize event
     */
    public void onResize(final TResizeEvent resize) {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            widget.onResize(resize);
        }
    }

    /**
     * Method that subclasses can override to handle posted command events.
     *
     * @param command command event
     */
    public void onCommand(final TCommandEvent command) {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            widget.onCommand(command);
        }
    }

    /**
     * Method that subclasses can override to handle menu or posted menu
     * events.
     *
     * @param menu menu event
     */
    public void onMenu(final TMenuEvent menu) {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            widget.onMenu(menu);
        }
    }

    /**
     * Method that subclasses can override to do processing when the UI is
     * idle.
     */
    public void onIdle() {
        // Default: do nothing, pass to children instead
        for (TWidget widget: children) {
            widget.onIdle();
        }
    }

    /**
     * Consume event.  Subclasses that want to intercept all events in one go
     * can override this method.
     *
     * @param event keyboard, mouse, resize, command, or menu event
     */
    public void handleEvent(final TInputEvent event) {
        // System.err.printf("TWidget (%s) event: %s\n", this.getClass().getName(),
        //     event);

        if (!enabled) {
            // Discard event
            // System.err.println("   -- discard --");
            return;
        }

        if (event instanceof TKeypressEvent) {
            onKeypress((TKeypressEvent) event);
        } else if (event instanceof TMouseEvent) {

            TMouseEvent mouse = (TMouseEvent) event;

            switch (mouse.getType()) {

            case MOUSE_DOWN:
                onMouseDown(mouse);
                break;

            case MOUSE_UP:
                onMouseUp(mouse);
                break;

            case MOUSE_MOTION:
                onMouseMotion(mouse);
                break;

            default:
                throw new IllegalArgumentException("Invalid mouse event type: "
                    + mouse.getType());
            }
        } else if (event instanceof TResizeEvent) {
            onResize((TResizeEvent) event);
        } else if (event instanceof TCommandEvent) {
            onCommand((TCommandEvent) event);
        } else if (event instanceof TMenuEvent) {
            onMenu((TMenuEvent) event);
        }

        // Do nothing else
        return;
    }

    /**
     * Check if a mouse press/release event coordinate is contained in this
     * widget.
     *
     * @param mouse a mouse-based event
     * @return whether or not a mouse click would be sent to this widget
     */
    public final boolean mouseWouldHit(final TMouseEvent mouse) {

        if (!enabled) {
            return false;
        }

        if ((mouse.getAbsoluteX() >= getAbsoluteX())
            && (mouse.getAbsoluteX() <  getAbsoluteX() + width)
            && (mouse.getAbsoluteY() >= getAbsoluteY())
            && (mouse.getAbsoluteY() <  getAbsoluteY() + height)
        ) {
            return true;
        }
        return false;
    }

}
