# Jexer - Java Text User Interface - Makefile
#
# $Id$
#
# This program is licensed under the GNU Lesser General Public License
# Version 3.  Please see the file "COPYING" in this directory for more
# information about the GNU Lesser General Public License Version 3.
#
#     Copyright (C) 2015  Kevin Lamonte
#
# This library is free software; you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this program; if not, see
# http://www.gnu.org/licenses/, or write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
# USA


# This Makefile is a quick-and-dirty build that is useful to execute
# the Demo1 application that uses stdin/stdout.  Use 'make run'.
#
# Generally it would be better to use the ant build.

default:	all

.SUFFIXES: .class .java

SRC_DIR = src
ANT_TARGET_DIR = build
TARGET_DIR = classes

JEXER_SRC = $(SRC_DIR)/jexer/TApplication.java \
	$(SRC_DIR)/jexer/TAction.java \
	$(SRC_DIR)/jexer/TButton.java \
	$(SRC_DIR)/jexer/TLabel.java \
	$(SRC_DIR)/jexer/TCommand.java \
	$(SRC_DIR)/jexer/TKeypress.java \
	$(SRC_DIR)/jexer/TText.java \
	$(SRC_DIR)/jexer/THScroller.java \
	$(SRC_DIR)/jexer/TVScroller.java \
	$(SRC_DIR)/jexer/TWidget.java \
	$(SRC_DIR)/jexer/bits/GraphicsChars.java \
	$(SRC_DIR)/jexer/bits/Color.java \
	$(SRC_DIR)/jexer/bits/CellAttributes.java \
	$(SRC_DIR)/jexer/bits/Cell.java \
	$(SRC_DIR)/jexer/bits/ColorTheme.java \
	$(SRC_DIR)/jexer/bits/MnemonicString.java \
	$(SRC_DIR)/jexer/event/TInputEvent.java \
	$(SRC_DIR)/jexer/event/TCommandEvent.java \
	$(SRC_DIR)/jexer/event/TKeypressEvent.java \
	$(SRC_DIR)/jexer/event/TMenuEvent.java \
	$(SRC_DIR)/jexer/event/TMouseEvent.java \
	$(SRC_DIR)/jexer/event/TResizeEvent.java \
	$(SRC_DIR)/jexer/menu/TMenu.java \
	$(SRC_DIR)/jexer/menu/TMenuItem.java \
	$(SRC_DIR)/jexer/menu/TMenuSeparator.java \
	$(SRC_DIR)/jexer/menu/TSubMenu.java \
	$(SRC_DIR)/jexer/session/SessionInfo.java \
	$(SRC_DIR)/jexer/session/TSessionInfo.java \
	$(SRC_DIR)/jexer/session/TTYSessionInfo.java \
	$(SRC_DIR)/jexer/io/Screen.java \
	$(SRC_DIR)/jexer/io/ECMA48Screen.java \
	$(SRC_DIR)/jexer/io/ECMA48Terminal.java \
	$(SRC_DIR)/jexer/backend/Backend.java \
	$(SRC_DIR)/jexer/backend/ECMA48Backend.java \
	$(SRC_DIR)/jexer/demos/Demo1.java

JEXER_BIN = $(TARGET_DIR)/jexer/TApplication.class \
	$(TARGET_DIR)/jexer/TAction.class \
	$(TARGET_DIR)/jexer/TButton.class \
	$(TARGET_DIR)/jexer/TLabel.class \
	$(TARGET_DIR)/jexer/TCommand.class \
	$(TARGET_DIR)/jexer/TKeypress.class \
	$(TARGET_DIR)/jexer/TText.class \
	$(TARGET_DIR)/jexer/THScroller.class \
	$(TARGET_DIR)/jexer/TVScroller.class \
	$(TARGET_DIR)/jexer/TWidget.class \
	$(TARGET_DIR)/jexer/bits/GraphicsChars.class \
	$(TARGET_DIR)/jexer/bits/Color.class \
	$(TARGET_DIR)/jexer/bits/CellAttributes.class \
	$(TARGET_DIR)/jexer/bits/Cell.class \
	$(TARGET_DIR)/jexer/bits/ColorTheme.class \
	$(TARGET_DIR)/jexer/bits/MnemonicString.class \
	$(TARGET_DIR)/jexer/event/TInputEvent.class \
	$(TARGET_DIR)/jexer/event/TCommandEvent.class \
	$(TARGET_DIR)/jexer/event/TKeypressEvent.class \
	$(TARGET_DIR)/jexer/event/TMenuEvent.class \
	$(TARGET_DIR)/jexer/event/TMouseEvent.class \
	$(TARGET_DIR)/jexer/event/TResizeEvent.class \
	$(TARGET_DIR)/jexer/menu/TMenu.class \
	$(TARGET_DIR)/jexer/menu/TMenuItem.class \
	$(TARGET_DIR)/jexer/menu/TMenuSeparator.class \
	$(TARGET_DIR)/jexer/menu/TSubMenu.class \
	$(TARGET_DIR)/jexer/session/SessionInfo.class \
	$(TARGET_DIR)/jexer/session/TSessionInfo.class \
	$(TARGET_DIR)/jexer/session/TTYSessionInfo.class \
	$(TARGET_DIR)/jexer/io/Screen.class \
	$(TARGET_DIR)/jexer/io/ECMA48Screen.class \
	$(TARGET_DIR)/jexer/io/ECMA48Terminal.class \
	$(TARGET_DIR)/jexer/backend/Backend.class \
	$(TARGET_DIR)/jexer/backend/ECMA48Backend.class \
	$(TARGET_DIR)/jexer/demos/Demo1.class

JAVAC = javac
JAVAFLAGS = -g -deprecation

all:	jexer

run:	jexer run-demo1

all-demos:	jexer

run-demo1:	all-demos
	java -Djexer.AWT=true -cp $(TARGET_DIR) jexer.demos.Demo1

clean:
	-rm -r $(ANT_TARGET_DIR)
	-rm -r $(TARGET_DIR)
	-mkdir $(TARGET_DIR)

jexer:	$(JEXER_SRC)
	$(JAVAC) $(JAVAFLAGS) -sourcepath $(SRC_DIR) -d $(TARGET_DIR) $(JEXER_SRC)
