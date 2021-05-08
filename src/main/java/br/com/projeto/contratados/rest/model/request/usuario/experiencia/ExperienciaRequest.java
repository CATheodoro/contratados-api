package br.com.projeto.contratados.rest.model.request.usuario.experiencia;

import br.com.projeto.contratados.domain.entity.usuario.Experiencia;
import br.com.projeto.contratados.domain.entity.usuario.Usuario;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Builder
public class ExperienciaRequest {

    @NotEmpty @NotNull
    private String descricao;
    @NotNull
    private Date inicio;
    @NotNull
    private Date termino;
    @NotNull
    private Usuario usuario;

    public Experiencia converte() {

        return Experiencia.builder()
                .descricao(this.descricao)
                .inicio(this.inicio)
                .termino(this.termino)
                .usuario(this.usuario)
                .build();
    }

}
