package com.dormmate.domain.fridge.item;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fridge_items")
public class FridgeItem {
    @Id
    private UUID id;
}
