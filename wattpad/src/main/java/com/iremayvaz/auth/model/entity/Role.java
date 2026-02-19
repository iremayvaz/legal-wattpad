package com.iremayvaz.auth.model.entity;

import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.auth.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// auth/model/Role.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "roles")
public class Role  extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    private RoleName name; // "AUTHOR", "READER", "MODERATOR", "ADMIN"
}

