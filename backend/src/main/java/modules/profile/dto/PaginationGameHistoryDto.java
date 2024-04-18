package modules.profile.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationGameHistoryDto {
    List<GameHistoryDto> games;
    int totalPages;
    int totalElements;
}
