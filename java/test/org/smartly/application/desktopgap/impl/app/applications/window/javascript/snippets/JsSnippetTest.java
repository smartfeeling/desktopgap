package org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets;

import org.junit.Test;
import org.smartly.commons.util.StringUtils;

/**
 * User: angelo.geminiani
 */
public class JsSnippetTest {

    public JsSnippetTest() {

    }

    @Test
    public void testReplace() throws Exception {

        final String message = "Hello \n world";
        final String html = StringUtils.replace(message, new String[]{"\n"}, "<br>");
        System.out.println(html);
        org.junit.Assert.assertEquals(html, "Hello <br> world");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
