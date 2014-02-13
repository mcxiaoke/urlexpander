package com.mcxiaoke.urlexpander;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.commons.os.TaskExecutor;
import com.mcxiaoke.commons.utils.LogUtils;
import com.mcxiaoke.commons.utils.StringUtils;
import com.mcxiaoke.commons.utils.AndroidUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

public class ExpandActivity extends Activity {
    public static final String TAG = ExpandActivity.class.getSimpleName();

    @InjectView(android.R.id.progress)
    ProgressBar mProgressBar;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_expand);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            mUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        } else {
            final Uri uri = intent.getData();
            mUrl = uri == null ? null : uri.toString();
        }

        if (BuildConfig.DEBUG) {
            LogUtils.v(TAG, "mUrl=" + mUrl);
        }

        if (StringUtils.isEmpty(mUrl)) {
            finish();
            return;
        }

        Matcher matcher = Patterns.WEB_URL.matcher(mUrl);
        if (!matcher.matches()) {
            finish();
            return;
        }

        doExpand();

    }

    private void doExpand() {
        final TaskExecutor.TaskCallback<String> callback = new TaskExecutor.TaskCallback<String>() {
            @Override
            public void onTaskSuccess(String result, Bundle bundle, Object o) {
                String url = mUrl;
                try {
                    JSONObject json = new JSONObject(result);
                    url = json.getString("long-url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onUrlExpanded(url);
            }

            @Override
            public void onTaskFailure(Throwable throwable, Bundle bundle) {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace();
                }
                onUrlExpandFailed(getString(R.string.url_expand_failed));

            }
        };
        Utils.doExpandUrlByUnShortenIt(mUrl, callback, this);
    }

    private void onUrlExpanded(String uriString) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onUrlExpanded: uriString=" + uriString);
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            startActivity(intent);
        }
        finish();
    }

    private void onUrlExpandFailed(String message) {
        AndroidUtils.showToast(this, message);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
