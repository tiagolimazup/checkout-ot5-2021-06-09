package br.com.zup.orange.checkout;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonProperty;

import static br.com.zup.orange.checkout.AnaliseApiClient.ResultadoDaSolicitacao.SEM_RESTRICAO;

@FeignClient(name = "analise-api", url = "http://localhost:9999/api")
interface AnaliseApiClient {

    @PutMapping("/solicitacao")
    ResultadoDaAnalise solicitacao(@RequestBody SolicitacaoDeAnalise solicitacaoDeAnalise);

    class SolicitacaoDeAnalise {

        @JsonProperty
        final String documento;

        @JsonProperty
        final String nome;

        @JsonProperty
        final String idProposta;

        SolicitacaoDeAnalise(String documento, String nome, String idProposta) {
            this.documento = documento;
            this.nome = nome;
            this.idProposta = idProposta;
        }

        static SolicitacaoDeAnalise paraProposta(Proposta proposta) {
            return new SolicitacaoDeAnalise(proposta.getDocumento(), proposta.getNome(), Long.toString(proposta.getId()));
        }
    }

    class ResultadoDaAnalise {

        @JsonProperty
        final String documento;

        @JsonProperty
        final String nome;

        @JsonProperty
        final String idProposta;

        @JsonProperty
        final ResultadoDaSolicitacao resultadoSolicitacao;

        ResultadoDaAnalise(String documento, String nome, String idProposta, ResultadoDaSolicitacao resultadoSolicitacao) {
            this.documento = documento;
            this.nome = nome;
            this.idProposta = idProposta;
            this.resultadoSolicitacao = resultadoSolicitacao;
        }

        StatusProposta statusDaProposta() {
            return resultadoSolicitacao == SEM_RESTRICAO ? StatusProposta.ELEGIVEL : StatusProposta.NAO_ELEGIVEL;
        }
    }

    enum ResultadoDaSolicitacao {
        SEM_RESTRICAO, COM_RESTRICAO
    }
}
