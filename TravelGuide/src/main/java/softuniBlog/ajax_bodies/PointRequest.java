package softuniBlog.ajax_bodies;

import softuniBlog.entity.Image;

public class PointRequest {
    private String event;
    private String comment;
    private Image image;
    private Double lat;
    private Double lon;
    private Integer dest_id;

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getDest_id() {
        return this.dest_id;
    }

    public void setDest_id(Integer dest_id) {
        this.dest_id = dest_id;
    }
}