package ch.moneyflow.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {
        @Id
        @Column(name = "mail", nullable = false, unique = true)
        private String mail;

        @Column(name = "pseudo", nullable = false, unique = true, length = 50)
        private String pseudo;

        @Column(name = "passwordhash", nullable = false)
        private String passwordHash;

        @Column(name = "createat", nullable = false)
        private LocalDate createAt = LocalDate.now();
}
