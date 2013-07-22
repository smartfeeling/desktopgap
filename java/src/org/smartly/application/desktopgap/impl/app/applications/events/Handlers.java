package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.commons.Delegates;

/**
 *
 *
 */
public interface Handlers {


    public static interface OnDragOver extends Delegates.Handler {
        void handle(final FrameDragEvent event);
    }

    public static interface OnDragDropped extends Delegates.Handler {
        void handle(final FrameDragEvent event);
    }

    public static interface OnHidden extends Delegates.Handler {
        void handle(final FrameHiddenEvent event);
    }

    public static interface OnScroll extends Delegates.Handler {
        void handle(final FrameScrollEvent event);
    }

    public static interface OnResize extends Delegates.Handler {
        void handle(final FrameResizeEvent event);
    }

    public static interface OnKeyPressed extends Delegates.Handler {
        void handle(final FrameKeyPressedEvent event);
    }

    public static interface OnOpen extends Delegates.Handler {
        void handle(final FrameOpenEvent event);
    }

    public static interface OnClose extends Delegates.Handler {
        void handle(final FrameCloseEvent event);
    }

}
