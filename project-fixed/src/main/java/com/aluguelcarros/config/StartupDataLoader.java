package com.aluguelcarros.config;

import com.aluguelcarros.model.entity.Agente;
import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.entity.Proprietario;
import com.aluguelcarros.repository.AgenteRepository;
import com.aluguelcarros.repository.AutomovelRepository;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

@Singleton
public class StartupDataLoader implements ApplicationEventListener<ServerStartupEvent> {

    private final AgenteRepository agenteRepository;
    private final AutomovelRepository automovelRepository;

    public StartupDataLoader(AgenteRepository agenteRepository,
                             AutomovelRepository automovelRepository) {
        this.agenteRepository = agenteRepository;
        this.automovelRepository = automovelRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ServerStartupEvent event) {
        seedAgente();
        seedAutomoveis();
    }

    private void seedAgente() {
        if (agenteRepository.findByLogin("agente").isPresent()) {
            return;
        }

        Agente agente = new Agente();
        agente.setNome("Agente Demo");
        agente.setLogin("agente");
        agente.setSenha("123456");
        agente.setPerfil("AGENTE");
        agente.setTipo("BANCO");
        agente.setContato("demo@autoloc.local");
        agenteRepository.save(agente);
    }

    private void seedAutomoveis() {
        if (automovelRepository.findAll().iterator().hasNext()) {
            return;
        }

        automovelRepository.save(criarAutomovel("MAT-1001", "ABC1D23", "Toyota", "Corolla", 2023, "BANCO", 1001));
        automovelRepository.save(criarAutomovel("MAT-1002", "DEF4G56", "Honda", "Civic", 2024, "BANCO", 1001));
        automovelRepository.save(criarAutomovel("MAT-1003", "HIJ7K89", "Jeep", "Renegade", 2022, "EMPRESA", 2002));
    }

    private Automovel criarAutomovel(String matricula,
                                     String placa,
                                     String marca,
                                     String modelo,
                                     int ano,
                                     String tipoProprietario,
                                     int referenciaId) {
        Proprietario proprietario = new Proprietario();
        proprietario.setTipo(tipoProprietario);
        proprietario.setReferenciaId(referenciaId);

        Automovel automovel = new Automovel();
        automovel.setMatricula(matricula);
        automovel.setPlaca(placa);
        automovel.setMarca(marca);
        automovel.setModelo(modelo);
        automovel.setAno(ano);
        automovel.setDisponivel(true);
        automovel.setProprietario(proprietario);
        return automovel;
    }
}