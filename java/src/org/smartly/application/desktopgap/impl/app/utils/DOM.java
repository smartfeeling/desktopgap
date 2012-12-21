package org.smartly.application.desktopgap.impl.app.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.util.FileUtils;
import org.smartly.packages.htmlparser.impl.HtmlParser;

import java.io.File;
import java.io.IOException;

/**
 * DOM utils
 */
public class DOM {

    private static final String SELECTOR_APP = "#app";
    private static final String SELECTOR_TITLE = "#title";

    private DOM() {
    }

    public void inject(final AppManifest manifest,
                       final String html_frame,
                       final String html_page,
                       final String output) throws IOException {
        final HtmlParser frame = new HtmlParser(html_frame);
        final Elements elements = frame.select(SELECTOR_APP);
        if (null != elements && !elements.isEmpty()) {
            // title
            final Element title = frame.selectFirst(SELECTOR_TITLE);
            if (null != title) {
                title.html(manifest.getTitle());
            }

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

            // get #app div
            final Element frame_app = elements.first();
            // add BODY to FRAME
            frame_app.append(body.outerHtml());

            // save output
            FileUtils.writeStringToFile(new File(output),
                    frame.getDocument().outerHtml(),
                    Smartly.getCharset());
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

    public static void insertInFrame(final AppManifest manifest,
                                     final String framePage,
                                     final String page,
                                     final String outputPage) throws IOException {
        final String html_frame = FileUtils.readFileToString(new File(framePage));
        final String html_page = FileUtils.readFileToString(new File(page));
        getInstance().inject(manifest, html_frame, html_page, outputPage);
    }

}
