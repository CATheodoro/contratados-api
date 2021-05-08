package br.com.projeto.contratados.domain.repository.empresa;

import br.com.projeto.contratados.domain.entity.empresa.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    Page<Empresa> findByNomeFantasia(String nomeFantasia, Pageable paginacao);

    boolean existsByEmail(String email);
}
