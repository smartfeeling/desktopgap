package org.smartly.application.desktopgap.impl.app.applications;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.applications.events.AppCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.AppOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.IEventListener;
import org.smartly.commons.util.JsonWrapper;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public final class DesktopControllerApps
        implements IEventListener {

    private final DesktopController _desktop;

    private final Map<String, AppInstance> _registry_running;
    private final Map<String, AppManifest> _registry_installed;
    private final Set<String> _system_apps; // system apps

    public DesktopControllerApps(final DesktopController desktop) {
        _desktop = desktop;
        _registry_running = Collections.synchronizedMap(new HashMap<String, AppInstance>());
        _registry_installed = Collections.synchronizedMap(new HashMap<String, AppManifest>());
        _system_apps = Collections.synchronizedSet(new HashSet<String>());
    }

    public AppInstance getApplication(final String appId) throws IOException {
        if (this.isRunning(appId)) {
            return this.getRunning(appId);
        } else {
            final AppManifest manifest = this.getInstalled(appId);
            return this.createApp(manifest);
        }
    }

    public void closeApplication(final String appId) throws IOException {
        if (this.isRunning(appId)) {
            this.getRunning(appId).close();
        }
    }

    public Collection<String> getSystemAppNames() {
        synchronized (_system_apps) {
            return new ArrayList<String>(_system_apps);
        }
    }

    /**
     * Returns App names, excluding system apps
     *
     * @return collection of app names.
     */
    public Collection<String> getAppNames() {
        synchronized (_registry_installed) {
            final Collection<String> result = new ArrayList<String>();
            final Set<String> keys = _registry_installed.keySet();
            for (final String appId : keys) {
                if (!_system_apps.contains(appId)) {
                    result.add(appId);
                }
            }
            return result;
        }
    }

    /**
     * Returns Apps manifest grouped by category.
     *
     * @return A Map of App manifests grouped by category.
     */
    public Map<String, List<AppManifest>> getAppManifests() {
        synchronized (_registry_installed) {
            final Map<String, List<AppManifest>> result = new LinkedHashMap<String, List<AppManifest>>();
            final Set<String> keys = _registry_installed.keySet();
            for (final String appId : keys) {
                if (!_system_apps.contains(appId)) {
                    final AppManifest manifest = _registry_installed.get(appId);
                    final String category = manifest.getCategory();
                    if (!result.containsKey(category)) {
                        result.put(category, new LinkedList<AppManifest>());
                    }
                    result.get(category).add(manifest);
                }
            }
            return result;
        }
    }

    public JSONObject getAppManifestsAsJSON() {
        synchronized (_registry_installed) {
            final JsonWrapper result = new JsonWrapper(new JSONObject());
            final Set<String> keys = _registry_installed.keySet();
            for (final String appId : keys) {
                if (!_system_apps.contains(appId)) {
                    final AppManifest manifest = _registry_installed.get(appId);
                    final String category = manifest.getCategory();
                    if (!result.has(category)) {
                        result.putSilent(category, new JSONArray());
                    }
                    result.optJSONArray(category).put(manifest.getJson().getJSONObject());
                }
            }
            return result.getJSONObject();
        }
    }

    // --------------------------------------------------------------------
    //               installed
    // --------------------------------------------------------------------

    public AppManifest getInstalled(final String appId) {
        synchronized (_registry_installed) {
            return _registry_installed.get(appId);
        }
    }

    public AppManifest removeInstalled(final String appId) {
        synchronized (_registry_installed) {
            return _registry_installed.remove(appId);
        }
    }

    public void addInstalled(final String file, final boolean system) throws IOException {
        synchronized (_registry_installed) {
            final AppManifest manifest = new AppManifest(file, system);
            _registry_installed.put(manifest.getAppId(), manifest);
            if (system) {
                this.addSystem(manifest.getAppId());
            }
        }
    }

    public void addInstalled(final AppManifest manifest) {
        synchronized (_registry_installed) {
            _registry_installed.put(manifest.getAppId(), manifest);
        }
    }

    public boolean isInstalled(final String appId) {
        synchronized (_registry_installed) {
            return _registry_installed.containsKey(appId);
        }
    }

    // --------------------------------------------------------------------
    //               running
    // --------------------------------------------------------------------

    public AppInstance getRunning(final String appId) {
        synchronized (_registry_running) {
            return _registry_running.get(appId);
        }
    }

    public boolean isRunning(final String appId) {
        synchronized (_registry_running) {
            return _registry_running.containsKey(appId);
        }
    }

    // ------------------------------------------------------------------------
    //                      IEventListener
    // ------------------------------------------------------------------------

    @Override
    public void on(final Event event) {
        if (event instanceof AppCloseEvent) {
            // FRAME CLOSE
            this.handleCloseApp((AppInstance) event.getSender());
        } else if (event instanceof AppOpenEvent) {
            // FRAME OPEN
            this.handleOpenApp((AppInstance) event.getSender());
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addSystem(final String appId) {
        synchronized (_system_apps) {
            _system_apps.add(appId);
        }
    }

    private AppInstance createApp(final AppManifest manifest) throws IOException {
        final AppInstance app = new AppInstance(_desktop, manifest);
        app.addEventListener(this);
        return app;
    }

    private void handleOpenApp(final AppInstance app) {
        synchronized (_registry_running) {
            if (!_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.put(app.getId(), app);
            }
        }
    }

    private void handleCloseApp(final AppInstance app) {
        synchronized (_registry_running) {
            if (_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.remove(app.getId());
            }
        }
    }


}
