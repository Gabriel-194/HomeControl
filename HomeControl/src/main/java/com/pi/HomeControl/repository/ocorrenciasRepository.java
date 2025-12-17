package com.pi.HomeControl.repository;

import com.pi.HomeControl.model.ocorrencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ocorrenciasRepository extends JpaRepository<ocorrencias, Long> {
    long countByStatus(String status);
    List<ocorrencias> findTop5ByOrderByDataCriacaoDesc();
}
