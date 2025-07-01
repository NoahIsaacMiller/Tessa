// src/main/java/com/noahmiller/tessa/user/controller/UserController.java
package com.noahmiller.tessa.user.controller;

import com.noahmiller.tessa.core.api.ApiResponse;
import com.noahmiller.tessa.auth.dto.UserResponse;
import com.noahmiller.tessa.auth.dto.UserUpdateRequest;
import com.noahmiller.tessa.user.entity_old.User;
import com.noahmiller.tessa.core.service.UserService;
import jakarta.validation.Valid; // 导入用于 Bean Validation 的 @Valid 注解

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping; // 用于更新资源的 PUT 方法
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // 确保导入 Collectors

import static com.noahmiller.tessa.core.api.ApiResponse.success; // 静态导入统一响应的 success 方法

/**
 * **用户相关的 RESTful API 控制器。**
 * 负责接收 HTTP 请求，将请求委派给 Service 层处理业务逻辑，并返回统一的 API 响应。
 * 此控制器遵循 RESTful 风格和职责分离原则，使得代码结构清晰且易于维护。
 *
 * **命名约定遵循 Saved Info:**
 * - 类名: PascalCase (UserController)
 * - 方法名: camelCase (getUsers, deleteUser, updateUser)
 * - 变量名: camelCase (userService)
 */
@RestController // 标记此类为一个 RESTful Web 服务控制器，并自动将方法返回值序列化为 JSON/XML
@RequestMapping("/api/v1") // 定义所有方法共享的基础请求路径
public class UserController {

    private final UserService userService; // 通过构造器注入 UserService 依赖

    /**
     * **构造器注入 UserService 依赖。**
     * 这种方式推荐用于依赖注入，确保了依赖关系的清晰和测试的便利性。
     * Controller 层仅依赖 Service 接口，不直接操作数据访问层 (Mapper)。
     *
     * @param userService 用户服务实例，由 Spring 容器自动提供。
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * **获取所有用户列表的 API。**
     *
     * 这是一个公共访问接口，允许客户端获取系统中的所有用户概览信息。
     *
     * **HTTP 方法:** GET
     * **请求路径:** `/api/v1/public/users`
     *
     * @return 包含用户响应列表的统一 API 响应。
     * - **成功时:** 返回 HTTP 状态码 200 (OK)，并在 `ApiResponse` 中包含一个 `UserResponse` 列表。
     * - **失败时:** 如果 Service 层内部出现未捕获的异常，将由全局异常处理器统一处理，返回相应的错误响应。
     */
    @GetMapping("/public/users")
    public ApiResponse<List<UserResponse>> getUsers() {
        // 调用 UserService 获取用户实体列表。
        // UserService 负责查询逻辑，并确保返回非 null 的列表。
        List<User> users = userService.getUsers();

        // 将获取到的 User 实体列表通过 Stream API 转换为 UserResponse DTO 列表。
        // 这样做是为了避免直接向前端暴露敏感的数据库实体结构，实现数据隔离和安全。
        return success("查询成功", users.stream()
                .map(UserResponse::fromUser) // 假设 UserResponse 有静态工厂方法 fromUser(User user)
                .collect(Collectors.toList())); // 使用 Collectors.toList() 收集结果
    }

    /**
     * **根据用户 ID 删除用户的 API。**
     *
     * **重要提示:** 这是一个**敏感操作**。该接口的路径 `/admin/users/{userId}` 明确指出其管理属性。
     * 在实际生产环境中，**必须**集成 Spring Security 或其他鉴权框架，
     * 以确保只有具有相应权限（例如，管理员角色）的用户才能访问此端点。
     *
     * **HTTP 方法:** DELETE
     * **请求路径:** `/api/v1/admin/users/{userId}`
     *
     * @param userId 要删除的用户 ID，通过 `@PathVariable` 从 URL 路径中提取。
     * @return 包含被删除用户 ID 的统一 API 响应。
     * - **成功时:** 返回 HTTP 状态码 200 (OK)，并在 `ApiResponse` 中包含被删除用户的 `userId`。
     * - **失败时:**
     * - 如果 `userId` 为 `null` 或小于 1，`UserService` 将抛出 `InvalidUserArgumentException`。
     * - 如果指定 ID 的用户不存在，`UserService` 将抛出 `UserNotFoundException`。
     * **所有这些异常都将由全局异常处理器统一捕获和处理**，并返回相应的错误响应给客户端。
     */
    @DeleteMapping("/admin/users/{userId}")
    public ApiResponse<Long> deleteUser(@PathVariable Long userId) {
        // 业务逻辑（包括参数校验、用户存在性检查和实际删除操作）已完全委托给 UserService 处理。
        // Controller 层只负责调用 Service 方法，并处理其成功返回的情况。
        // 任何由 UserService 抛出的业务异常，都会被 GlobalExceptionHandler 捕获并转换。
        userService.deleteUser(userId);
        return success("用户已注销", userId);
    }

    /**
     * **更新用户信息的 API。**
     *
     * 使用 PUT 方法进行全量更新（通常情况下，如果只更新部分字段，更推荐使用 PATCH 方法）。
     * 此接口应受权限保护。通常，用户只能更新自己的信息，或者管理员可以更新任何用户信息。
     *
     * **HTTP 方法:** PUT
     * **请求路径:** `/api/v1/users/{email}` (假设邮箱是用户的唯一标识符，也可以通过 userId 更新: `/api/v1/users/{userId}`)
     *
     * @param email 更新用户的邮箱，通过 `@PathVariable` 从 URL 路径中提取，用于标识待更新的用户。
     * @param userUpdateRequest 包含更新信息的 DTO，通过 `@RequestBody` 从请求体中获取。
     * `@Valid` 注解将触发 `UserUpdateRequest` 中定义的所有 JSR 303/380 Bean Validation 规则。
     * 如果校验失败，将抛出 `MethodArgumentNotValidException`，由全局异常处理器处理。
     * @return 统一 API 响应，指示用户信息更新是否成功。
     * - **成功时:** 返回 HTTP 状态码 200 (OK)，并在 `ApiResponse` 中包含成功消息。
     * - **失败时:**
     * - 如果 `userUpdateRequest` 的字段不符合校验规则，由 `GlobalExceptionHandler` 处理。
     * - 如果 `email` 对应的用户不存在，`UserService` 将抛出 `UserNotFoundException`。
     * - 如果 `UserUpdateRequest` 中的某些值（如性别）不合法，`UserService` 将抛出 `InvalidUserArgumentException`。
     * **所有这些异常都将由全局异常处理器统一捕获和处理**，并返回相应的错误响应。
     */
    @PutMapping("/users/{email}")
    public ApiResponse<String> updateUser(
            @PathVariable String email,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        // Controller 仅负责接收请求参数并将其传递给 Service 层。
        // 所有的业务逻辑、数据校验、密码哈希和 DTO 到实体的映射都由 UserService 处理。
        userService.updateUserByEmail(email, userUpdateRequest);
        return success("用户信息更新成功!");
    }
}