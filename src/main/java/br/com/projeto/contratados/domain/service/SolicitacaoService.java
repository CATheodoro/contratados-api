package br.com.projeto.contratados.domain.service;

import br.com.projeto.contratados.config.exception.excecoes.*;
import br.com.projeto.contratados.config.security.TokenService;
import br.com.projeto.contratados.domain.entity.empresa.AnuncioVaga;
import br.com.projeto.contratados.domain.entity.empresa.Empresa;
import br.com.projeto.contratados.domain.entity.solicitacao.Solicitacao;
import br.com.projeto.contratados.domain.entity.solicitacao.SolicitacaoEmpresaStatus;
import br.com.projeto.contratados.domain.entity.solicitacao.SolicitacaoUsuarioStatus;
import br.com.projeto.contratados.domain.entity.usuario.Usuario;
import br.com.projeto.contratados.domain.repository.SolicitacaoRepository;
import br.com.projeto.contratados.domain.repository.empresa.AnuncioVagaRepository;
import br.com.projeto.contratados.domain.repository.empresa.EmpresaRepository;
import br.com.projeto.contratados.domain.repository.usuario.UsuarioRepository;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoAtualizarEmpresaRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoEmpresaStatusRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnuncioVagaRepository anuncioVagaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;


    @Autowired
    private TokenService tokenService;

    private Long getIdUsuario() {
        return tokenService.getAuthenticatedUsuario();
    }

    private Long getIdEmpresa() {
        return tokenService.getAuthenticatedEmpresaSemValidacao();
    }

    private Long getIdEmpresaSemValidacao() {
        return tokenService.getAuthenticatedEmpresaSemValidacao();
    }

    public Solicitacao cadastrar(SolicitacaoRequest form) {

        Optional<AnuncioVaga> anuncioVagaOptional = anuncioVagaRepository.findById(form.getAnuncioVagaId());
        if (anuncioVagaOptional.isEmpty())
            throw new AnuncioVagaNaoEncontradoException("An??ncio de Vaga n??o encontrado, n??o foi poss??vel enviar a solicita????o");

        if (solicitacaoRepository.existsByAnuncioVagaIdAndUsuarioId(form.getAnuncioVagaId(), getIdUsuario()))
            throw new SolicitacaoJaEnviadaException("Usu??rio j?? enviou a solicita????o");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(getIdUsuario());
        if (usuarioOptional.isEmpty())
            throw new UsuarioNaoEncontradoException("Usu??rio n??o encontrado, n??o foi poss??vel enviar a solicita????o");

        var solicitacao = form.converter(usuarioOptional.get(), anuncioVagaOptional.get());

        return solicitacaoRepository.save(solicitacao);
    }


    public Page<Solicitacao> listar(SolicitacaoEmpresaStatus status, Long anuncioId, Pageable paginacao) {
        Optional<Empresa> empresaOptional = empresaRepository.findById(getIdEmpresaSemValidacao());
        if(empresaOptional.isPresent()){
            if(status !=null)
                return solicitacaoRepository.findByAnuncioVagaEmpresaIdAndSolicitacaoEmpresaStatus(getIdEmpresaSemValidacao(), status, paginacao);
            if(anuncioId !=null)
                return solicitacaoRepository.findByAnuncioVagaEmpresaIdAndAnuncioVagaId(getIdEmpresaSemValidacao(), anuncioId, paginacao);
            return solicitacaoRepository.findByAnuncioVagaEmpresaId(getIdEmpresaSemValidacao(), paginacao);
        }


        Optional<Usuario> usuarioOptional = usuarioRepository.findById(getIdUsuario());
        if(usuarioOptional.isPresent()){
            if(status !=null)
                return solicitacaoRepository.findAllByUsuarioIdAndSolicitacaoEmpresaStatus(getIdUsuario(), status, paginacao);

            return solicitacaoRepository.findAllByUsuarioId(getIdUsuario(), paginacao);
        }
        throw new SolicitacaoNaoEncontradaException("Solicita????o n??o encontrada");
    }

    public Solicitacao getSolicitacao(Long id) {
        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);
        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicita????o n??o encontrada");

        return optional.get();
    }


    public Solicitacao atualizarSolicitacaoEmpresa(Long id, SolicitacaoAtualizarEmpresaRequest form) throws IOException {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);
        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicita????o n??o encontrada, n??o foi poss??vel alterar");

        if (!optional.get().getAnuncioVaga().getEmpresa().getId().equals(getIdEmpresa()))
            throw new EmpresaNaoEncontradaException("Solicita????o n??o pode ser atualizada, entre com o perfil de empresa");

        var solicitacao = form.atualizar(optional.get());

        if (solicitacao.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.RECUSADO)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("N??o ?? poss??vel alterar dados, solicita????o recusada anteriormente");

        if (solicitacao.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("N??o ?? poss??vel alterar dados, status da solicita????o ainda est?? pendente");

        return solicitacaoRepository.save(solicitacao);
    }


    public Solicitacao solicitacaoEmpresa(Long id, SolicitacaoEmpresaStatusRequest form) throws IOException {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);

        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicita????o n??o encontrada, n??o foi poss??vel enviar sua confirma????o");

        if (!optional.get().getAnuncioVaga().getEmpresa().getId().equals(getIdEmpresa()))
            throw new EmpresaNaoEncontradaException("Solicita????o n??o pode ser confirmada, entre com o perfil de empresa");

        var confirmarStatus = solicitacaoRepository.getOne(id);

        if (confirmarStatus.getSolicitacaoEmpresaStatus() != SolicitacaoEmpresaStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("N??o ?? poss??vel alterar dados j?? cadastrado");

        var solicitacao = form.solicitacaoEmpresaRequest(optional.get());

        return solicitacaoRepository.save(solicitacao);
    }

    public Solicitacao solicitacaoUsuario(Long id, SolicitacaoUsuarioRequest form) {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);

        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicita????o n??o encontrada, n??o foi poss??vel confirmar sua solicita????o");

        if (!optional.get().getUsuario().getId().equals(getIdUsuario()))
            throw new UsuarioNaoEncontradoException("N??o foi poss??vel confirmar solicita????o, entre com uma conta de usu??rio");

        var confirmarStatus = solicitacaoRepository.getOne(id);

        if (confirmarStatus.getSolicitacaoUsuarioStatus() == SolicitacaoUsuarioStatus.CANCELADO)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("N??o foi poss??vel confirmar solicita????o, proposta cancelada anteriormenente");

        if (confirmarStatus.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.RECUSADO)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("N??o foi poss??vel confirmar solicita????o, empresa n??o aceitou sua solicita????o");

        if (confirmarStatus.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.PENDENTE && confirmarStatus.getSolicitacaoUsuarioStatus() != SolicitacaoUsuarioStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("N??o foi poss??vel confirmar solicita????o, empresa ainda n??o checou sua solicita????o");

        var solicitacao = form.solicitacaoUsuarioRequest(optional.get());

        return solicitacaoRepository.save(solicitacao);

    }
}
