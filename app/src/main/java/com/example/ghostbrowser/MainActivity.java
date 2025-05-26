package com.example.ghostbrowser;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends AppCompatActivity {
    private GhostBrowser ghostBrowser;
    private GeckoView geckoView;
    private EditText urlBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geckoView = findViewById(R.id.geckoview);
        urlBar = findViewById(R.id.url_bar);

        ghostBrowser = new GhostBrowser();
        ghostBrowser.initialize(this);
        ghostBrowser.getSession().setContentDelegate(new GeckoSession.ContentDelegate() {
            @Override
            public void onTitleChange(GeckoSession session, String title) {
                runOnUiThread(() -> getSupportActionBar().setTitle(title));
            }
        });
        
        geckoView.setSession(ghostBrowser.getSession());
        ghostBrowser.loadUrl("https://duckduckgo.com");

        findViewById(R.id.btn_go).setOnClickListener(v -> {
            String url = urlBar.getText().toString();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            ghostBrowser.loadUrl(url);
        });

        findViewById(R.id.btn_reload).setOnClickListener(v -> {
            ghostBrowser.reloadSession();
            geckoView.setSession(ghostBrowser.getSession());
            ghostBrowser.loadUrl(urlBar.getText().toString());
        });
    }
}

