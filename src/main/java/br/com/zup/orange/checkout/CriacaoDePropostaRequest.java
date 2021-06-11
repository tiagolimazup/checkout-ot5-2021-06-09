package br.com.zup.orange.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;

class CriacaoDePropostaRequest {

    @JsonProperty
    final String documento;

    @JsonProperty
    final String nome;

    @JsonProperty
    final String email;

    CriacaoDePropostaRequest(String documento, String nome, String email) {
        this.documento = documento;
        this.nome = nome;
        this.email = email;
    }

    Proposta toModel() {
        return new Proposta(documento, nome, email);
    }
}
