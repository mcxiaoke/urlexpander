package com.mcxiaoke.urlexpander;

import android.content.Context;
import com.koushikdutta.ion.Ion;
import com.mcxiaoke.commons.os.TaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * User: mcxiaoke
 * Date: 14-2-6
 * Time: 11:18
 */
final class Api {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static final String API_UNSHORTEN_IT_URL = "http://api.unshorten.it";
    public static final String API_UNSHORTEN_IT_KEY = "46cf09e03460d4687d2aec121153406e";

    public static String doExpandUrlByUnShortenIt(
            final String shortUrl, final TaskExecutor.TaskCallback<String> callback, final Context context) {
        final Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return expand(context, shortUrl);
            }
        };
        return TaskExecutor.getInstance().execute(callable, callback, context);

    }

    public static String expand(Context context, String shortUrl) throws ExecutionException, InterruptedException {

//        Map<String, String> params = new HashMap<String, String>();
//        params.put("return", "fullurl");
//        params.put("responseFormat", "text");
//        params.put("apiKey", API_UNSHORTEN_IT_KEY);
//        params.put("shortURL", shortUrl);

        return Ion.with(context, API_UNSHORTEN_IT_URL)
                .addQuery("apiKey", API_UNSHORTEN_IT_KEY)
                .addQuery("return", "fullurl")
                .addQuery("responseFormat", "text")
                .addQuery("shortURL", shortUrl).asString().get();
    }

}
