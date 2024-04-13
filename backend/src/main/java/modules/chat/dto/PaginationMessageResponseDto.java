package modules.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMessageResponseDto {
    List<MessageResponseDto> messages;
    int totalPages;
    int totalElements;
}
