package br.com.zup.orange.checkout;

import br.com.zup.orange.checkout.AnaliseApiClient.ResultadoDaAnalise;
import br.com.zup.orange.checkout.AnaliseApiClient.SolicitacaoDeAnalise;
import feign.FeignException.FeignClientException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/proposta")
class CadastroDePropostaResource {

    final PropostaRepository propostaRepository;
    final AnaliseApiClient analiseApiClient;

    CadastroDePropostaResource(PropostaRepository propostaRepository, AnaliseApiClient analiseApiClient) {
        this.propostaRepository = propostaRepository;
        this.analiseApiClient = analiseApiClient;
    }

    @PostMapping
    ResponseEntity<Void> criaNovaProposta(@RequestBody CriacaoDePropostaRequest request, UriComponentsBuilder uriBuilder) {
        Proposta proposta = propostaRepository.save(request.toModel());

        try {
            ResultadoDaAnalise resultado = analiseApiClient.solicitacao(SolicitacaoDeAnalise.paraProposta(proposta));
            proposta.setStatus(resultado.statusDaProposta());
        } catch (FeignClientException.UnprocessableEntity e) {
            proposta.setStatus(StatusProposta.NAO_ELEGIVEL);
        }

        propostaRepository.save(proposta);

        return ResponseEntity.created(uriBuilder.path("/propostas/{id}").build(proposta.getId())).build();
    }
}