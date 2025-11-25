package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@Data
@Entity
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    Booking booking;

    String text;
}
