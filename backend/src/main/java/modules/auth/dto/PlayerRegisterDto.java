package modules.auth.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRegisterDto {
    String displayName;
    String email;
    String password;

    public PlayerRegisterDto(ResultSet rs) throws SQLException {
        displayName = rs.getString("display_name");
        email = rs.getString("email");
        password = rs.getString("password");
    }
}
