package com.software_design.horseland.model;

import com.software_design.horseland.util.EncryptDecryptConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_token")
public class AuthToken {

    @Id
    @Convert(converter = EncryptDecryptConverter.class)
    @Column(length = 512)
    private String token;

    private String username;

    private LocalDateTime expiryDate;
}
