package org.smartly.application.desktopgap.impl.app.command;

import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.client.Client;

/**
 * Helper to send commands to existing instance of DesktopGap
 */
public final class CommandSender implements ICommandConstants {

    private static final String HOST = "localhost";
    private final int _port;

    private CommandSender() {
        _port = getPort();
    }

    public boolean sendPing() {
        try {
            return Client.ping(HOST, _port);
        } catch (Throwable t) {
            return false;
        }
    }

    public Object sendMessage(final Object message) {
        try {
            return Client.send(HOST, _port, message);
        } catch (Throwable t) {
            Smartly.getLogger().warning(this, t);
            return t;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static CommandSender __instance;

    private static CommandSender getInstance() {
        if (null == __instance) {
            __instance = new CommandSender();
        }
        return __instance;
    }

    private static int getPort() {
        return DesktopGap.getPort();
    }

    public static boolean ping() {
        return getInstance().sendPing();
    }

    public static boolean sendEcho() {
        final Object echo = getInstance().sendMessage(MSG_ECHO);
        return null != echo && echo.equals(MSG_ECHO);
    }

    public static Object send(final Object message) {
        return getInstance().sendMessage(message);
    }
}
