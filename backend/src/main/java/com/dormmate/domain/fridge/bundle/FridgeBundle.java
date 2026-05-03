package com.dormmate.domain.fridge.bundle;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fridge_bundles")
public class FridgeBundle {
    @Id
    private UUID id;
}
