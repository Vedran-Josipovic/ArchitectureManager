package app.prod.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class Meeting extends Entity implements Serializable {
    private LocalDateTime meetingStart;
    private LocalDateTime meetingEnd;
    private Location location;
    private Set<Contact> participants;
    private String notes;

    public Meeting(LocalDateTime meetingStart, LocalDateTime meetingEnd, Location location, Set<Contact> participants, String notes) {
        this.meetingStart = meetingStart;
        this.meetingEnd = meetingEnd;
        this.location = location;
        this.participants = participants;
        this.notes = notes;
    }

    public Meeting(Long id, String name, LocalDateTime meetingStart, LocalDateTime meetingEnd, Location location, Set<Contact> participants, String notes) {
        super(id, name);
        this.meetingStart = meetingStart;
        this.meetingEnd = meetingEnd;
        this.location = location;
        this.participants = participants;
        this.notes = notes;
    }

    public Meeting(String name, LocalDateTime meetingStart, LocalDateTime meetingEnd, Location location, Set<Contact> participants, String notes) {
        super(name);
        this.meetingStart = meetingStart;
        this.meetingEnd = meetingEnd;
        this.location = location;
        this.participants = participants;
        this.notes = notes;
    }

    public Meeting() {
        super();
    }

    public LocalDateTime getMeetingStart() {
        return meetingStart;
    }

    public void setMeetingStart(LocalDateTime meetingStart) {
        this.meetingStart = meetingStart;
    }

    public LocalDateTime getMeetingEnd() {
        return meetingEnd;
    }

    public void setMeetingEnd(LocalDateTime meetingEnd) {
        this.meetingEnd = meetingEnd;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<Contact> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Contact> participants) {
        this.participants = participants;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(getMeetingStart(), meeting.getMeetingStart()) && Objects.equals(getMeetingEnd(), meeting.getMeetingEnd()) && Objects.equals(getLocation(), meeting.getLocation()) && Objects.equals(getParticipants(), meeting.getParticipants()) && Objects.equals(getNotes(), meeting.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMeetingStart(), getMeetingEnd(), getLocation(), getParticipants(), getNotes());
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingStart=" + meetingStart +
                ", meetingEnd=" + meetingEnd +
                ", location=" + location +
                ", participants=" + participants +
                ", notes='" + notes + '\'' +
                "} " + super.toString();
    }
}
