package com.nellshark.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRegistrationDTO(
    @Email(message = "Email is not valid")
    String email,

    @Size(min = 8, max = 32, message = "Password should be between 8 and 32 characters in length")
    String password) {

}
