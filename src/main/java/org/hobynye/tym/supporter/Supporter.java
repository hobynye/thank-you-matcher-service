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

    // Address fields — applicable to all supporter types
    @Column(length = 500)
    private String street;
    @Column(length = 255)
    private String city;
    @Column(length = 100)
    private String state;
    @Column(length = 20)
    private String zip;

    // Donor fields
    @Column(name = "full_name", length = 500)
    private String fullName;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(length = 500)
    private String organization;

    @Column(name = "donation_info")
    private String donationInfo;

    @Column(name = "donor_type")
    private String donorType;

    // Donor matching fields
    @Column(name = "beneficiary_first")
    private String beneficiaryFirst;

    @Column(name = "beneficiary_last")
    private String beneficiaryLast;

    @Column(name = "sponsored_school")
    private String sponsoredSchool;

    @Column(name = "sponsor_county")
    private String sponsorCounty;

    // Speaker / Panelist / Staff fields
    @Column(length = 255)
    private String title;
    @Column(length = 255)
    private String role;

    // Staff matching fields
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

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getDonationInfo() { return donationInfo; }
    public void setDonationInfo(String donationInfo) { this.donationInfo = donationInfo; }

    public String getDonorType() { return donorType; }
    public void setDonorType(String donorType) { this.donorType = donorType; }

    public String getBeneficiaryFirst() { return beneficiaryFirst; }
    public void setBeneficiaryFirst(String beneficiaryFirst) { this.beneficiaryFirst = beneficiaryFirst; }

    public String getBeneficiaryLast() { return beneficiaryLast; }
    public void setBeneficiaryLast(String beneficiaryLast) { this.beneficiaryLast = beneficiaryLast; }

    public String getSponsoredSchool() { return sponsoredSchool; }
    public void setSponsoredSchool(String sponsoredSchool) { this.sponsoredSchool = sponsoredSchool; }

    public String getSponsorCounty() { return sponsorCounty; }
    public void setSponsorCounty(String sponsorCounty) { this.sponsorCounty = sponsorCounty; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
}
