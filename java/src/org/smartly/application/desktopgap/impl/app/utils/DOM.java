package org.smartly.application.desktopgap.impl.app.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.htmlparser.impl.HtmlParser;

import java.io.File;
import java.io.IOException;

/**
 * DOM utils
 */
public class DOM {

    private static final String SELECTOR_APP = "#app";
    private static final String SELECTOR_WEBAPP = "#webapp";
    private static final String SELECTOR_TITLE = "#title";

    private DOM() {
    }

    public String inject(final AppManifest manifest,
                       final String html_frame,
                       final String html_page,
                       final String output) throws IOException {
        final boolean isExternal = !html_page.contains("<");
        final HtmlParser frame = new HtmlParser(html_frame);
        final Elements app_elements = frame.select(SELECTOR_APP);
        final Elements webapp_elements = frame.select(SELECTOR_WEBAPP);
        if (null != app_elements && !app_elements.isEmpty()) {
            // get #app or #webapp div
            final Element frame_app = app_elements.first();
            // title
            final Element title = frame.selectFirst(SELECTOR_TITLE);
            if (null != title) {
                title.html(manifest.getTitle());
            }

            if (!isExternal) {
                // parse page
                final HtmlParser page = new HtmlParser(html_page);
                final Elements links = page.select("head > link");
                final Elements scripts = page.select("head > script");
                final Elements styles = page.select("head > style");
                final Element body = page.getBody();

                // add HEADER elements to FRAME
                final Element frame_head = frame.getHead();
                this.addTo(frame_head, links);
                this.addTo(frame_head, scripts);
                this.addTo(frame_head, styles);

                // add BODY to FRAME
                frame_app.append(body.outerHtml());
                webapp_elements.attr("style", "visibility:hidden");
            } else {
                // EXTERNAL URL
                webapp_elements.attr("src", html_page);
                frame_app.attr("style", "visibility:hidden");
            }

            final String html = frame.getDocument().outerHtml();
            // save output
            if(StringUtils.hasText(output)){
                FileUtils.writeStringToFile(new File(output),
                        html,
                        Smartly.getCharset());
            }

            return html;
        } else {
            throw new IOException("Invalid frame.");
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addTo(final Element head, final Elements elements) {
        if (null != head && null != elements && !elements.isEmpty()) {
            for (final Element element : elements) {
                head.append(element.outerHtml());
            }
        }
    }

    private void wrapHyperlinks(final Element body) {

    }

    // --------------------------------------------------------------------
    //                     S T A T I C
    // --------------------------------------------------------------------

    private static DOM __instance;

    private static DOM getInstance() {
        if (null == __instance) {
            __instance = new DOM();
        }
        return __instance;
    }

    private static String read(final String uri) throws IOException {
        if (PathUtils.isHttp(uri)) {
            return uri;
        }
        return FileUtils.readFileToString(new File(uri));
    }

    public static String insertInFrameByUrl(final AppManifest manifest,
                                            final String page,
                                            final String outputPage) throws IOException {
        final String html_frame = read(manifest.getAbsolutePageFrame());
        final String html_page = read(page);
        return getInstance().inject(manifest, html_frame, html_page, outputPage);
    }

    public static String insertInFrame(final AppManifest manifest,
                                            final String html_page,
                                            final String outputPage) throws IOException {
        final String html_frame = read(manifest.getAbsolutePageFrame());
        return getInstance().inject(manifest, html_frame, html_page, outputPage);
    }

}
