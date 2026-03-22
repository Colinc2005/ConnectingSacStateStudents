package com.sacconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    public Course() {
    }

    public Course(Major major, String code, String title) {
        this.major = major;
        this.code = code;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}