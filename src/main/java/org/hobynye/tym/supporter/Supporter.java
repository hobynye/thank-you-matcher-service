package org.hobynye.tym.supporter;

import jakarta.persistence.*;
import org.hobynye.tym.seminar.Seminar;
import java.util.UUID;

@Entity
@Table(name = "supporter")
public class Supporter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seminar_id", nullable = false)
    private Seminar seminar;

    @Enumerated(EnumType.STRING)
    @Column(name = "supporter_type", nullable = false, length = 20)
    private SupporterType supporterType;

    @Column(name = "letter_count", nullable = false)
    private int letterCount = 1;

    // Address fields
    @Column(length = 500)
    private String street;

    @Column(length = 500)
    private String street2;

    @Column(length = 255)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String zip;

    // Donor identity fields
    @Column(length = 500)
    private String name;

    @Column(name = "contact_name", length = 500)
    private String contactName;

    @Column(length = 255)
    private String category;

    @Column(length = 255)
    private String club;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    // Donor matching fields — drive phase 1 mandatory match logic
    @Column(name = "sponsored_school")
    private String sponsoredSchool;

    @Column(name = "sponsored_county")
    private String sponsoredCounty;

    @Column(name = "sponsored_j_staff")
    private String sponsoredJStaff;

    @Column(name = "sponsored_ambassador")
    private String sponsoredAmbassador;

    // Speaker / Panelist / Staff fields
    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String role;

    @Column(length = 100)
    private String color;

    @Column(name = "group_code", length = 50)
    private String groupCode;

    public UUID getId() { return id; }

    public Seminar getSeminar() { return seminar; }
    public void setSeminar(Seminar seminar) { this.seminar = seminar; }

    public SupporterType getSupporterType() { return supporterType; }
    public void setSupporterType(SupporterType supporterType) { this.supporterType = supporterType; }

    public int getLetterCount() { return letterCount; }
    public void setLetterCount(int letterCount) { this.letterCount = letterCount; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getStreet2() { return street2; }
    public void setStreet2(String street2) { this.street2 = street2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSponsoredSchool() { return sponsoredSchool; }
    public void setSponsoredSchool(String sponsoredSchool) { this.sponsoredSchool = sponsoredSchool; }

    public String getSponsoredCounty() { return sponsoredCounty; }
    public void setSponsoredCounty(String sponsoredCounty) { this.sponsoredCounty = sponsoredCounty; }

    public String getSponsoredJStaff() { return sponsoredJStaff; }
    public void setSponsoredJStaff(String sponsoredJStaff) { this.sponsoredJStaff = sponsoredJStaff; }

    public String getSponsoredAmbassador() { return sponsoredAmbassador; }
    public void setSponsoredAmbassador(String sponsoredAmbassador) { this.sponsoredAmbassador = sponsoredAmbassador; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
}