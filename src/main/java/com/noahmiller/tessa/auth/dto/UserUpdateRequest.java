// src/main/java/com/noahmiller/tessa/auth/dto/UserUpdateRequest.java
package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.*; // 导入所有必要的校验注解
import lombok.Data; // 引入 Lombok 的 @Data 注解，自动生成 getter/setter/toString/equals/hashCode

/**
 * 用户信息更新请求数据传输对象 (DTO)。
 * 用于接收前端发送的用户更新请求数据。
 *
 * 核心原则：
 * - DTO 仅作为数据载体，不包含复杂的业务逻辑。
 * - 复杂的校验规则（如用户名长度/模式、邮箱格式、密码强度等）将全部推迟到 Service 层处理。
 * - 仅保留必要的非空或非空白校验，以确保基本的数据完整性。
 */
@Data // Lombok 注解，自动生成所有 getter/setter/toString/equals/hashCode 方法
public class UserUpdateRequest {

    // 通常用于根据 ID 查找用户进行更新。
    // 如果是通过其他唯一标识（如邮箱）更新，此字段可移除或仅用于特定场景。
    private Long id; // 使用 Long 类型与数据库 ID 匹配

    // 用户名：不再包含复杂的长度或模式校验，这些将在 Service 层进行。
    private String username;

    // 密码：可选，如果需要更新密码。
    // 其强度和格式校验将在 Service 层进行。
    private String password;

    // 邮箱：仅保留非空和非空白约束，具体的邮箱格式校验将在 Service 层进行。
    // @Email 校验注解也将移至 Service 层
    @NotNull(message = "邮箱不能为空") // 确保字段不为 null
    @NotBlank(message = "邮箱不能为空白") // 确保字段不为空字符串或只包含空白字符
    private String email; // 邮箱仍然是重要的更新标识

    // 手机号：模式校验移至 Service 层。
    private String phoneNumber;

    // 生日：日期格式校验移至 Service 层。
    private String birthday; // 假设仍然是 String，Service 层转换为 LocalDate

    // 性别：枚举值校验移至 Service 层。
    private String gender;

    // 省份/州：无特殊校验。
    private String stateProvince;

    // 城市：无特殊校验。
    private String city;
}