package modules.game_chesslib.dto;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleSetDto {
    int id;
    String name;
    JsonObject detail;
    JsonObject description; 
    boolean isPublished;
}
