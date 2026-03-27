package com.dormmate.domain.room;

import java.util.UUID;

import com.dormmate.domain.fridge.slot.FridgeSlotAssignment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = )
    private FridgeSlotAssignment fridgeSlotAssignment;
}
