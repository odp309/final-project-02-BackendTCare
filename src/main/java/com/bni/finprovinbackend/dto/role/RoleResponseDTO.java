package com.bni.finprovinbackend.dto.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponseDTO {
    private String message, roleName, roleDescription;
}
