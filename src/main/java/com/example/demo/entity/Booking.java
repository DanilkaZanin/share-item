package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сущность будет описывать транзакцию (передачу) предмета на определённое время
 */
@Entity
@Table(name = "bookings")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime createdAt;

    LocalDateTime verifiedAt; // когда владелец предмета подтверждает или опровергает статус бронирования

    LocalDate startDate;

    LocalDate endDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    User booker;

    @Enumerated(EnumType.STRING)
    Status status;
}
