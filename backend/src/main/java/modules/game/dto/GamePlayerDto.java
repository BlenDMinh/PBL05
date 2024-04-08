package modules.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerDto {
    int id;
    String displayName;
    String avatarUrl;
    boolean white;
}
