package org.smartly.application.desktopgap.impl.resources;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * User: angelo.geminiani
 */
public class AppResourcesTest {

    public AppResourcesTest() {

    }


    @Test
    public void testGetAppTemplateUri() throws Exception {
        String path = AppResources.getAppFrameUri("window.css");
        System.out.println(path);
        assertNotNull(path);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
