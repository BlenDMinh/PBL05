package common.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessageDto {
    String message;
    Object data;

    public SocketMessageDto(String message) {
        this.message = message;
    }
}
