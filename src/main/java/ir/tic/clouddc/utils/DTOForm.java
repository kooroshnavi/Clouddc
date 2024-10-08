package ir.tic.clouddc.utils;

import lombok.Data;

@Data
public abstract class DTOForm {

    private String challenge;

    private Integer providedAnswer = null;

    private int index;
}
