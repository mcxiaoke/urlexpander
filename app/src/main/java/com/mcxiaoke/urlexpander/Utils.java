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
final class Utils {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static final String API_URL = "http://api.longurl.org/v2/expand";

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

    public static String expand(final Context context, final String shortUrl) throws ExecutionException, InterruptedException {

        return Ion.with(context, API_URL).addQuery("url", shortUrl).addQuery("format", "json")
                .asString().get();
    }


}
