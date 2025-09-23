package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Lv.2-8: QueryDSL : TodoQueryRepository 구현 -> JPAQueryFactory를 이용해 동적 쿼리 작성
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        Todo qtodo = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(qtodo);
    }

    // Lv.3-10: Query DSL로 Page Search
    @Override
    public Page<TodoSearchResponse> searchTodoPage(Pageable pageable, String title, LocalDate createdAt, String nickname) {

        BooleanExpression titleContains = title != null ? todo.title.contains(title) : null;
        BooleanExpression createdAfter = createdAt != null ? todo.createdAt.goe(createdAt.atStartOfDay()) : null;
        BooleanExpression nicknameContains = nickname != null ? todo.user.nickname.contains(nickname) : null;

        List<TodoSearchResponse> todos = queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.todo.eq(todo)),
                        JPAExpressions.select(manager.count())
                                .from(manager)
                                .where(manager.todo.eq(todo))
                ))
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(
                        titleContains,
                        createdAfter,
                        nicknameContains
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(todo.count())
                        .from(todo)
                        .leftJoin(todo.user, user)
                        .where(
                                titleContains,
                                createdAfter,
                                nicknameContains
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(todos, pageable, total);
    }
}
