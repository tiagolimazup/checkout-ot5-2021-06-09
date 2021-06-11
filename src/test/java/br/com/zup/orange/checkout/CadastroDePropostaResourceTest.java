package br.com.zup.orange.checkout;

import br.com.zup.orange.checkout.AnaliseApiClient.ResultadoDaAnalise;
import br.com.zup.orange.checkout.AnaliseApiClient.ResultadoDaSolicitacao;
import javafx.beans.binding.Bindings;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockServerExtension.class)
@AutoConfigureMockMvc
@Transactional
@MockServerSettings(ports = 9999)
class CadastroDePropostaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private PropostaRepository propostaRepository;

    @Test
    void deveCriarUmaNovaProposta(MockServerClient mockServer) throws Exception {
        CriacaoDePropostaRequest request = new CriacaoDePropostaRequest("56074939039", "Tiago", "tiago.lima@zup.com.br");

        ResultadoDaAnalise resultado = new ResultadoDaAnalise(request.documento, request.nome, "", ResultadoDaSolicitacao.SEM_RESTRICAO);

        mockServer.reset()
                .when(request("/api/solicitacao").withMethod("POST"))
                .respond(response().withStatusCode(200)
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(json(resultado)));

        mockMvc.perform(post("/proposta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
            .andExpect(status().isCreated());

        Optional<Proposta> proposta = propostaRepository.findByDocumento("56074939039");

        assertTrue(proposta.isPresent());
        assertTrue(proposta.get().isElegivel());
    }

    @Test
    void deveCriarUmaNovaPropostaComStatusNaoElegivelQuandoAApiDeAnaliseRetornarAlgumaRestricao(MockServerClient mockServer) throws Exception {
        CriacaoDePropostaRequest request = new CriacaoDePropostaRequest("56074939039", "Tiago", "tiago.lima@zup.com.br");

        mockServer.when(request("/api/solicitacao").withMethod("POST"))
                .respond(response().withStatusCode(422));

        mockMvc.perform(post("/proposta")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isCreated());

        Optional<Proposta> proposta = propostaRepository.findByDocumento("56074939039");

        assertTrue(proposta.isPresent());
        assertTrue(proposta.get().isNaoElegivel());
    }

    private String json(Object request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}
