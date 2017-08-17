package com.wzc.mobileplayer.Domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/23.
 */
public class MediaItem implements Serializable {
    private String name;
    private long duration;
    private long size;
    private String data;
    private String artist;

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", desc='" + desc + '\'' +
                ", heightUri='" + heightUri + '\'' +
                '}';
    }

    /*
        * 图片路径
         */
    String imageUri;

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setHeightUri(String heightUri) {
        this.heightUri = heightUri;
    }

    /*
        *  描述
         */
    String desc;

    public String getImageUri() {
        return imageUri;
    }

    public String getDesc() {
        return desc;
    }

    public String getHeightUri() {
        return heightUri;
    }

    /*
        *  高清视频地址
         */
    String heightUri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
