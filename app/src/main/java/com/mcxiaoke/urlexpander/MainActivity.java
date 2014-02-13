package com.mcxiaoke.urlexpander;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mcxiaoke.commons.os.TaskExecutor;
import com.mcxiaoke.commons.utils.AndroidUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

public class MainActivity extends Activity implements TextWatcher {

    @InjectView(android.R.id.edit)
    EditText mEditText;

    @InjectView(android.R.id.button1)
    Button mButton;

    @InjectView(android.R.id.progress)
    ProgressBar mProgressBar;

    @InjectView(android.R.id.text1)
    TextView mTextView;

    @InjectView(android.R.id.empty)
    TextView mEmptyTextView;

    private String mShortUrl;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.inject(this);
        mEditText.addTextChangedListener(this);

        showEmpty();
    }

    private void showEmpty() {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);
        mButton.setEnabled(true);
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        mButton.setEnabled(false);
    }

    private void showContent() {
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.VISIBLE);
        mEmptyTextView.setVisibility(View.GONE);
        mButton.setEnabled(true);
    }

    @OnClick(android.R.id.button1)
    void onButtonClick() {
        Matcher matcher = Patterns.WEB_URL.matcher(mShortUrl);
        if (!matcher.matches()) {
            AndroidUtils.showToast(this, R.string.invalid_url);
        } else {
            AndroidUtils.hideSoftKeyboard(this, mEditText);
            doExpand();
            showProgress();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mShortUrl = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {

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
        mUrl = uriString;
        mTextView.setText(mUrl);
        showContent();
    }

    private void onUrlExpandFailed(String message) {
        mUrl = null;
        mEmptyTextView.setText(message);
    }

}
