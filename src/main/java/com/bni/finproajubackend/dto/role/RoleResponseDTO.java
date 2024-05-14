package com.bni.finproajubackend.dto.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponseDTO {
    private String roleName, roleDescription;
}
