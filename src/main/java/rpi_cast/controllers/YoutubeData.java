package rpi_cast.controllers;

import java.util.List;

public class YoutubeData {

    private final List<String> imgs;
    private final List<String> urls;
    private final List<String> infos;

    public YoutubeData(List<String> imgs, List<String> urls, List<String> infos) {
        this.imgs = imgs;
        this.urls = urls;
        this.infos = infos;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getInfos() {
        return infos;
    }
}