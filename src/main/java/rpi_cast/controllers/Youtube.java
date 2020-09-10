package rpi_cast.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rpi_cast.common.Tools;

@Controller
public class Youtube {

    @GetMapping("/youtube")
    public String mainForm() {
        return "youtube";
    }

    @GetMapping("/youtube/play/{id}")
    public @ResponseBody YoutubeData playYoutube(@RequestParam("data") String str){
        // Tools.runCommand(true, "mpv " + "https://www.youtube.com/watch?v=" + str);

        List<String> imgsList = new ArrayList<>();
        List<String> urlsList = new ArrayList<>();
        List<String> infosList = new ArrayList<>();
        String ytSearch = "https://www.youtube.com/watch?v=" + str;

        try {
            Document doc = Jsoup.connect(ytSearch).maxBodySize(1024*1024*3).userAgent("Mozilla").get();

            Elements videos = doc.select("div#watch7-sidebar-modules").select("div.watch-sidebar-section");
            
            Element nextVid = videos.first().select("li.related-list-item").first();

            urlsList.add(nextVid.select("div.thumb-wrapper").select("span[data-vid]").attr("data-vid"));
            imgsList.add(nextVid.select("div.thumb-wrapper").select("img").attr("data-thumb"));
            String vidTitle = nextVid.select("div.content-wrapper").select("span.title").text();
            String vidTime = nextVid.select("div.thumb-wrapper").select("span.video-time").text();
            infosList.add(vidTitle + " " + vidTime);

            Elements relatedVids = videos.last().select("ul#watch-related").select("li.related-list-item-compact-video");

            for (Element var : relatedVids) {
                urlsList.add(var.select("div.thumb-wrapper").select("span[data-vid]").attr("data-vid"));
                imgsList.add(var.select("div.thumb-wrapper").select("img").attr("data-thumb"));
                vidTitle = var.select("div.content-wrapper").select("span.title").text();
                vidTime = var.select("div.thumb-wrapper").select("span.video-time").text();
                infosList.add(vidTitle + " " + vidTime);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YoutubeData(imgsList, urlsList, infosList);
    }

    @GetMapping("/youtube/parse")
    public @ResponseBody YoutubeData parseYoutube(@RequestParam("data") String str){
        List<String> imgsList = new ArrayList<>();
        List<String> urlsList = new ArrayList<>();
        List<String> infosList = new ArrayList<>();
        String ytSearch = "https://www.youtube.com/results?search_query=" + str;

        try {
            Document doc = Jsoup.connect(ytSearch).maxBodySize(1024*1024*3).userAgent("Mozilla").get();

            Elements videos = doc.select("ol.item-section").select("li");

            Elements imgs = videos.select("img");
            for (Element e : imgs) {
                String img = e.attr("src");
                if (img.contains(".gif")) img = e.attr("data-thumb");
                if (!img.isEmpty()) imgsList.add(img);
            }
            
            Elements urls = videos.select("div[data-context-item-id]");
            for (Element e : urls) urlsList.add(e.attr("data-context-item-id"));

            Elements titles = videos.select("h3.yt-lockup-title");
            for (Element e : titles) infosList.add(e.text());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new YoutubeData(imgsList, urlsList, infosList);
    }
}