package org.smartly.application.desktopgap.impl.app.server;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ServletUtils {


    private static final String REQ_PARAM_SEP = "&";

    private ServletUtils() {

    }

    public static JSONObject getParameters(final HttpServletRequest request) {
        final JSONObject result = new JSONObject();
        //-- GET METHOD --//
        try {
            final Map<String, String[]> map = request.getParameterMap();
            if (map.size() > 0) {
                final Set<String> keys = map.keySet();
                for (final String key : keys) {
                    if (null != key) {
                        addParam(result, key, map.get(key));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        //-- POST METHOD --//
        try {
            final InputStream is = request.getInputStream();
            final byte[] bytes = ByteUtils.getBytes(is);
            if (null != bytes && bytes.length > 0) {
                final String data = new String(bytes, Smartly.getCharset());
                if (StringUtils.hasLength(data)) {
                    final String[] queryTokens = StringUtils.split(data, REQ_PARAM_SEP);
                    for (final String qt : queryTokens) {
                        final String[] keyValue = StringUtils.split(qt, "=");
                        if (keyValue.length == 2) {
                            JsonWrapper.put(result, keyValue[0], decode(keyValue[1]));
                        } else {
                            JsonWrapper.put(result, keyValue[0], "");
                        }
                    }
                }
            }
            is.close();
        } catch (Throwable ignored) {
        }


        return result;
    }

    public static void notFound404(final HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND_404);
    }

    public static void writeResponse(final HttpServletResponse response,
                                     final Long lastModified,
                                     final String mimeType,
                                     final byte[] content) throws IOException {
        doResponseHeaders(response, content.length, mimeType);
        final InputStream is = new ByteArrayInputStream(content);
        writeResponse(response, lastModified, is);
    }

    public static void writeResponse(final HttpServletResponse response,
                                     final Long lastModified,
                                     final Object content) throws IOException {
        if (null != lastModified) {
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
        }

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        // See if a short direct method can be used?
        if (out instanceof AbstractHttpConnection.Output) {
            ((AbstractHttpConnection.Output) out).sendContent(content);
        } else if (out instanceof WriterOutputStream) {
            // Write content normally
            FileUtils.copy((InputStream) content, out);
        }
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final Resource resource,
                                         final String mimeType) {
        final long length = resource.length();
        doResponseHeaders(response, null, length, mimeType);
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final long length,
                                         final String mimeType) {
        doResponseHeaders(response, null, length, mimeType);
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final ByteArrayBuffer cacheControl,
                                         final Resource resource,
                                         final String mimeType) {
        final long length = resource.length();
        doResponseHeaders(response, cacheControl, length, mimeType);
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final ByteArrayBuffer cacheControl,
                                         final long length,
                                         final String mimeType) {
        if (mimeType != null)
            response.setContentType(mimeType);

        if (response instanceof Response) {
            final HttpFields fields = ((Response) response).getHttpFields();

            if (length > 0)
                fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);

            if (cacheControl != null)
                fields.put(HttpHeaders.CACHE_CONTROL_BUFFER, cacheControl);
        } else {
            if (length > 0)
                response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(length));

            if (cacheControl != null)
                response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
        }
    }

    public static Map<String, String> getUrlParams(final HttpServletRequest request) throws MalformedURLException {
        final Map<String, String> params = new HashMap<String, String>();
        getUrlParams(params, request);
        return params;
    }

    public static void getUrlParams(final Map<String, String> params,
                                    final HttpServletRequest request) throws MalformedURLException {
        final Map<String, String[]> paramsMap = request.getParameterMap();
        final Set<String> names = paramsMap.keySet();
        for (final String name : names) {
            final String[] value = paramsMap.get(name);
            if (null != value && value.length > 0) {
                if (value.length == 1) {
                    params.put(name, value[0]);
                } else {
                    final JSONArray array = new JSONArray();
                    for (final String v : value) {
                        array.put(v);
                    }
                    params.put(name, array.toString());
                }
            }
        }
    }

    public static String getResourcePath(final HttpServletRequest request) throws MalformedURLException {
        String servletPath;
        String pathInfo;
        Boolean included = request.getAttribute(Dispatcher.INCLUDE_REQUEST_URI) != null;
        if (included != null && included.booleanValue()) {
            servletPath = (String) request.getAttribute(Dispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String) request.getAttribute(Dispatcher.INCLUDE_PATH_INFO);

            if (servletPath == null && pathInfo == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        } else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }

        return URIUtil.addPaths(servletPath, pathInfo);
    }

    public static String getResourcePath(final ServletRequest request) throws MalformedURLException {
        String servletPath = "";
        String pathInfo = "";
        Boolean included = request.getAttribute(Dispatcher.INCLUDE_REQUEST_URI) != null;
        if (included != null && included.booleanValue()) {
            servletPath = (String) request.getAttribute(Dispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String) request.getAttribute(Dispatcher.INCLUDE_PATH_INFO);

            if (servletPath == null && pathInfo == null) {
                //servletPath = request.getServletPath();
                //pathInfo = request.getPathInfo();
            }
        } else {
            //servletPath = request.getServletPath();
            //pathInfo = request.getPathInfo();
        }

        return URIUtil.addPaths(servletPath, pathInfo);
    }

    public static Resource getResource(final Resource baseResource,
                                       final ContextHandler context,
                                       final String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/"))
            throw new MalformedURLException(path);

        Resource base = baseResource;
        if (base == null) {
            if (context == null)
                return null;
            base = context.getBaseResource();
            if (base == null)
                return null;
        }

        try {
            return base.addPath(URIUtil.canonicalPath(path));
        } catch (Exception ignore) {
        }

        return null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static String decode(final String value) {
        try {
            return URLDecoder.decode(value, Smartly.getCharset());
        } catch (Exception ex) {
            return value;
        }
    }

    /**
     * @param params container
     * @param key    key. i.e. name[obj1][obj2]..
     * @param values array of values
     */
    private static void addParam(final JSONObject params, final String key, final String[] values) throws JSONException {
        final Object jsonValue = toJsonValue(values);
        final String path = toJsonPath(key);
        JsonWrapper.put(params, path, jsonValue, true);
    }

    private static Object toJsonValue(final String[] values) throws JSONException {
        if (values.length == 0) {
            return "";
        } else if (values.length == 1) {
            return values[0];
        } else {
            return new JSONArray(values);
        }
    }

    /**
     * @param key i.e. "name[obj1][obj2]..."
     * @return "name.obj1.obj2"
     */
    private static String toJsonPath(final String key) {
        final String result = key.replaceAll("\\[", ".").replaceAll("]", "");
        if (result.endsWith(".")) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

}
