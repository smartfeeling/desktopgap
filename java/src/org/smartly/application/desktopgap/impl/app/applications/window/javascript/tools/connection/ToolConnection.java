package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.connection;

import org.smartly.commons.network.NetworkUtils;

/**
 * The connection object gives access to the device's cellular and wifi connection information.
 * <p/>
 * <h2>Properties</h2>
 * <ul>
 * <li>connection.type</li>
 * </ul>
 * <p/>
 * <h2>Constants</h2>
 * <ul>
 * <li>Connection.ETHERNET</li>
 * <li>Connection.UNKNOWN</li>
 * <li>Connection.NONE</li>
 * </ul>
 */
public final class ToolConnection {

    private String _type;

    public static enum Connections {
        ETHERNET,
        NONE,
        UNKNOWN
    }

    public ToolConnection() {
        this.refresh();
    }

    public String getType() {
        return _type;
    }

    public void refresh() {
        _type = this.testConnection().toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Connections testConnection() {
        try {
            if (NetworkUtils.hasNetworkAccess()) {
                return Connections.ETHERNET;
            }
        } catch (Throwable ignored) {
        }
        return Connections.NONE;
    }

}
