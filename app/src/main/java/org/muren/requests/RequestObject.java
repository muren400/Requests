package org.muren.requests;

import java.util.ArrayList;
import java.util.List;

public class RequestObject {
    public static final int MAX_URLS = 5;

    private String name = "";
    private List<String> urls = null;

    public RequestObject(String name){
        this.name = name;
        this.urls = new ArrayList<>();
    }

    public RequestObject(String name, List<String> urls){
        this.name = name;
        this.urls = urls;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getUrls() {
        return urls;
    }
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void addUrl(String url) {
        urls.add(url);
    }
}
