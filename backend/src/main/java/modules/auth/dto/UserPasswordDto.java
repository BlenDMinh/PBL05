package modules.auth.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.auth.common.AccountStatus;
import modules.auth.common.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordDto {
    private int id;
    private String displayName;
    private String email;
    private String password;
    private AccountStatus status;
    private boolean online;
    private String avatarUrl;
    private int elo;
    private Role role;

    public UserPasswordDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        displayName = rs.getString("display_name");
        email = rs.getString("email");
        password = rs.getString("password");
        status = AccountStatus.fromInt(rs.getInt("status"));
        online = rs.getBoolean("online");
        avatarUrl = rs.getString("avatar_url");
        elo = rs.getInt("elo");
        role = Role.values()[rs.getInt("role")];
    }
}