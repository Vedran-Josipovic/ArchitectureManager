package app.prod.model;

import java.util.Objects;

public class VirtualLocation implements Location {
    private Long id;
    private String meetingLink;
    private String platform;


    public VirtualLocation(Long id, String meetingLink, String platform) {
        this.id = id;
        this.meetingLink = meetingLink;
        this.platform = platform;
    }

    public VirtualLocation(String meetingLink, String platform) {
        this.meetingLink = meetingLink;
        this.platform = platform;
    }

    @Override
    public String getFullLocationDetails() {
        return "Join online via " + getPlatform() + ": " + getMeetingLink();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualLocation that = (VirtualLocation) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getMeetingLink(), that.getMeetingLink()) && Objects.equals(getPlatform(), that.getPlatform());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMeetingLink(), getPlatform());
    }

    @Override
    public String toString() {
        return "VirtualLocation{" + "id=" + id + ", meetingLink='" + meetingLink + '\'' + ", platform='" + platform + '\'' + '}';
    }
}
