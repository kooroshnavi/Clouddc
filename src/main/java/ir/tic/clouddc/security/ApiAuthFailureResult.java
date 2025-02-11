package ir.tic.clouddc.security;

import ir.tic.clouddc.api.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiAuthFailureResult extends Result {

    private String message;
}
