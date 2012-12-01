package org.smartly.application.desktopgap.impl.app.command;

import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.command.ICommandConstants;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.server.Server;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;


import java.io.IOException;

/**
 * Command server
 */
public final class CommandHandler implements ISocketHandler, ICommandConstants {

    private Server _server;

    public CommandHandler() throws Exception {
        final int port = this.getPort();
        if (port < 0) {
            throw new Exception("Another instance is already running");
        }
        _server = new Server(port, this);
    }

    @Override
    public Object handle(final Object request) {
        if (request instanceof String) {
            return this.handleMessage((String) request);
        }
        return null;
    }

    public void close() {
        if (null != _server) {
            _server.stopServer();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private int getPort() {
        final int port = DesktopGap.getPort();
        try {
            // try send message: nobody should respond
            final Object response = Client.send("localhost", port, MSG_ECHO);
            if (MSG_ECHO.equals(response)) {
                return -1;
            }
        } catch (Throwable t) {
            return port; // ok, can start new server on this port
        }
        return -1;
    }

    private Object handleMessage(final String message) {
        if (message.equalsIgnoreCase(MSG_ECHO)) {
            return message;
        }
        return "";
    }

}
