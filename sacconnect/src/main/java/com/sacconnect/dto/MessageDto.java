package com.sacconnect.dto;
import java.time.Instant;

import com.sacconnect.model.Message;

public record MessageDto (
    Long id,
    Long senderId,
    String senderName,
    String text,
    String imageUrl,
    Instant createdAt
)
{
    public static MessageDto from(Message m)
    {
        return new MessageDto(
            m.getId(),
            m.getSender() != null ? m.getSender().getId() : null,
            m.getSender() != null ? m.getSender().getName() : "Anonymous",
            m.getText(),
            m.getImageUrl(),
            m.getCreatedAt()
        );
    }
}
