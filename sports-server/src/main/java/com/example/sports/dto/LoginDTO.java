package com.example.sports.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "昵称不能为空")
    @Length(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
} 