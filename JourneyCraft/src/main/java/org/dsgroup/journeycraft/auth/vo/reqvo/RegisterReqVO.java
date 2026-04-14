package org.dsgroup.journeycraft.auth.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求参数。
 */
@Data
public class RegisterReqVO {

    @NotBlank(message = "username不能为空")
    private String username;

    @NotBlank(message = "password不能为空")
    private String password;

    private String nickname;

    private String phone;

    private String email;
}
