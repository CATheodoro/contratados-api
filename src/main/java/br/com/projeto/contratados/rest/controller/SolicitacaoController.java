package br.com.projeto.contratados.rest.controller;

import br.com.projeto.contratados.domain.service.SolicitacaoService;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoEmpresaRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoUsuarioRequest;
import br.com.projeto.contratados.rest.model.response.SolicitacaoResponse;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoAtualizarEmpresaRequest;
import br.com.projeto.contratados.rest.model.request.solicitacao.SolicitacaoRequest;
import br.com.projeto.contratados.domain.entity.solicitacao.Solicitacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/solicitacao")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping
    public ResponseEntity<SolicitacaoResponse> cadastrar(@RequestBody @Valid SolicitacaoRequest form, UriComponentsBuilder uriComponentsBuilder){
        Solicitacao solicitacao = solicitacaoService.cadastrar(form);

        URI uri = uriComponentsBuilder.path("/solicitacao/{id}").buildAndExpand(solicitacao.getId()).toUri();
        return ResponseEntity.created(uri).body(new SolicitacaoResponse(solicitacao));
    }

    @GetMapping
    public ResponseEntity<Page<SolicitacaoResponse>> listar(@PageableDefault(size = 10,page = 0,sort = "dataCriacaoSolicitacao", direction = Sort.Direction.DESC)Pageable paginacao){
        Page<Solicitacao> solicitacao = solicitacaoService.listar(paginacao);
        return ResponseEntity.ok().body(SolicitacaoResponse.converter(solicitacao));
    }

    @PutMapping("/empresaatualizar/{id}")
    private ResponseEntity<SolicitacaoResponse> atualizarSolicitacaoEmpresa(@PathVariable Integer id, @RequestBody @Valid SolicitacaoAtualizarEmpresaRequest form) throws IOException {

        Solicitacao solicitacao = solicitacaoService.atualizarSolicitacaoEmpresa(id, form);
        return ResponseEntity.ok().body(new SolicitacaoResponse(solicitacao));
    }

    @PutMapping("/empresa/{id}")
    public ResponseEntity<SolicitacaoResponse> solicitacaoEmpresa(@PathVariable Integer id, @RequestBody @Valid SolicitacaoEmpresaRequest form) throws IOException {
        Solicitacao solicitacao = solicitacaoService.solicitacaoEmpresa(id ,form);

        return ResponseEntity.ok().body(new SolicitacaoResponse(solicitacao));
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<SolicitacaoResponse> solicitacaoUsuario(@PathVariable Integer id, @RequestBody @Valid SolicitacaoUsuarioRequest form) throws IOException {
        Solicitacao solicitacao = solicitacaoService.solicitacaoUsuario(id ,form);

        return ResponseEntity.ok().body(new SolicitacaoResponse(solicitacao));
    }
}