package com.dormmate.domain.fridge.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FridgeSlotRepository extends JpaRepository<FridgeSlot, UUID> {
}
