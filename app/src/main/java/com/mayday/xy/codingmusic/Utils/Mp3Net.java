package com.mayday.xy.codingmusic.Utils;

/**
 * Created by xy-pc on 2016/11/16.
 */

public class Mp3Net {
    private String artist;//歌手
    private String title;//歌名
    private String Url;
    private String album;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
