package org.example.expert.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Lv.2-8: QueryDSL Config
@Configuration
public class QueryDslConfig {

    // EntityManager : @Entity 객체를 관리하며 조회/수정/저장하는 중요한 기능을 수행
    // @PersistenceContext : entity를 영속성 컨텍스트로 보내며 해당 데이터를 영속화
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
