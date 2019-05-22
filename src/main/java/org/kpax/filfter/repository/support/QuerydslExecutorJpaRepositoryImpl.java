package org.kpax.filfter.repository.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.kpax.filfter.model.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuerydslExecutorJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
		implements QuerydslExecutorJpaRepository<T, ID> {

	private static final Logger logger = LoggerFactory.getLogger(QuerydslExecutorJpaRepositoryImpl.class);

	private final JpaEntityInformation<T, ID> entityInformation;
	private final PathBuilder<T> pathBuilder;
	private final EntityManager entityManager;

	public QuerydslExecutorJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		EntityPath<T> path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
		this.pathBuilder = new PathBuilder<>(path.getType(), path.getMetadata());
		Querydsl querydsl = new Querydsl(entityManager, pathBuilder);
		this.entityManager = entityManager;
	}

	@Override
	public Page<T> executeQuery(JPQLQuery<T> query, Pageable pageable, Filter... filters) {
		logger.info("Execute query [{}] with: {} and: {}", query, pageable, filters);
		if (filters != null) {
			QuerydslPredicateBuilder<T> querydslPredicateBuilder = new QuerydslPredicateBuilder<>(this.entityManager,
					this.pathBuilder, this.entityInformation);
			Predicate filterPredicate = querydslPredicateBuilder.toPredicate(filters);
			query = query.where(filterPredicate);
		}
		JPAQuery jpaQuery = (JPAQuery) query;
		applyHints(jpaQuery, false);
		JPAQuery<T> countQuery = jpaQuery.clone(entityManager);
		applyHints(countQuery, true);
		Querydsl querydsl = new Querydsl(entityManager, this.pathBuilder);
		JPQLQuery<T> paginatedQuery = querydsl.applyPagination(pageable, query);
		logger.debug("Paginated query [{}]", paginatedQuery);
		return PageableExecutionUtils.getPage(paginatedQuery.fetch(), pageable, countQuery::fetchCount);
	}

	private Map<String, Object> asMap(boolean forCounts) {
		Map<String, Object> hints = new HashMap<>();
		if (getRepositoryMethodMetadata() != null) {
			if (forCounts) {
				hints.putAll(getRepositoryMethodMetadata().getQueryHintsForCount());
			} else {
				hints.putAll(getRepositoryMethodMetadata().getQueryHints());
			}
			hints.putAll(getFetchGraphs());
		}
		return hints;
	}

	private Map<String, Object> getFetchGraphs() {
		return getRepositoryMethodMetadata().getEntityGraph().map(entityGraph -> Jpa21Utils
				.tryGetFetchGraphHints(entityManager, getEntityGraph(entityGraph), entityInformation.getJavaType()))
				.orElse(Collections.emptyMap());
	}

	private JpaEntityGraph getEntityGraph(EntityGraph entityGraph) {
		String fallbackName = entityInformation.getEntityName() + "." + getRepositoryMethodMetadata().getMethod()
				.getName();
		return new JpaEntityGraph(entityGraph, fallbackName);
	}

	private void applyHints(JPAQuery<T> query, boolean forCounts) {
		for (Map.Entry<String, Object> hint : asMap(forCounts).entrySet()) {
			query.setHint(hint.getKey(), hint.getValue());
		}

	}
}
