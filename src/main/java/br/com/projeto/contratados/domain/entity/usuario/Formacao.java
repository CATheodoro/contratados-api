package br.com.projeto.contratados.domain.entity.usuario;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Formacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //Chaves Estrangeiras
    @ManyToOne
    private Usuario usuario;

    private String descricao;
    private Date inicio;
    private Date termino;
}
