package com.reactivechat.model.contacs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class Avatar {
    
    private final String description;
    private final String source;
    private final String avatar;
    
}
