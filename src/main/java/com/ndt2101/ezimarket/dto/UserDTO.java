package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.model.RoleEntity;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO extends BaseDTO {
    @Size(min = 6, max = 15, message = "first name out of length")
    private String firstName;
    @Size(min = 6, max = 15, message = "last name out of length")
    private String lastName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//    private Date dateOfBirth;
    private String phone;
    private RoleEntity role;
    @Email(message = "wrong format email")
    private String emailAddress;
    @Size(min = 6, max = 20, message = "login name out of length")
    private String loginName;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "password is invalid")
    private String password;

    private String avatarUrl;

    public UserDTO(Long id) {
        super(id);
    }
}
