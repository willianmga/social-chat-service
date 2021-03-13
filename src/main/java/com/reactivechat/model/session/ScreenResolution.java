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
public class ScreenResolution {
    
    private final String width;
    private final String height;
    private final String orientation;
    
}
