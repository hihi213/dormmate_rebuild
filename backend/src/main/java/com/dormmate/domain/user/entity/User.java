package com.dormmate.domain.user.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    private UUID id;
    
}
