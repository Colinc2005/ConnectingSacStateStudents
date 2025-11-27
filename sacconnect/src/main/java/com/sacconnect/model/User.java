package com.sacconnect.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    private Integer age;
    private String major;
    @Column(length = 500)
    private String bio;
    private boolean verified = false;  //whether the email and authentication is completed
    @Column(nullable = false)
    private String username;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    @CollectionTable(
        name = "user_interests",
        joinColumns= @JoinColumn(name = "user_id")
    )

    @Column(name = "interest")
    private Set<String> interests = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "user_tags",
        joinColumns= @JoinColumn(name = "user_id")
    )

    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public User() {}

    //============ Getter and Setters ===============
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Integer getAge()
    {
        return age;
    }
    public void setAge(Integer age)
    {
        this.age = age;
    }
    public String getMajor()
    {
        return major;
    }
    public void setMajor(String major)
    {
        this.major = major;
    }
    public String getBio()
    {
        return bio;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }
    public Set<String> getInterests()
    {
        return interests;
    }
    public void setInterests(Set<String> interests)
    {
        this.interests = interests;
    }
    public Set<String> getTags()
    {
        return tags;
    }
    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }
    public boolean isVerified()
    {
        return verified;
    }
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt)
    {
        this.createdAt = createdAt;
    }








    
    


}