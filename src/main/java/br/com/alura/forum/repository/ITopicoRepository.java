package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.modelo.Topico;

public interface ITopicoRepository extends JpaRepository<Topico,Long> {
    List<Topico> findByTitulo(String titulo);
    Page<Topico> findByCursoNome(String nomeCurso, Pageable paginacao);
}
