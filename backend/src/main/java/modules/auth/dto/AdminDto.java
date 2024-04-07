package modules.auth.dto;

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
public class AdminDto {
    private int id;
    private String displayName;
    private String email;
    private AccountStatus status;
    private boolean online;
    private String avatarUrl;
    private Role role;
}
