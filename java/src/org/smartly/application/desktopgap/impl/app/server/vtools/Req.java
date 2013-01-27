package org.smartly.application.desktopgap.impl.app.server.vtools;


import org.smartly.Smartly;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Req implements IVLCTool {

    public static final String NAME = "req";

    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEADER_USER_AGENT = "User-Agent";

    private final String _resourcePath;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;
    private final Map<String, String> _externalParams;

    private String _langCode;
    private String _userAgent;

    public Req(final String resourcePath,
               final HttpServletRequest httprequest,
               final HttpServletResponse httpresponse) {
        _resourcePath = resourcePath;
        _request = httprequest;
        _response = httpresponse;
        _externalParams = null;
    }

    public Req(final String resourcePath,
               final Map<String, String> externalParams,
               final HttpServletRequest httprequest,
               final HttpServletResponse httpresponse) {
        _resourcePath = resourcePath;
        _request = httprequest;
        _response = httpresponse;
        _externalParams = externalParams;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Return requested file path. i.e. "/pages/index.html"
     *
     * @return i.e. "/pages/index.html"
     */
    public String getFilePath() {
        return _resourcePath;
    }

    /**
     * Returns the part of this request's URL from the protocol
     * name up to the query string in the first line of the HTTP request.
     * The web container does not decode this String.
     * For example:
     * <table summary="Examples of Returned Values">
     * <tr align=left><th>First line of HTTP request      </th>
     * <th>     Returned Value</th>
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     *
     * @return a <code>String</code> containing
     *         the part of the URL from the
     *         protocol name up to the query string
     */
    public String getRequestURI() {
        return _request.getRequestURI();
    }

    /**
     * Returns any extra path information associated with
     * the URL the client sent when it made this request.
     * The extra path information follows the servlet path
     * but precedes the query string and will start with
     * a "/" character.
     * <p/>
     * <p>This method returns <code>null</code> if there
     * was no extra path information.
     * <p/>
     * <p>Same as the value of the CGI variable PATH_INFO.
     *
     * @return a <code>String</code>, decoded by the
     *         web container, specifying
     *         extra path information that comes
     *         after the servlet path but before
     *         the query string in the request URL;
     *         or <code>null</code> if the URL does not have
     *         any extra path information
     */
    public String getPathInfo() {
        return _request.getPathInfo();
    }

    /**
     * Return request parameter or empty String
     *
     * @param name Name of parameter into POST or GET request
     * @return Object or empty String. Never null.
     */
    public String getParam(final String name) {
        return this.getParam(name, "");
    }

    public String getParam(final String name, final String def) {
        if (null != _externalParams && _externalParams.containsKey(name)) {
            final String param = _externalParams.get(name);
            return null != param ? param : def;
        } else {
            final Object param = this.getRawParam(name);
            if (null != param) {
                if (param.getClass().isArray()) {
                    final Object[] array = (Object[]) param;
                    return array[0].toString();
                }
                return param.toString();
            }
            return def;
        }
    }

    public int getInt(final String paramName) {
        return this.getInt(paramName, 0);
    }

    public int getInt(final String paramName, final int def) {
        try {
            return ConversionUtils.toInteger(this.getParam(paramName), def);
        } catch (Throwable t) {
        }
        return def;
    }

    public long getLong(final String paramName) {
        return this.getLong(paramName, 0L);
    }

    public long getLong(final String paramName, final long def) {
        try {
            return ConversionUtils.toLong(this.getParam(paramName), def);
        } catch (Throwable t) {
        }
        return def;
    }

    public double getDouble(final String paramName) {
        return this.getDouble(paramName, 0d);
    }

    public double getDouble(final String paramName, final double def) {
        try {
            return ConversionUtils.toDouble(this.getParam(paramName), -1, def);
        } catch (Throwable t) {
        }
        return def;
    }

    public String getString(final String paramName) {
        return this.getString(paramName, "");
    }

    public String getString(final String paramName, final String def) {
        try {
            return this.getParam(paramName, def).toString();
        } catch (Throwable t) {
        }
        return def;
    }

    public String getLangCode() {
        if (StringUtils.hasText(_langCode)) {
            return _langCode;
        } else {
            _langCode = getLang(_request);
        }
        return StringUtils.hasText(_langCode)
                ? _langCode
                : LocaleUtils.getCurrent().getLanguage();
    }

    public String getUserAgent() {
        if (StringUtils.hasText(_userAgent)) {
            return _userAgent;
        } else {
            _userAgent = _request.getHeader(HEADER_USER_AGENT);
        }
        return StringUtils.hasText(_userAgent)
                ? _userAgent
                : "";
    }

    public boolean isMobile() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("mobile") > -1 || this.isMobileApple() || this.isAndroid();
        }
        return false;
    }

    public boolean isIPhone() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("iphone") > -1;
        }
        return false;
    }

    public boolean isIPad() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("ipad") > -1;
        }
        return false;
    }

    public boolean isIPod() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("ipod") > -1;
        }
        return false;
    }

    public boolean isMobileApple() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return this.isIPad() || this.isIPhone() || this.isIPod();
        }
        return false;
    }

    public boolean isAndroid() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("android") > -1;
        }
        return false;
    }

    public boolean isMobileAndroid() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return this.isAndroid() && useragent.indexOf("mobile") > -1;
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object getRawParam(final String name) {
        if (null != _request) {
            return _request.getParameterMap().get(name);
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String decode(final String value) {
        try {
            return URLDecoder.decode(value, Smartly.getCharset());
        } catch (Exception ex) {
            return value;
        }
    }

    public static String getLang(final HttpServletRequest request) {
        return getLang(request, Smartly.getLang());
    }

    public static String getLang(final HttpServletRequest request, final String defaultLang) {
        final String value = request.getHeader(HEADER_ACCEPT_LANGUAGE);
        final String[] tokens = StringUtils.split(value, ",");
        if (tokens.length > 0) {
            final Locale locale = LocaleUtils.getLocaleFromString(tokens[0]);
            return locale.getLanguage();
        }
        return StringUtils.hasText(defaultLang) ? defaultLang : LocaleUtils.getCurrent().getLanguage();
    }

    public static Map<String, String> getParameters(final HttpServletRequest request) {
        final String method = request.getMethod();
        return getParameters(method, request);
    }

    public static Map<String, String> getParameters(final String method, final HttpServletRequest request) {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        if (method.equalsIgnoreCase("GET")) {
            //-- GET METHOD --//
            final Map<String, String[]> map = request.getParameterMap();
            if (map.size() > 0) {
                final Set<String> keys = map.keySet();
                for (final String key : keys) {
                    final String[] value = map.get(key);
                    if (null != key && key.length() > 0) {
                        result.put(key, value[0]);
                    } else {
                        result.put(key, "");
                    }
                }
            }
        } else {
            //-- POST METHOD --//
            try {
                final InputStream is = request.getInputStream();
                final byte[] bytes = ByteUtils.getBytes(is);
                if (null != bytes) {
                    final String data = new String(bytes, Smartly.getCharset());
                    if (StringUtils.hasLength(data)) {
                        final String[] queryTokens = StringUtils.split(data, "&");
                        for (final String qt : queryTokens) {
                            final String[] keyValue = StringUtils.split(qt, "=");
                            if (keyValue.length == 2) {
                                result.put(keyValue[0], decode(keyValue[1]));
                            } else {
                                result.put(keyValue[0], "");
                            }
                        }
                    }
                }
                is.close();
            } catch (Throwable ignored) {
            }
        }

        return result;
    }
}
