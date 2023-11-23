package project.eepw.softwaretestingcrud.domain.user.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDTO(Long id, String name, String surname, String email) {}
