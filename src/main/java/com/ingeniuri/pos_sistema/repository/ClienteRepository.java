package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}