package com.sacconnect.model;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(length = 2000)
    private String text;

    private String imageUrl;

    private Instant createdAt = Instant.now();

    //getters and setters
    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public Chatroom getChatroom()
    {
        return chatroom;
    }
    public void setChatroom(Chatroom chatroom)
    {
        this.chatroom = chatroom;
    }
    public User getSender()
    {
        return sender;
    }
    public void setSender(User sender)
    {
        this.sender =sender;
    }
    public String getText()
    {
        return text;
    }
    public void setText(String text)
    {
        this.text = text;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
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
