package br.com.projeto.contratados.domain.entity.empresa;

import br.com.projeto.contratados.domain.entity.Endereco;
import br.com.projeto.contratados.domain.entity.solicitacao.Solicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioVaga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //Chaves Estrangeiras
    @OneToMany(mappedBy = "anuncioVaga")
    private List<SetorCargo> setorCargo = new ArrayList<>();

    @OneToMany(mappedBy = "anuncioVaga")
    private List<Solicitacao> solicitacao = new ArrayList<>();

    @ManyToOne
    private Empresa empresa;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false)
    private Time cargaHoraria;
    @Column(length = 255, nullable = false)
    private String requisitos;
    private Float salario;
    private boolean statusAnuncio;

    private LocalDateTime dataPostagem;


}
