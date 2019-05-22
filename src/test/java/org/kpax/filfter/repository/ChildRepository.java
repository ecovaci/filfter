package org.kpax.filfter.repository;

import org.kpax.filfter.entity.Child;
import org.kpax.filfter.entity.Parent;
import org.kpax.filfter.repository.support.QuerydslExecutorJpaRepository;

public interface ChildRepository extends QuerydslExecutorJpaRepository<Child, Long> {
}
