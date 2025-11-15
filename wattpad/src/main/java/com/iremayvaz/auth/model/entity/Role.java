package com.iremayvaz.auth.model.entity;

import jakarta.persistence.*;

// auth/model/Role.java
@Entity
@Table(name = "roles")
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true, length = 32)
    private String code; // "AUTHOR", "READER", "MODERATOR", "ADMIN"
}

