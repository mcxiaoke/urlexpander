package com.mcxiaoke.urlexpander;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.commons.os.TaskExecutor;
import com.mcxiaoke.commons.utils.StringUtils;
import com.mcxiaoke.commons.utils.SystemUtils;

import java.util.regex.Matcher;

public class ExpandActivity extends Activity {

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
                onUrlExpanded(result);
            }

            @Override
            public void onTaskFailure(Throwable throwable, Bundle bundle) {
                if (BuildConfig.DEBUG) {
                    throwable.printStackTrace();
                }
                onUrlExpandFailed(throwable);

            }
        };
        Api.doExpandUrlByUnShortenIt(mUrl, callback, this);
    }

    private void onUrlExpanded(String uriString) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        startActivity(intent);
        finish();
    }

    private void onUrlExpandFailed(Throwable e) {
        SystemUtils.showToast(this, R.string.url_expand_failed);
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
