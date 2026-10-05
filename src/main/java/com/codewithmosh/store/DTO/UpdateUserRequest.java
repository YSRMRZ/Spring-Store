package com.codewithmosh.store.DTO;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
}
