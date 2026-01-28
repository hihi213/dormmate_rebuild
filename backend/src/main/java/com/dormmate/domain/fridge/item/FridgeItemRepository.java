package com.dormmate.domain.fridge.item;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FridgeItemRepository extends JpaRepository<FridgeItem, UUID> {
}
