package org.smartly.application.desktopgap.impl.app.applications.window.controller;

import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.AppWindow;
import org.smartly.application.desktopgap.impl.app.utils.DOM;
import org.smartly.commons.cryptograph.MD5;
import org.smartly.commons.util.CompareUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.IOException;

/**
 * Path wrapper for Navigation URL
 */
public class AppWindowUrl {

    private static final String TEMP_PREFIX = "_temp_";
    private static final String PROTOCOL = "file:///";


    private final String _app_docroot;
    private final String _url;

    public AppWindowUrl(final AppWindow window,
                        final String url) throws IOException {
        _app_docroot = window.getManifest().getAbsoluteAppPath("");
        if (isTemp(url)) {
            _url = stripProtocol(url);
        } else {
            _url = this.getTempUrl(url);
            //-- insert page into frame --//
            final AppManifest manifest = window.getManifest();
            final String frame = window.getFrame();
            DOM.insertInFrame(manifest, frame, stripProtocol(url), _url);
        }
    }

    public String getUrl() {
        return _url.startsWith(PROTOCOL) ? _url : PROTOCOL.concat(_url);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getTempUrl(final String url) {
        final String unix_path = PathUtils.toUnixPath(url);
        final String md5 = MD5.encode(unix_path);

        return PathUtils.concat(_app_docroot, TEMP_PREFIX.concat(md5.concat(".html")));
    }

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    private static String stripProtocol(final String url) {
        if (StringUtils.hasText(url)) {
            return PathUtils.toUnixPath(url).replace("file:///", "");
        }
        return "";
    }

    public static boolean isTemp(final String url) {
        if (StringUtils.hasText(url)) {
            return PathUtils.getFilename(url).startsWith(TEMP_PREFIX);
        }
        return true;
    }

    public static boolean equals(final String path1, final String path2) {
        if (isTemp(path1) && isTemp(path2)) {
            return PathUtils.getFilename(path1).equalsIgnoreCase(PathUtils.getFilename(path2));
        }
        return CompareUtils.equals(path1, path2);
    }

    public static void delete(final String file) {
        try {
            FileUtils.delete(stripProtocol(file));
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

}
