package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MedicoRepository extends JpaRepository<Medico, Long> { // foi passado quem e o entidade e a tipo da chave primaria

    Page<Medico> findAllByAtivoTrue(Pageable paginacao);
    // ByAtivoTrue define direamtente apenas medicos ativos
}
