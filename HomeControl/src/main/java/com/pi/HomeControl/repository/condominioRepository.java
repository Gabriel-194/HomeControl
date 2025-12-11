package com.pi.HomeControl.repository;

import com.pi.HomeControl.model.condominio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface condominioRepository extends JpaRepository<condominio, Long> {
    Optional<condominio> findByCodigoAcesso(String codigoAcesso);

    boolean existsBySchemaName(String SchemaName);
}