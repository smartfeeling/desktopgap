package org.smartly.application.desktopgap.impl.app.base;

import org.smartly.commons.util.ExceptionUtils;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class AppMappedErrors {

    // ------------------------------------------------------------------------
    //                      E R R O R S
    // ------------------------------------------------------------------------

    public static final String CONNECTION_REFUSED = "connection_refused";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<Class<? extends Exception>, String> _errors;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AppMappedErrors() {
        _errors = new HashMap<Class<? extends Exception>, String>();
        this.init();
    }

    public void register(final Class<? extends Exception> type, final String code) {
        _errors.put(type, code);
    }

    public String get(final Object error) {
        if (error instanceof Exception) {
            final String code = _errors.get(((Exception) error).getClass());
            if (StringUtils.hasText(code)) {
                return code;
            }
            return ExceptionUtils.getRealMessage((Exception) error);
        }
        return StringUtils.toString(error);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        //-- map defaults --//
        this.register(java.net.ConnectException.class, CONNECTION_REFUSED);
    }

}
