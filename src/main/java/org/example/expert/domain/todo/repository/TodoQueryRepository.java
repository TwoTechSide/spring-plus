package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

// Lv.2-8, Lv.3-10: QueryDSL
public interface TodoQueryRepository {

    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    Page<TodoSearchResponse> searchTodoPage(Pageable pageable, String title, LocalDate createdAt, String nickname);
}
