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
package jexer.demos;

import java.net.*;
import jexer.net.*;

/**
 * This class is the main driver for a simple demonstration of Jexer's
 * capabilities.  Rather than run locally, it serves a Jexer UI over a TCP
 * port.
 */
public class Demo2 {

    /**
     * Main entry point.
     *
     * @param args Command line arguments
     */
    public static void main(final String [] args) {
        try {
            if (args.length == 0) {
                System.err.printf("USAGE: java -cp jexer.jar jexer.demos.Demo2 port\n");
                return;
            }

            int port = Integer.parseInt(args[0]);
            ServerSocket server = new TelnetServerSocket(port);
            while (true) {
                Socket socket = server.accept();
                System.out.printf("New connection: %s\n", socket);
                DemoApplication app = new DemoApplication(socket.getInputStream(),
                    socket.getOutputStream());
                (new Thread(app)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
