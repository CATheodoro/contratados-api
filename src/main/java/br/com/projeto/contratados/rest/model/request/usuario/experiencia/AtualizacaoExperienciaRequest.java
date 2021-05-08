package br.com.projeto.contratados.rest.model.request.usuario.experiencia;

import br.com.projeto.contratados.domain.entity.usuario.Experiencia;
import br.com.projeto.contratados.domain.repository.usuario.ExperienciaRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Builder
public class AtualizacaoExperienciaRequest {

    private String descricao;
    private Date inicio;
    private Date termino;

    public Experiencia atualizacaoExperienciaForm(Integer id, ExperienciaRepository experienciaRepository) {
        Experiencia experiencia = experienciaRepository.getOne(id);

        if (this.descricao != null && !this.descricao.isEmpty())
            experiencia.setDescricao(this.descricao);
        if (this.inicio != null)
            experiencia.setInicio(this.inicio);
        if (this.termino != null)
            experiencia.setTermino(this.termino);

        return experiencia;
    }
}
