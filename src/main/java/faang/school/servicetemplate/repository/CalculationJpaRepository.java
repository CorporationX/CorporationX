package faang.school.servicetemplate.repository;


import java.util.List;

import faang.school.servicetemplate.model.CalculationJpa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculationJpaRepository extends CrudRepository<CalculationJpa, Integer> {
    @Override
    List<CalculationJpa> findAll();
}
