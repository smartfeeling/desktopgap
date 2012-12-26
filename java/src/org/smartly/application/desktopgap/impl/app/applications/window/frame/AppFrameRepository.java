package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class AppFrameRepository {

    private final Map<String, AppFrame> _repo;

    public AppFrameRepository() {
        _repo = Collections.synchronizedMap(new HashMap<String, AppFrame>());
    }

    public boolean contains(final String winId) {
        synchronized (_repo) {
            return _repo.containsKey(winId);
        }
    }

    public void put(final AppFrame window) {
        if (null != window) {
            synchronized (_repo) {
                _repo.put(window.getId(), window);
            }
        }
    }

    public AppFrame get(final String winId) {
        return _repo.get(winId);
    }

    public AppFrame[] getAll() {
        synchronized (_repo) {
            final Collection<AppFrame> frames = _repo.values();
            return frames.toArray(new AppFrame[frames.size()]);
        }
    }

    public AppFrame[] removeAll() {
        synchronized (_repo) {
            final Collection<AppFrame> frames = _repo.values();
            final AppFrame[] result = frames.toArray(new AppFrame[frames.size()]);
            frames.clear();
            return result;
        }
    }

    public AppFrame remove(final String winId) {
        synchronized (_repo) {
            return _repo.remove(winId);
        }
    }

    public void remove(final AppFrame frame) {
        synchronized (_repo) {
            if (null != frame) {
                if (_repo.containsKey(frame.getId())) {
                    _repo.remove(frame.getId());
                }
            }
        }

    }

    public int size() {
        return _repo.size();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
