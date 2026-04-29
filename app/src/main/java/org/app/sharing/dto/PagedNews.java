package org.app.sharing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.note.dto.RoomNoteDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedNews {
    private List<RoomNoteDto> content;
    private int  page;
    private int  size;
    private long totalElements;
    private int  totalPages;
    private boolean last;
}
