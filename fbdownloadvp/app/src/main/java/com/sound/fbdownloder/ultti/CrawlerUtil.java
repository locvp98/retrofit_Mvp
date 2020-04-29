package com.sound.fbdownloder.ultti;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class CrawlerUtil {
    public enum TypeInfo {
        RESOLUTION, FORMAT, SIZE, DOWNLOAD
    }

    public static String crawInfo(Elements elements, TypeInfo type) {
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String str = element.text();

            switch (type) {
                case RESOLUTION: {
                    if (isResolution(str)) {
                        return str;
                    } else {
                        break;
                    }
                }

                case FORMAT: {
                    if (isFormat(str)) {
                        return str;
                    } else {
                        break;
                    }
                }

                case SIZE: {
                    if (isSize(str)) {
                        return str;
                    } else {
                        break;
                    }
                }

                case DOWNLOAD: {
                    if (isDownloadLink(element)) {
                        Elements a = element.select("a");
                        return a.attr("href");
                    } else {
                        break;
                    }
                }
            }
        }

        return "";
    }

    private static boolean isResolution(String str) {
        String temp = str.toUpperCase();
        return temp.contains("HD") || temp.contains("SD") || temp.contains("AUDIO");
    }

    private static boolean isFormat(String str) {
        String temp = str.toUpperCase();
        return temp.contains("MP") || temp.contains("MP4") || temp.contains("MP3");
    }

    private static boolean isSize(String str) {
        String temp = str.toUpperCase();
        return temp.contains("MB") || temp.contains("KB");
    }

    private static boolean isDownloadLink(Element element) {
        Elements a = element.select("a");
        String download = a.attr("href").toUpperCase();
        return download.contains(".MP4")|| download.contains(".MP3");
    }

    public static String parseJson(String strUrl, String requestMethod) {
        try {
            URL url = new URL(strUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            if (!requestMethod.isEmpty()) {
                con.setRequestMethod(requestMethod);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getQuery(NameValuePair params) throws UnsupportedEncodingException {
        return URLEncoder.encode(params.name, "UTF-8") +
                "=" +
                URLEncoder.encode(params.value, "UTF-8");
    }

    public static class NameValuePair {
        private String name;
        private String value;

        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}