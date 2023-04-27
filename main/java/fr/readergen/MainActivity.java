package fr.readergen;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView mywebView;
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String COOKIE_KEY = "cookie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mywebView = (WebView) findViewById(R.id.webview);
        mywebView.setWebViewClient(new MywebClient()); // utiliser la classe interne MywebClient
        WebSettings webSettings = mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Obtenez le gestionnaire de cookies de l'application
        CookieManager cookieManager = CookieManager.getInstance();

        // Permettre le stockage de cookies tiers (facultatif)
        cookieManager.setAcceptThirdPartyCookies(mywebView, true);

        // Obtenez les cookies enregistrés dans les préférences partagées de l'application
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String cookie = sharedPreferences.getString(COOKIE_KEY, null);

        if (cookie != null) {
            // Chargez le cookie dans le gestionnaire de cookies
            cookieManager.setCookie("https://readergen.synology.me/wordpress/", cookie);
        }

        // Chargez l'URL du site
        mywebView.loadUrl("https://readergen.synology.me/wordpress/");
    }

    // Classe interne MywebClient
    public class MywebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            // Chargez à nouveau l'URL en cas d'erreur
            view.loadUrl(failingUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (mywebView.canGoBack()) {
            mywebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Enregistrez le cookie dans les préférences partagées de l'application
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie("https://readergen.synology.me/wordpress/");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COOKIE_KEY, cookie);
        editor.apply();
    }
}



