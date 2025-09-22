package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

// Lv.2-8: QueryDSL이 작성된 TodoQueryRepository 상속
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoQueryRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // Lv.1-3: (weather, startDate, endDate)이 각각 null 이거나 특정 조건을 만족하는 경우 적용
    @EntityGraph(attributePaths = {"user"})
    @Query("select t from Todo t " +
            "where (:weather is null or t.weather = :weather)" +
            "and (:startDate is null or t.modifiedAt >= :startDate)" +
            "and (:endDate is null or t.modifiedAt <= :endDate) " +
            "order by t.modifiedAt desc")
    Page<Todo> findTodosOrderBySpecAndModifiedAtDesc(Pageable pageable,
                                                     @Param("weather") String weather,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
}
