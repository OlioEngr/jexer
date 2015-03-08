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
	$(SRC_DIR)/jexer/bits/GraphicsChars.java \
	$(SRC_DIR)/jexer/bits/Color.java \
	$(SRC_DIR)/jexer/bits/CellAttributes.java \
	$(SRC_DIR)/jexer/bits/Cell.java \
	$(SRC_DIR)/jexer/bits/ColorTheme.java

JEXER_BIN = $(TARGET_DIR)/jexer/TApplication.class \
	$(TARGET_DIR)/jexer/bits/GraphicsChars.class \
	$(TARGET_DIR)/jexer/bits/Color.class \
	$(TARGET_DIR)/jexer/bits/CellAttributes.class \
	$(TARGET_DIR)/jexer/bits/Cell.class \
	$(TARGET_DIR)/jexer/bits/ColorTheme.class

JAVAC = javac
JAVAFLAGS = -g -deprecation

all:	jexer demos

run:	jexer run-demo1

all-demos:	jexer demos/Demo1.class

demos/Demo1.class:	demos/Demo1.java
	$(JAVAC) $(JAVAFLAGS) -cp $(TARGET_DIR) -d demos demos/Demo1.java

run-demo1:	demos/Demo1.class
	java -cp $(TARGET_DIR):demos Demo1

clean:	clean-demos
	-rm -r $(ANT_TARGET_DIR)
	-rm -r $(TARGET_DIR)
	-mkdir $(TARGET_DIR)

clean-demos:
	-rm demos/Demo1.class

jexer:	$(JEXER_SRC)
	$(JAVAC) $(JAVAFLAGS) -sourcepath $(SRC_DIR) -d $(TARGET_DIR) $(JEXER_SRC)
