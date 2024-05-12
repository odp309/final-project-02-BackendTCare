package com.bni.finalproject01webservice.interfaces;

import com.bni.finalproject01webservice.dto.RoleDTO;
import com.bni.finalproject01webservice.model.Role;

public interface RoleInterface {

    Role createNewRole(RoleDTO roleDTO);
}
