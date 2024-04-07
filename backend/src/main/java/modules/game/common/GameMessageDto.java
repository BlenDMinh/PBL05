package modules.game.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameMessageDto {
    String message;
    Object data;

    public GameMessageDto(String message) {
        this.message = message;
    }
}
