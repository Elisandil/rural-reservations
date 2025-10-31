package com.aogdev.rural.application.port.out;

public interface HashPasswordPort {
    String hash(String plainPassword);
    Boolean verify(String plainPassword, String hashedPassword);
}
