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
public class RegisterIdAndVerifyCodeDto {
    String registerId;
    String code;

    public RegisterIdAndVerifyCodeDto(ResultSet rs) throws SQLException {
        registerId = rs.getString("id");
        code = rs.getString("verify_code");
    }
}
