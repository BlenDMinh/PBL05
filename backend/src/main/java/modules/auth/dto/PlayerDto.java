package modules.auth.dto;

import common.AccountStatus;
import common.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private int id;
    private String displayName;
    private String email;
    private AccountStatus status;
    private boolean online;
    private String avatarUrl;
    private int elo;
    private Role role;
}
