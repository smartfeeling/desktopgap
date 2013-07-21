package org.smartly.application.desktopgap.impl.app.utils;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;

/**
 *
 */
public class URLUtils {

    private static final String PAGE_PARAM = IDesktopConstants.PARAM_DESKTOPGAP;

    public static String addPageParamToUrl(final String url, final boolean value) {
       return addParamToUrl(url, PAGE_PARAM, value+"");
    }

    public static String addParamToUrl(final String url,
                                       final String paramName,
                                       final String paramValue) {
        final String param = paramName.concat("=").concat(paramValue);
        if (url.contains("?")) {
            return url.concat("&").concat(param);
        }
        return url.concat("?").concat(param);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
