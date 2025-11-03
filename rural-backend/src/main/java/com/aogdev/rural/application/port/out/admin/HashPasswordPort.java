package com.aogdev.rural.application.port.out.admin;

public interface HashPasswordPort {
    String hash(String plainPassword);
    boolean verify(String plainPassword, String hashedPassword);
}
