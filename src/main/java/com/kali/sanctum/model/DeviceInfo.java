package com.kali.sanctum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class DeviceInfo {
    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;
}
