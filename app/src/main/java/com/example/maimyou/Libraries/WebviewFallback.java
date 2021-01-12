package com.example.maimyou.Libraries;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.maimyou.Activities.ScanMarksActivity;

/**
 * A Fallback that opens a Webview when Custom Tabs is not available
 */
public class WebviewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, ScanMarksActivity.class);
        intent.putExtra(ScanMarksActivity.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }
}