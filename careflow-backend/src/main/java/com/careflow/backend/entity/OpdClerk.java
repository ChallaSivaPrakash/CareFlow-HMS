package com.careflow.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "opd_clerks")
public class OpdClerk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String contactNumber;
    private String department;
    private String address;
    
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImageUrl;

    private boolean isActive = true;

    public OpdClerk() {}

    public OpdClerk(String name, String email, String contactNumber, String department, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.department = department;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Manual Builder
    public static OpdClerkBuilder builder() {
        return new OpdClerkBuilder();
    }

    public static class OpdClerkBuilder {
        private String name;
        private String email;
        private String contactNumber;
        private String department;
        private String address;
        private String bio;
        private String profileImageUrl;

        public OpdClerkBuilder name(String name) { this.name = name; return this; }
        public OpdClerkBuilder email(String email) { this.email = email; return this; }
        public OpdClerkBuilder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public OpdClerkBuilder department(String department) { this.department = department; return this; }
        public OpdClerkBuilder address(String address) { this.address = address; return this; }
        public OpdClerkBuilder bio(String bio) { this.bio = bio; return this; }
        public OpdClerkBuilder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }

        public OpdClerk build() {
            OpdClerk clerk = new OpdClerk(name, email, contactNumber, department, profileImageUrl);
            clerk.setAddress(address);
            clerk.setBio(bio);
            return clerk;
        }
    }
}
