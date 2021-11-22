package com.example.azzzqz.javabean;

public class News {
    private String title;
    private String pic;
    private String ownername;
    private String content_url;
    private String tname;

    public News(String title, String pic, String ownername, String content_url, String tname) {
        this.title = title;
        this.pic = pic;
        this.ownername = ownername;
        this.content_url = content_url;
        this.tname = tname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }
}
