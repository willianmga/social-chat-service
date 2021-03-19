package com.reactivechat.message.message;

import com.reactivechat.session.session.UserDeviceDetails;
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
public class AuthenticateRequest {
    
    private final String username;
    private final String password;
    private final UserDeviceDetails userDeviceDetails;
    
}
