package com.dormmate.domain.fridge.item;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FridgeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID id;
}
