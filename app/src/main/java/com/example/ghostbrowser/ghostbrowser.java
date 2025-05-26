package com.example.ghostbrowser;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONArray;
import org.mozilla.geckoview.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GhostBrowser {
    private final Random random = new Random();
    private JSONArray userAgents;
    private GeckoRuntime runtime;
    private GeckoSession session;
    
    private final String[] MOBILE_RESOLUTIONS = {
        "360x640", "375x667", "390x844", "412x732", "414x896", 
        "428x926", "393x873", "360x780", "412x915"
    };
    
    private final String[] TABLET_RESOLUTIONS = {
        "768x1024", "800x1280", "810x1080", 
        "1280x800", "600x1024", "962x601"
    };

    public void initialize(Context context) {
        loadUserAgents(context);
        configureRuntime(context);
        createNewSession(true);
    }

    private void loadUserAgents(Context context) {
        try {
            AssetManager assets = context.getAssets();
            InputStream is = assets.open("userAgents.json");
            userAgents = new JSONArray(new String(
                is.readAllBytes(), StandardCharsets.UTF_8));
            is.close();
        } catch (Exception e) {
            userAgents = new JSONArray()
                .put("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .put("Mozilla/5.0 (iPhone; CPU iPhone OS 16_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.4 Mobile/15E148 Safari/604.1")
                .put("Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
                .put("Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0");
        }
    }

    private void configureRuntime(Context context) {
        GeckoRuntimeSettings settings = new GeckoRuntimeSettings.Builder()
            .javaScriptEnabled(true)
            .aboutConfigEnabled(false)
            .extensionsEnabled(false)
            .remoteDebuggingEnabled(false)
            .consoleOutputEnabled(false)
            .preferredColorScheme(GeckoRuntimeSettings.COLOR_SCHEME_LIGHT)
            .cookieBehavior(GeckoRuntimeSettings.COOKIE_BEHAVIOR_REJECT_TRACKER_AND_PARTITION_FOREIGN)
            .trackingProtection(GeckoRuntimeSettings.TrackingProtectionMode.STRICT)
            .webFontsEnabled(false)
            .build();

        runtime = GeckoRuntime.getDefault(context);
        runtime.configure(settings);

        runtime.getSettings()
            .setBool(GeckoSessionSettings.RESIST_FINGERPRINTING, true)
            .setInt(GeckoSessionSettings.HARDWARE_CONCURRENCY, 2)
            .setInt(GeckoSessionSettings.DEVICE_MEMORY, 4)
            .setBool(GeckoSessionSettings.ACCELEROMETER_ENABLED, false)
            .setBool(GeckoSessionSettings.GYROSCOPE_ENABLED, false)
            .setBool(GeckoSessionSettings.MAGNETOMETER_ENABLED, false)
            .setBool(GeckoSessionSettings.PROXIMITY_ENABLED, false)
            .setBool(GeckoSessionSettings.AMBIENT_LIGHT_ENABLED, false)
            .setBool(GeckoSessionSettings.ORIENTATION_ENABLED, false)
            .setBool(GeckoSessionSettings.BATTERY_API_ENABLED, false)
            .setBool(GeckoSessionSettings.BLUETOOTH_ENABLED, false)
            .setBool(GeckoSessionSettings.USB_ENABLED, false)
            .setBool(GeckoSessionSettings.NFC_ENABLED, false)
            .setBool(GeckoSessionSettings.VIBRATION_ENABLED, false)
            .setBool(GeckoSessionSettings.GAMEPAD_ENABLED, false)
            .setBool(GeckoSessionSettings.WEBGL_ENABLED, false)
            .setBool(GeckoSessionSettings.WEBGL2_ENABLED, false)
            .setBool(GeckoSessionSettings.CANVAS_POISONING, true)
            .setBool(GeckoSessionSettings.AUDIO_CONTEXT_NOISE, true)
            .setBool(GeckoSessionSettings.WEBGPU_ENABLED, false)
            .setInt(GeckoSessionSettings.FONT_VISIBILITY_LEVEL, 0)
            .setBool(GeckoSessionSettings.PLUGINS_ENABLED, false)
            .setString(GeckoSessionSettings.WEBRTC_IP_HANDLING_POLICY, "disable_non_proxied_udp")
            .setBool(GeckoSessionSettings.EPHEMERAL_STORAGE, true)
            .setBool(GeckoSessionSettings.PRIVACY_SANDBOX_ENABLED, false)
            .setBool(GeckoSessionSettings.GEOLOCATION_ENABLED, false)
            .setBool(GeckoSessionSettings.SPEECH_RECOGNITION_ENABLED, false)
            .setBool(GeckoSessionSettings.LOCAL_STORAGE_ENABLED, false)
            .setBool(GeckoSessionSettings.SESSION_STORAGE_ENABLED, false)
            .setBool(GeckoSessionSettings.INDEXED_DB_ENABLED, false)
            .setBool(GeckoSessionSettings.CACHE_API_ENABLED, false)
            .setBool(GeckoSessionSettings.SERVICE_WORKER_ENABLED, false)
            .setBool(GeckoSessionSettings.NOTIFICATIONS_ENABLED, false)
            .setBool(GeckoSessionSettings.TIMEZONE_OVERRIDE_ENABLED, false)
            .setBool(GeckoSessionSettings.REDUCE_TIMER_PRECISION, true);
    }

    public void createNewSession(boolean randomizeFingerprint) {
        session = new GeckoSession();
        
        if (randomizeFingerprint) {
            randomizeFingerprint();
        }
        
        session.getSettings()
            .setTrackingProtection(GeckoSessionSettings.TrackingProtectionMode.STRICT)
            .setSuspendMediaWhenInactive(true)
            .setUsePrivateMode(true)
            .setDisplayMode(GeckoSessionSettings.DISPLAY_MODE_BROWSER)
            .setAllowScriptsToCloseWindows(false)
            .setPopupBlockingEnabled(true)
            .setForceUserScalableEnabled(true);
            
        session.setStorageDelegate(new GeckoSession.StorageDelegate() {
            @Override
            public void onStorageReady(GeckoSession session) {
                session.clearData(
                    GeckoSession.CLEAR_CACHE |
                    GeckoSession.CLEAR_COOKIES |
                    GeckoSession.CLEAR_SITE_SETTINGS |
                    GeckoSession.CLEAR_INDEXED_DB |
                    GeckoSession.CLEAR_LOCAL_STORAGE |
                    GeckoSession.CLEAR_SERVICE_WORKERS |
                    GeckoSession.CLEAR_SESSION_STORAGE
                );
            }
        });
    }

    private void randomizeFingerprint() {
        try {
            session.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
                @Override
                public void onLocationChange(GeckoSession session, String url) {
                    String userAgent = userAgents.getString(random.nextInt(userAgents.length()));
                    session.getSettings().setUserAgentOverride(userAgent);
                }
            });

            String userAgent = userAgents.getString(random.nextInt(userAgents.length()));
            boolean isMobile = random.nextBoolean();
            String[] resolutions = isMobile ? MOBILE_RESOLUTIONS : TABLET_RESOLUTIONS;
            String resolution = resolutions[random.nextInt(resolutions.length)];
            String[] dims = resolution.split("x");
            
            session.getSettings()
                .setUserAgentOverride(userAgent)
                .setViewportMode(isMobile ? 
                    GeckoSession.VIEWPORT_MODE_MOBILE : 
                    GeckoSession.VIEWPORT_MODE_TABLET)
                .setScreenSizeOverride(
                    Integer.parseInt(dims[0]), 
                    Integer.parseInt(dims[1]))
                .setDevicePixelRatioOverride((random.nextFloat() * 1.5f) + 0.5f)
                .setForceUserScalableEnabled(true);
            
            runtime.getSettings()
                .setInt(GeckoSessionSettings.HARDWARE_CONCURRENCY, 
                       random.nextBoolean() ? 2 : 4)
                .setInt(GeckoSessionSettings.DEVICE_MEMORY, 
                       random.nextBoolean() ? 4 : 8);
            
        } catch (Exception e) {
            session.getSettings()
                .setUserAgentOverride("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .setScreenSizeOverride(1920, 1080);
        }
    }

    public void loadUrl(String url) {
        if (session != null) {
            session.loadUri(url);
        }
    }

    public void reloadSession() {
        createNewSession(true);
    }

    public GeckoSession getSession() {
        return session;
    }

    public GeckoRuntime getRuntime() {
        return runtime;
    }
}

