package com.example.planandmeet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;

public class ViewPdf extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        webView = findViewById(R.id.pdfViewPage);

        webView.getSettings().setJavaScriptEnabled(true);

        String fileName = getIntent().getStringExtra("filename");
        String fileUrl = getIntent().getStringExtra("fileurl");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(fileName);
        progressDialog.setMessage("Opening...");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });

        String url = "";
        try {
            url = URLEncoder.encode(fileUrl, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewPdf.this, DocumentsView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}