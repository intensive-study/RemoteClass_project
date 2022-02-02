package org.server.remoteclass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.server.remoteclass.entity.User;
import org.server.remoteclass.entity.UserRole;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long userId;
    private String email;
    private String name;
    private String password;
    private UserRole userRole;
    private Timestamp registerDate;
    private Set<AuthorityDto> authorityDtoSet;

    public static UserDto from(User user){
        if(user == null) return null;
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .registerDate(user.getRegisterDate())
                .userRole(user.getUserRole())
                .build();
    }
}
