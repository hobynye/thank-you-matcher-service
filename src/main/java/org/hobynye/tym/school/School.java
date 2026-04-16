package org.hobynye.tym.school;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "school")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String county;

    private String state;

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
