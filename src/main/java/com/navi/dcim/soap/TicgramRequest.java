package com.navi.dcim.soap;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TicgramRequest {

    private String us;

    private String ps;

    private String text;

    private String tell;
}

