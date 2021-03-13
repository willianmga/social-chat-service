package com.reactivechat.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDeviceDetails {
    
    private final String userIp;
    private final String userAgent;
    private final ScreenResolution screenResolution;
    
}
