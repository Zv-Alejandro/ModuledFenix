package org.ies.fenix.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    @Column(name = "password_hashed")
    private String passwordHashed;

    @Column(unique = true)
    private String username;


    private String bio;


    @OneToMany(mappedBy = "dev")
    private List<Game> developedGames;

    @OneToMany(mappedBy = "client")
    private List<Purchase> purchases;
}