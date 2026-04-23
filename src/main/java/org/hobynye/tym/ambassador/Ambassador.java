package org.hobynye.tym.ambassador;

import jakarta.persistence.*;
import org.hobynye.tym.seminar.Seminar;
import java.util.UUID;

@Entity
@Table(name = "ambassador")
public class Ambassador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seminar_id", nullable = false)
    private Seminar seminar;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Intentional denormalization: stored as a plain string to simplify bulk upload.
    // Ambassadors are always queried by seminar, never by school, so an FK to the
    // school table would add a lookup-or-insert per row at import time with no benefit.
    @Column(name = "school_name")
    private String schoolName;

    @Column
    private String color;

    @Column(name = "group_code")
    private String groupCode;

    @Column
    private String county;

    public UUID getId() { return id; }

    public Seminar getSeminar() { return seminar; }
    public void setSeminar(Seminar seminar) { this.seminar = seminar; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }
}
