package org.app.sharing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResult {
    private Long   id;
    private String name;
    private String surname;
    private String lastName;
    private String photoLink;
    private Long   roomId;
    private String roomNumber;
}
