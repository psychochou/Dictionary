package euphoria.psycho.dictionary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class QueryTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "QueryTask";

    private static final String URL_BING = "https://cn.bing.com/dict/search?q=";

    private final Context mContext;

    private FloatingView mFloatingView;

    @Override
    protected String doInBackground(String... strings) {
        String word = strings[0];
        String result = DataProvider.getInstance().queryWord(word);

        if (result.length() == 0) {

            String bingResponse = queryFromBing(word).trim();
            if (bingResponse.length() == 0) return "未找到";
            bingResponse = extractBing(bingResponse);
            if (bingResponse.length() == 0) return "未找到";
            DataProvider.getInstance().insert(word, bingResponse);
            return word + " / " + bingResponse;
        }
        // result += queryFromBing(word);
        return word + " / " + result;
    }

    @Override
    protected void onPreExecute() {

        mFloatingView = new FloatingView(mContext);
    }

    private String extractBing(String response) {

        String r = "";
        Document document = Jsoup.parse(response);
        org.jsoup.nodes.Element element = document.selectFirst(".hd_p1_1");
        if (element != null) {
            r = element.text().trim() + " ";
        }
        Elements elements = document.select(".def");
        if (elements != null && elements.size() > 0) {
            r += elements.get(0).text();

        }

        return r;
    }

    private String queryFromBing(String word) {

        try {
            URL url = new URL(URL_BING + URLEncoder.encode(word, "UTF-8"));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            String strLine;
            while ((strLine = reader.readLine()) != null) {

                stringBuilder.append(strLine).append('\n');
            }

            reader.close();
            return stringBuilder.toString();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        mFloatingView.setText(s);
    }


    public QueryTask(Context context) {
        mContext = context;
    }
}
