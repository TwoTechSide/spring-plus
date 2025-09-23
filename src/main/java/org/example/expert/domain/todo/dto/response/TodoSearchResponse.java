package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Lv.3-10: Page 반환용 Response Dto
@Getter
@AllArgsConstructor
public class TodoSearchResponse {

    private final String title;
    private final long commentCount;
    private final long managerCount;
}
