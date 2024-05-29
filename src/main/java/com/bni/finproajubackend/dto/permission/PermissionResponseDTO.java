package com.bni.finproajubackend.dto.permission;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponseDTO {
    private String permissionName, permissionDescription, message;
}
