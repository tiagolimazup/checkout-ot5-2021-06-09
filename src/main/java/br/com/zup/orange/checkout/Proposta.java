package br.com.zup.orange.checkout;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
class Proposta {

    @Id
    @GeneratedValue
    private Long id;

    private String documento;

    private String nome;

    private String email;

    @Enumerated(EnumType.STRING)
    private StatusProposta status;

    @Deprecated
    Proposta() {}

    Proposta(String documento, String nome, String email) {
        this.documento = documento;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    boolean isElegivel() {
        return status == StatusProposta.ELEGIVEL;
    }

    public boolean isNaoElegivel() {
        return status == StatusProposta.NAO_ELEGIVEL;
    }

    void setStatus(StatusProposta status) {
        this.status = status;
    }

}
