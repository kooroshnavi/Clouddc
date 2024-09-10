package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleCategoryRepository extends JpaRepository<ModuleCategory, Integer> {
}
