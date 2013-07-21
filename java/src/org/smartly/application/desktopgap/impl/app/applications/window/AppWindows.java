package org.smartly.application.desktopgap.impl.app.applications.window;

import org.smartly.application.desktopgap.impl.app.applications.events.FrameCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.Handlers;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrameRepository;
import org.smartly.commons.Delegates;
import org.smartly.commons.util.StringUtils;


/**
 * Application Window Manager.
 * Each application can host one or more windows.
 */
public final class AppWindows {

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    private static final Class EVENT_ON_OPEN = Handlers.OnOpen.class;
    private static final Class EVENT_ON_CLOSE = Handlers.OnClose.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Delegates.Handlers _eventHandler;

    private final AppInstance _app;
    private final AppFrameRepository _frames;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AppWindows(final AppInstance app) {
        _app = app;
        _frames = new AppFrameRepository();
        _eventHandler = new Delegates.Handlers();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public AppInstance getApp() {
        return _app;
    }

    public int size() {
        return _frames.size();
    }

    public boolean isEmpty() {
        return _frames.size() == 0;
    }

    // --------------------------------------------------------------------
    //               events
    // --------------------------------------------------------------------

    public void onEvent(final Delegates.Handler handler) {
        _eventHandler.add(handler);
    }

    // --------------------------------------------------------------------
    //               frame
    // --------------------------------------------------------------------

    public AppFrame open(final String winId) {
        final String id = this.createWinId(winId);
        return this.openById(id);
    }

    public void close(final String winId) {
        if (!StringUtils.hasText(winId)) {
            this.closeAll();
        } else {
            final String id = this.createWinId(winId);
            final AppFrame frame = _frames.remove(id);
            if (null != frame) {
                frame.close();
            }
        }
    }

    /**
     * Same as close, but with no events.
     */
    public void kill(final String winId) {
        if (!StringUtils.hasText(winId)) {
            this.killAll();
        } else {
            final String id = this.createWinId(winId);
            final AppFrame frame = _frames.remove(id);
            if (null != frame) {
                frame.kill();
            }
        }
    }

    public void minimize(final String winId) {
        if (!StringUtils.hasText(winId)) {
            this.minimizeAll();
        } else {
            final String id = this.createWinId(winId);
            final AppFrame frame = _frames.get(id);
            if (null != frame) {
                frame.minimize();
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String createWinId(final String winId) {
        final String appId = _app.getId();
        if (StringUtils.hasText(winId) && !winId.equalsIgnoreCase(appId)) {
            return StringUtils.concatDot(appId, winId);
        }
        return appId;
    }

    private AppFrame openById(final String id) {
        final AppFrame frame;
        if (_frames.contains(id)) {
            frame = _frames.get(id);
        } else {
            frame = new AppFrame(this, id);
            this.handleFrameEvents(frame);
        }
        frame.open();
        return frame;
    }

    private void handleFrameEvents(final AppFrame frame) {
        //-- open --//
        frame.onEvent(new Handlers.OnOpen() {
            @Override
            public void handle(final FrameOpenEvent event) {
                handleOpenFrame(frame);
                _eventHandler.triggerAsync(EVENT_ON_OPEN, event);
            }
        });

        //-- close --//
        frame.onEvent(new Handlers.OnClose() {
            @Override
            public void handle(final FrameCloseEvent event) {
                handleCloseFrame(frame);
                _eventHandler.trigger(EVENT_ON_CLOSE, event);
            }
        });
    }

    private void closeAll() {
        final AppFrame[] frames = _frames.removeAll();
        for (final AppFrame frame : frames) {
            try {
                frame.close();
            } catch (Throwable ignored) {
            }
        }
    }

    private void killAll() {
        final AppFrame[] frames = _frames.removeAll();
        for (final AppFrame frame : frames) {
            try {
                frame.kill();
            } catch (Throwable ignored) {
            }
        }
    }

    private void minimizeAll() {
        final AppFrame[] frames = _frames.getAll();
        for (final AppFrame frame : frames) {
            try {
                frame.minimize();
            } catch (Throwable ignored) {
            }
        }
    }

    private void handleCloseFrame(final AppFrame frame) {
        // remove if exists
        _frames.remove(frame);
    }

    private void handleOpenFrame(final AppFrame frame) {
        _frames.put(frame);
    }
}
