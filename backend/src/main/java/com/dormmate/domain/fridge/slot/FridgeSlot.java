package com.dormmate.domain.fridge.slot;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fridge_slots")
public class FridgeSlot {
    @Id
    private UUID id;
}
