package org.kpax.filfter.repository.support;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.kpax.filfter.model.Filter;
import org.kpax.filfter.model.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class QuerydslPredicateBuilder<T> {

	private static final Logger logger = LoggerFactory.getLogger(QuerydslPredicateBuilder.class);
	private JpaEntityInformation<T, ?> entityInformation;
	private EntityManager entityManager;
	private PathBuilder<T> pathBuilder;

	public QuerydslPredicateBuilder(EntityManager entityManager, PathBuilder<T> pathBuilder,
			JpaEntityInformation<T, ?> entityInformation) {
		this.entityManager = entityManager;
		this.pathBuilder = pathBuilder;
		this.entityInformation = entityInformation;
	}

	public Predicate toPredicate(Filter... filters) {
		Assert.notNull(filters, "filters cannot be null");
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		for (Filter filter : filters) {
			booleanBuilder.and(applyFilter(filter));
		}
		return booleanBuilder;
	}

	private Predicate applyFilter(Filter filter) {
		logger.debug("Apply filter {}", filter);
		Class<?> javaType = findJavaType(filter.getPath());
		logger.debug("Java type {}", javaType);
		if (isNumeric(javaType)) {
			return buildNumericPredicate(filter);
		} else if (Date.class.isAssignableFrom(javaType)) {
			return buildDatePredicate(filter);
		} else if (String.class.isAssignableFrom(javaType)) {// FIXME More string types
			return buildStringPredicate(filter);
		} else {
			throw new IllegalArgumentException("There is no method handler for this Java type: " + javaType);
		}
	}

	private Class<?> findJavaType(String path) {
		logger.debug("Find Java type for path [{}]", path);
		String element = null;
		Class<?> javaType = this.entityInformation.getJavaType();
		Iterator<String> iterator = Arrays.asList(path.split("\\.")).iterator();
		while (iterator.hasNext()) {
			element = iterator.next();
			if (iterator.hasNext()) {// Still at entity level, get the new Java type
				EntityType<?> entityType = this.entityManager.getMetamodel().entity(javaType);
				javaType = entityType.getSingularAttribute(element).getJavaType();
			}
		}
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(javaType);
		} catch (IntrospectionException e) {
			throw new RuntimeException("Error on introspecting entity class: " + javaType, e);
		}
		final String lastElement = Optional.ofNullable(element).orElseThrow(() -> new IllegalArgumentException("Invalid path [" + path + "]"));
		final Class<?> entityClass = javaType;
		PropertyDescriptor propertyDescriptor = Stream.of(beanInfo.getPropertyDescriptors())
				.filter(descriptor -> descriptor.getName().equals(lastElement)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("There is no field with name [" + lastElement + "] on entity class [" + entityClass));
		return propertyDescriptor.getPropertyType();

	}

	private boolean isNumeric(Class<?> javaType) {
		if (javaType.isPrimitive()) {
			return javaType == Byte.TYPE || javaType == Short.TYPE || javaType == Integer.TYPE || javaType == Float.TYPE || javaType == Double.TYPE;
		}
		return Number.class.isAssignableFrom(javaType);
	}

	private BooleanExpression buildNumericPredicate(Filter filter) {
		BigDecimal attributeValue = new BigDecimal(filter.getValue().toString());
		NumberPath<BigDecimal> numberPath = pathBuilder.getNumber(filter.getPath(), BigDecimal.class);
		if (filter.getType() == FilterType.equals) {
			return numberPath.eq(attributeValue);
		} else if (filter.getType() == FilterType.gt) {
			return numberPath.gt(attributeValue);
		} else if (filter.getType() == FilterType.lt) {
			return numberPath.lt(attributeValue);
		} else if (filter.getType() == FilterType.gte) {
			return numberPath.goe(attributeValue);
		} else if (filter.getType() == FilterType.lte) {
			return numberPath.loe(attributeValue);
		} else if (filter.getType() == FilterType.ne) {
			return numberPath.ne(attributeValue);
		} else {
			throw new IllegalArgumentException(
					"Invalid filter, type [" + filter.getType() + "] not allowed for numeric type");
		}
	}

	private BooleanExpression buildDatePredicate(Filter filter) {
		Date attributeValue = new Date(Long.parseLong(filter.getValue().toString()));
		DatePath<Date> datePath = pathBuilder.getDate(filter.getPath(), Date.class);
		if (filter.getType() == FilterType.equals) {
			return datePath.eq(attributeValue);
		} else if (filter.getType() == FilterType.gt) {
			return datePath.after(attributeValue);
		} else if (filter.getType() == FilterType.lt) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(attributeValue);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			return datePath.before(calendar.getTime());
		} else if (filter.getType() == FilterType.gte) {
			return datePath.goe(attributeValue);
		} else if (filter.getType() == FilterType.lte) {
			return datePath.loe(attributeValue);
		} else if (filter.getType() == FilterType.gten) {
			return datePath.goe(attributeValue).or(datePath.isNull());
		} else if (filter.getType() == FilterType.lten) {
			return datePath.loe(attributeValue).or(datePath.isNull());
		} else if (filter.getType() == FilterType.ne) {
			return datePath.ne(attributeValue);
		} else {
			throw new IllegalArgumentException(
					"Invalid filter, type [" + filter.getType() + "] not allowed for date type");
		}
	}

	private <T> BooleanExpression buildStringPredicate(Filter filter) {
		StringPath stringPath = pathBuilder.getString(filter.getPath());
		if (filter.getType() == FilterType.equals) {
			return stringPath.equalsIgnoreCase(filter.getValue().toString());
		} else if (filter.getType() == FilterType.ne) {
			return stringPath.notEqualsIgnoreCase(filter.getValue().toString());
		} else if (filter.getType() == FilterType.contains) {
			return stringPath.upper().like("%" + filter.getValue().toString().toUpperCase() + "%");
		} else {
			throw new IllegalArgumentException(
					"Invalid filter, type [" + filter.getType() + "] not allowed for string type");
		}
	}

}
