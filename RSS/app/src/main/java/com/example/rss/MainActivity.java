package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static String rssUrl = "https://rss.joins.com/sonagi/joins_sonagi_sports_list.xml";

    ProgressDialog progressDialog;
    Handler handler = new Handler();

    RecyclerView recyclerView;
    RSSNewsAdapter adapter;
    ArrayList<RSSNewsItem> newsItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RSSNewsAdapter();
        recyclerView.setAdapter(adapter);


        newsItemList = new ArrayList<RSSNewsItem>();


        showRSS(rssUrl);


    }

    private void showRSS(String urlStr) {
        try {
            //progressDialog = ProgressDialog.show(this, "RSS Refresh", "RSS 정보 업데이트 중...", true, true);

            RefreshThread thread = new RefreshThread(urlStr);
            thread.start();

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }
    }

    class RefreshThread extends Thread {
        String urlStr;

        public RefreshThread(String str) {
            urlStr = str;
        }

        public void run() {

            try {

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                URL urlForHttp = new URL(urlStr);

                InputStream inputStream = getInputStreamUsingHTTP(urlForHttp);
                Document document = builder.parse(inputStream);
                int countItem = processDocument(document);
                Log.d(TAG, countItem + " news item processed.");

                // post for the display of fetched RSS info.
                handler.post(updateRSSRunnable);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public InputStream getInputStreamUsingHTTP(URL url)
            throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        int resCode = conn.getResponseCode();
        Log.d(TAG, "Response Code : " + resCode);

        InputStream inputStream = conn.getInputStream();

        return inputStream;
    }

    private int processDocument(Document doc) {
        newsItemList.clear();

        Element docEle = doc.getDocumentElement();
        NodeList nodelist = docEle.getElementsByTagName("item");
        int count = 0;
        if ((nodelist != null) && (nodelist.getLength() > 0)) {
            for (int i = 0; i < nodelist.getLength(); i++) {
                RSSNewsItem newsItem = dissectNode(nodelist, i);
                if (newsItem != null) {
                    newsItemList.add(newsItem);
                    count++;
                }
            }
        }

        return count;
    }

    private RSSNewsItem dissectNode(NodeList nodelist, int index) {
        RSSNewsItem newsItem = null;

        try {
            Element entry = (Element) nodelist.item(index);

            Element title = (Element) entry.getElementsByTagName("title").item(0);
            Element link = (Element) entry.getElementsByTagName("link").item(0);

            NodeList pubDataNode = entry.getElementsByTagName("pubDate");
            if (pubDataNode == null) {
                pubDataNode = entry.getElementsByTagName("dc:date");
            }
            Element pubDate = (Element) pubDataNode.item(0);


            String titleValue = null;
            if (title != null) {
                Node firstChild = title.getFirstChild();
                if (firstChild != null) {
                    titleValue = firstChild.getNodeValue();
                }
            }
            String linkValue = null;
            if (link != null) {
                Node firstChild = link.getFirstChild();
                if (firstChild != null) {
                    linkValue = firstChild.getNodeValue();
                }
            }


            String pubDateValue = null;
            if (pubDate != null) {
                Node firstChild = pubDate.getFirstChild();
                if (firstChild != null) {
                    pubDateValue = firstChild.getNodeValue();
                }
            }


            Log.d(TAG, "item node : " + titleValue + ", " + linkValue + ", " + pubDateValue);

            newsItem = new RSSNewsItem(titleValue, linkValue, pubDateValue);

        } catch (DOMException e) {
            e.printStackTrace();
        }

        return newsItem;
    }


    Runnable updateRSSRunnable = new Runnable() {
        public void run() {

            try {

                for (int i = 0; i < newsItemList.size(); i++) {
                    RSSNewsItem newsItem = newsItemList.get(i);
                    adapter.addItem(newsItem);
                }

                adapter.notifyDataSetChanged();

                progressDialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    };
}
