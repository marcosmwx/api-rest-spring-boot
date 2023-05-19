package med.voll.api.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

// para fazer qualquer operacao na tabela usuarios utilizamos o repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {// foi passado quem e a entidade e o tipo da chave primaria
    UserDetails findByLogin(String login);




}
