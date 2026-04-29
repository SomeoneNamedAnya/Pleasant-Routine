package org.app.sharing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSharingCardRequest {
    private String title;
    private String description;
    private String photoLink;
}