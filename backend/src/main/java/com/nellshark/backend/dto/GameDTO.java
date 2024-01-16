package com.nellshark.backend.dto;

public record GameDTO(
        long id,
        String name,
        String description,
        String imageUrl,
        boolean windows,
        boolean mac,
        boolean linux) {
}
