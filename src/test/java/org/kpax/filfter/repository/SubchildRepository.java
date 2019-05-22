package org.kpax.filfter.repository;

import org.kpax.filfter.entity.Parent;
import org.kpax.filfter.entity.Subchild;
import org.kpax.filfter.repository.support.QuerydslExecutorJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface SubchildRepository extends QuerydslExecutorJpaRepository<Subchild, Long> {
	@Query("select subchild from Subchild subchild where subchild.childParent.parent.bigdField = :bigdField")
	Subchild getParentByBigdField (@Param("bigdField")  BigDecimal bigdField) ;
}
