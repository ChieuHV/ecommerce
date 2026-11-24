package com.ecommerce.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEventRequest {

    @NotBlank(message = "Event ID is required")
    private String eventId;

    @NotBlank(message = "Event type is required")
    private String eventType;
}