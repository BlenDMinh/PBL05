package modules.admin.dto;

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
public class AdminDto {
    private int id;
    private String displayName;
    private String email;
    private AccountStatus status;
    private boolean online;
    private String avatarUrl;
    private Role role;
}
