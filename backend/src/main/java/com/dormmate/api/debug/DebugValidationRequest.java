package com.dormmate.api.debug;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DebugValidationRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식이 올바르지 않습니다.")
    private String expiryDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
