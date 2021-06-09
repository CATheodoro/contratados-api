package br.com.projeto.contratados.domain.service;

import br.com.projeto.contratados.config.exception.excecoes.*;
import br.com.projeto.contratados.domain.entity.empresa.AnuncioVaga;
import br.com.projeto.contratados.domain.entity.solicitacao.Solicitacao;
import br.com.projeto.contratados.domain.entity.solicitacao.SolicitacaoEmpresaStatus;
import br.com.projeto.contratados.domain.entity.solicitacao.SolicitacaoUsuarioStatus;
import br.com.projeto.contratados.domain.entity.usuario.Usuario;
import br.com.projeto.contratados.domain.repository.SolicitacaoRepository;
import br.com.projeto.contratados.domain.repository.empresa.AnuncioVagaRepository;
import br.com.projeto.contratados.domain.repository.usuario.UsuarioRepository;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoAtualizarEmpresaRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoEmpresaRequest;
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

    public Solicitacao cadastrar(SolicitacaoRequest form){

        Solicitacao solicitacao = form.converter();

        Optional<AnuncioVaga> anuncioVagaOptional = anuncioVagaRepository.findById(solicitacao.getAnuncioVaga().getId());
        if (anuncioVagaOptional.isEmpty())
            throw new AnuncioVagaNaoEncontradoException("Anúncio de Vaga não encontrado, não foi possível enviar a solicitação");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(solicitacao.getUsuario().getId());
        if (usuarioOptional.isEmpty())
            throw new UsuarioNaoEncontradoException("Usuário não encontrado, não foi possível enviar a solicitação");

        if (solicitacaoRepository.existsByUsuarioId(solicitacao.getUsuario().getId()) && solicitacaoRepository.existsByAnuncioVagaId(solicitacao.getAnuncioVaga().getId()))
            throw new SolicitacaoJaEnviadaException("Usuário já enviou a solicitação");

        return solicitacaoRepository.save(solicitacao);
    }


    public Page<Solicitacao> listar(Pageable paginacao) {
        return solicitacaoRepository.findAll(paginacao);
    }



    public Solicitacao atualizarSolicitacaoEmpresa(Integer id, SolicitacaoAtualizarEmpresaRequest form) throws IOException {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);

        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicitação não encontrada, não foi possível alterar");

        Solicitacao solicitacao = form.atualizar(id, solicitacaoRepository);

        if (solicitacao.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.RECUSADO)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("Não é possível alterar dados, solicitação recusada anteriormente");

        if (solicitacao.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("Não é possível alterar dados, status da solicitação ainda está pendente");

        return solicitacaoRepository.save(solicitacao);
    }



    public Solicitacao solicitacaoEmpresa(Integer id, SolicitacaoEmpresaRequest form) throws IOException {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);

        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicitação não encontrada, não foi possível enviar sua confirmação");

        Solicitacao confirmarStatus = solicitacaoRepository.getOne(id);

        if (confirmarStatus.getSolicitacaoEmpresaStatus() != SolicitacaoEmpresaStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarSolicitacaoEmpresaException("Não é possível alterar dados já cadastrado");

        Solicitacao solicitacao = form.solicitacaoEmpresaRequest(id, solicitacaoRepository);

        return solicitacaoRepository.save(solicitacao);
    }

    public Solicitacao solicitacaoUsuario(Integer id, SolicitacaoUsuarioRequest form) {

        Optional<Solicitacao> optional = solicitacaoRepository.findById(id);

        if (optional.isEmpty())
            throw new SolicitacaoNaoEncontradaException("Solicitação não encontrada, não foi possível confirmar sua solicitação");

        Solicitacao confirmarStatus = solicitacaoRepository.getOne(id);

        if (confirmarStatus.getSolicitacaoUsuarioStatus() == SolicitacaoUsuarioStatus.CANCELADO)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("Não foi possível confirmar solicitação, proposta cancelada anteriormenente");

        if (confirmarStatus.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.RECUSADO)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("Não foi possível confirmar solicitação, empresa não aceitou sua solicitação");

        if (confirmarStatus.getSolicitacaoEmpresaStatus() == SolicitacaoEmpresaStatus.PENDENTE)
            throw new NaoFoiPossivelAtualizarConfirmacaoUsuarioException("Não foi possível confirmar solicitação, empresa ainda não checou sua solicitação");

        Solicitacao solicitacao = form.solicitacaoUsuarioRequest(id, solicitacaoRepository);

        return solicitacaoRepository.save(solicitacao);

    }
}
