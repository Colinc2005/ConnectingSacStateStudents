package com.sacconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sacconnect.model.Message;
import com.sacconnect.model.User;

public interface MessageRepository extends JpaRepository<Message, Long>
{

    List<Message> findByChatroomIdOrderByCreatedAtAsc(Long chatroomId);

    @Query("select distinct m.sender from Message m " +
           "where m.chatroom.id = :chatroomId and m.sender is not null")
    List<User> findDistinctSendersByChatroomId(@Param("chatroomId") Long chatroomId);

    @Modifying
    @Query("update Message m set m.sender = null where m.sender.id = :userId")
    int clearSenderForUser(@Param("userId") Long userId);

}
