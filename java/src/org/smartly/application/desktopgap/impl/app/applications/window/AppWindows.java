package org.smartly.application.desktopgap.impl.app.applications.window;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrameRepository;
import org.smartly.application.desktopgap.impl.app.applications.events.IDesktopGapEvents;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.event.IEventListener;
import org.smartly.commons.util.StringUtils;


/**
 * Application Window Manager.
 * Each application can host one or more windows.
 */
public final class AppWindows
        extends EventEmitter
        implements IEventListener {

    private final AppInstance _app;
    private final AppFrameRepository _frames;


    public AppWindows(final AppInstance app) {
        _app = app;
        _frames = new AppFrameRepository();
    }

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

    // --------------------------------------------------------------------
    //                      IEventListener
    // --------------------------------------------------------------------

    @Override
    public void on(final Event event) {
        if (event.getName().equalsIgnoreCase(IDesktopGapEvents.FRAME_CLOSE)) {
            // CLOSE
            this.handleCloseFrame((AppFrame) event.getSender());
        } else if (event.getName().equalsIgnoreCase(IDesktopGapEvents.FRAME_OPEN)) {
            // OPEN
            this.handleOpenFrame((AppFrame) event.getSender());
        }
        // emit events for AppInstance to manage application close
        this.emit(event);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String createWinId(final String winId) {
        final String appId = _app.getId();
        return StringUtils.hasText(winId) ? appId.concat(winId) : appId;
    }

    private AppFrame openById(final String id) {
        final AppFrame frame;
        if (_frames.contains(id)) {
            frame = _frames.get(id);
        } else {
            frame = new AppFrame(this, id);
            frame.addEventListener(this);

        }
        frame.open();
        return frame;
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
