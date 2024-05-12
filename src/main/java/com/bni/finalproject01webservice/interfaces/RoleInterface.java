package com.bni.finalproject01webservice.interfaces;

import com.bni.finalproject01webservice.dto.RoleRequestDTO;
import com.bni.finalproject01webservice.model.Role;

public interface RoleInterface {

    Role createNewRole(RoleRequestDTO request);
}
