package ru.practicum.ewm.utility.stats;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
public class EndpointRequestDto {
    @Null
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String app;
    @NotBlank
    @Size(max = 2048)
    private String uri;
    @NotBlank
    @Size(max = 45)
    private String ip;
    @NotBlank
    private String timestamp;
}
