package org.app.note.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ParamDto {
    private List<String> tags;
    private List<Long> owner;
    private Instant start;
    private Instant end;
}