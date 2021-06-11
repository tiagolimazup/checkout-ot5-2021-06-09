package br.com.zup.orange.checkout;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface PropostaRepository extends JpaRepository<Proposta, Long> {
    Optional<Proposta> findByDocumento(String document);
}
