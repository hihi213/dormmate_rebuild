package com.dormmate.domain.fridge.slot;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FridgeSlotAssignment {
    @Id  
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
