package modules.chat.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDto {
    Integer id;
    String content;
    Integer senderId;
    Integer receiverId;
    String sendedAt;

    public MessageResponseDto(Integer id, String content, Integer senderId, Integer receiverId, Date sendedAt) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.sendedAt = sendedAt.toString();
    }

    public MessageResponseDto(String content) {
        this.content = content;
    }
}
