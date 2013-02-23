package org.smartly.application.desktopgap.impl.app.applications.window;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: angelo.geminiani
 */
public class AppManifestTest {

    public AppManifestTest() {

    }

    @Test
    public void testGetManifest() throws Exception {
        final String path = "c:/_test/foo1/foo2";
        AppManifest manifest = AppManifest.getManifest(path);
        Assert.assertTrue(null == manifest);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
