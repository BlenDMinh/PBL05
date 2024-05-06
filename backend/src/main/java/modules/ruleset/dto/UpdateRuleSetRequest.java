package modules.ruleset.dto;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRuleSetRequest {
    String name;
    JsonObject detail;
    JsonObject description;
    boolean published;
}
