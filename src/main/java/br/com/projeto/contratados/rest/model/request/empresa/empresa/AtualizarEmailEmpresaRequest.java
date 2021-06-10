package br.com.projeto.contratados.rest.model.request.empresa.empresa;

import br.com.projeto.contratados.domain.entity.empresa.Empresa;
import br.com.projeto.contratados.domain.repository.empresa.EmpresaRepository;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AtualizarEmailEmpresaRequest {
    @NotNull @NotEmpty @Email
    private String email;

    public Empresa atualizarSenhaEmpresaRequest(Integer id, EmpresaRepository empresaRepository){
        var empresa = empresaRepository.getOne(id);

        empresa.setEmail(this.email);

        return empresa;
    }
}
