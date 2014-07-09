package com.mcxiaoke.urlexpander;

import android.content.Context;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextParams;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-2-6
 * Time: 11:18
 */
final class Utils {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static final String API_URL = "http://tools.mcxiaoke.com/unshorten.php";
    public static final String API_URL2 = "http://api.longurl.org/v2/expand";

    public static String doExpand(
            final String shortUrl, final TaskCallback<String> callback, final Context context) {
        final Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return expand(shortUrl);
            }
        };
        return TaskQueue.getDefault().add(callable, callback, context);

    }

    public static String expand(final String shortUrl) throws IOException {
        final NextParams params = new NextParams();
        params.put("url", shortUrl);
        params.put("format", "json");
        NextResponse response;
        try {
            response = NextClient.get(API_URL, params);
        } catch (IOException e) {
            response = NextClient.get(API_URL2, params);
        }
        if (response != null && response.successful()) {
            return response.string();
        }
        return shortUrl;
    }


}
