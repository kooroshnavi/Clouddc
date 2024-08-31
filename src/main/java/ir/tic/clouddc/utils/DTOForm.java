package ir.tic.clouddc.utils;

import lombok.Data;

@Data
public abstract class DTOForm {

    private String challenge;

    private int providedAnswer;

    private int index;
}
