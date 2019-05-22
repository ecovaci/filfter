package org.kpax.filfter;

import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kpax.filfter.entity.*;
import org.kpax.filfter.model.Filter;
import org.kpax.filfter.model.FilterType;
import org.kpax.filfter.repository.ChildRepository;
import org.kpax.filfter.repository.ParentRepository;
import org.kpax.filfter.repository.SubchildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FilfterApplicationTest.class)
public class CoreTests {

	@Autowired
	ParentRepository parentRepository;

	@Autowired
	ChildRepository childRepository;

	@Autowired
	SubchildRepository subchildRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	public void testIntDepthZero() {
		Filter filter = new Filter("intField", FilterType.equals, 1);
		JPAQuery jpaQuery = new JPAQuery(entityManager);
		JPAQueryBase from = jpaQuery.from(QParent.parent);
		Page<Parent> page = parentRepository.executeQuery(from, PageRequest.of(0, 10), filter);
		System.out.println(page.getContent());
		Assert.assertTrue("page.getContent().size() == 1", page.getContent().size() == 1);
		Assert.assertTrue("intField == 1", page.getContent().get(0).getIntField() == 1);
	}

	@Test
	public void testDoubleDepthZero() {
		Filter filter = new Filter("dblField", FilterType.equals, 100.5);
		JPAQuery jpaQuery = new JPAQuery(entityManager);
		JPAQueryBase from = jpaQuery.from(QParent.parent);
		Page<Parent> page = parentRepository.executeQuery(from, PageRequest.of(0, 10), filter);
		System.out.println(page.getContent());
		Assert.assertTrue("page.getContent().size() == 1", page.getContent().size() == 1);
		Assert.assertTrue("dblField == 100.5", page.getContent().get(0).getDblField() == 100.5);
	}

	@Test
	public void testBigDecimalDepthZero() {
		Filter filter = new Filter("bigdField", FilterType.equals, BigDecimal.ONE);
		JPAQuery jpaQuery = new JPAQuery(entityManager);
		JPAQueryBase from = jpaQuery.from(QParent.parent);
		Page<Parent> page = parentRepository.executeQuery(from, PageRequest.of(0, 10), filter);
		System.out.println(page.getContent());
		Assert.assertTrue("page.getContent().size() == 1", page.getContent().size() == 1);
		Assert.assertTrue("bigdField == 1", page.getContent().get(0).getBigdField().compareTo(BigDecimal.ONE) == 0);
	}

	@Test
	public void testBigDecimalDepthTwo() {
		Filter filter = new Filter("childParent.parent.bigdField", FilterType.equals, BigDecimal.ONE);
		JPAQuery jpaQuery = new JPAQuery(entityManager);
		JPAQueryBase from = jpaQuery.from(QSubchild.subchild);
		Page<Subchild> page = subchildRepository.executeQuery(from, PageRequest.of(0, 10), filter);
		System.out.println(page.getContent());
		Assert.assertTrue("page.getContent().size() == 1", page.getContent().size() == 1);
	}

}
