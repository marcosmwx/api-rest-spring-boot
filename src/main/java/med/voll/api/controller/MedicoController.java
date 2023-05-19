package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
// Spring vai Carregar na inicializacao do projeto
@RequestMapping("medicos")
// Url do controller
public class MedicoController {
    @Autowired
    private MedicoRepository repository;
    // injecao de dependencia, vai instanciar e passar o atributo para a classe controller
    @PostMapping
    // declaração do verbo http do metodo
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder){
        /* @RequestBody pega o corpo da requisicao do parametro json
         foi criado records para receber todos dados como parametro e neles ja serem criados os metodos necessarios
         @Valid para spring se integrar e executar o bean validation, roda em cascata a validacao dos dto
         caso nao tenha essa anotacao nao ira funcionar corretamente
         */

        var medico = new Medico(dados);
        repository.save(medico);
        // passa os parametros do json da req do construtor da entidade medicos

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        // pega o id e passa como parametro da uri

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
        //201 created - devolve no corpo da resposta os dados do registro e cabecalho
    }
    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"} )    Pageable paginacao){
        // Foi feito um record para receber apenas o necessario das informacoes dos medicos

        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        // converte uma lista de medicos para uma lista de dados da listagem de medicos
        // paginacao foi feita atraves do Pageable passando obj paginacao como parametro do FindAllByAtivoTrue ( metodo criado no repository)
        // Para passar apenas 1 parametro acrescente a url ?size=1
        // Para mostrar em ordem crescente use o parametro na url ?sort=atributo ( passe o nome do atributo da entidade para ordenar)
        // ou pode sobrescrever o @PageableDefault(size = 10, sort = {"nome"}

        return ResponseEntity.ok(page);
        // devolve o codigo 200 e na resposta vem o page
        }
    @PutMapping
    @Transactional
    // automaticamente ja faz a mudanca no banco de dados
    public  ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados){
        var medico = repository.getReferenceById(dados.id());
        // carrega o medico pelo id
        medico.atualizarInformacoes(dados);
        // atualiza as informacoes atraves dos metodos criados e apenas o que estao passados no parametro do record
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
    @DeleteMapping("/{id}")
    // parametro dinamico sendo avisado pelo PathVariable
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        medico.excluir();
        return ResponseEntity.noContent().build();
        // faz com que a requisicao devolva um 204, ok mas sem conteudo e faz isso atraves dessa classe ResponseEntity
    }

    @GetMapping("/{id}")
    // parametro dinamico sendo avisado pelo PathVariable
    @Transactional
    public ResponseEntity detalhar(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
        // Devolve o dto ja utilizado para fazer o detalhamento de um medico
    }

}
