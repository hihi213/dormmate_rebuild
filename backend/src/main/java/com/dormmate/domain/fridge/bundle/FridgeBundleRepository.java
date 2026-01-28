package com.dormmate.domain.fridge.bundle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FridgeBundleRepository extends JpaRepository<FridgeBundle, UUID> {
}
