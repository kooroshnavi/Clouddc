package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Link;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Port {

    private boolean up;

    private Link link;

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
