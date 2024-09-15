package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleInventoryRepository extends JpaRepository<ModuleInventory, Integer> {

    @Query("select m from ModuleInventory m where m.available > 0")
    List<ModuleInventory> getAvailableList();
}
