package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    String name;

    String description;

    LocalDateTime createdAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> items;
}
