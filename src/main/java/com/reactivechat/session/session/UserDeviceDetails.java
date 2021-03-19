package com.reactivechat.session.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserDeviceDetails {
    
    private final String userIp;
    private final String userAgent;
    private final ScreenResolution screenResolution;
    
    @JsonCreator
    @BsonCreator
    public UserDeviceDetails(@BsonProperty("userIp") @JsonProperty("userIp") final String userIp,
                             @BsonProperty("userAgent") @JsonProperty("userAgent") final String userAgent,
                             @BsonProperty("screenResolution") @JsonProperty("screenResolution") final ScreenResolution screenResolution) {
        
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.screenResolution = screenResolution;
    }
    
}
