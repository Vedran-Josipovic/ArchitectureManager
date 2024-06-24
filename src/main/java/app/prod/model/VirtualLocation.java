package app.prod.model;

import java.util.Objects;

public class VirtualLocation implements Location {
    private String meetingLink;
    private String platform;

    public VirtualLocation(String meetingLink, String platform) {
        this.meetingLink = meetingLink;
        this.platform = platform;
    }

    @Override
    public String getFullLocationDetails() {
        return "Join online via " + getPlatform() + ": " + getMeetingLink();
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
        return Objects.equals(getMeetingLink(), that.getMeetingLink()) && Objects.equals(getPlatform(), that.getPlatform());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeetingLink(), getPlatform());
    }

    @Override
    public String toString() {
        return "VirtualLocation{" + "meetingLink='" + meetingLink + '\'' + ", platform='" + platform + '\'' + '}';
    }
}
