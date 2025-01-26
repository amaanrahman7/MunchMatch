package com.group6.tinderforfood;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RestaurantParser {
    private static String getOriginal(String url) {
        int i;
        for (i = url.length()-1; i >= 0; i--) {
            if (i == '/') {
                break;
            }
        }
        return url.substring(0, i+1) + "O.jpg";
    }
    public static List<String> getPictures(String html) {
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("div.photo-box > img");
        List<String> urls = new ArrayList<>();
        String src;
        for (Element image: images) {
            src = image.attr("src");
            if(src != null & !src.equals("")) {
                urls.add(src);
            }
        }
        return urls;
    }
}
