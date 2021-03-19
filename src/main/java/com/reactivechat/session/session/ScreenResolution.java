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
public class ScreenResolution {
    
    private final String width;
    private final String height;
    private final String orientation;
    
    @JsonCreator
    @BsonCreator
    public ScreenResolution(@BsonProperty("width") @JsonProperty("width") final String width,
                            @BsonProperty("height") @JsonProperty("height") final String height,
                            @BsonProperty("orientation") @JsonProperty("orientation") final String orientation) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
    }
    
}
