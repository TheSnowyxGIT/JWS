package fr.epita.assistant.jws.data.repository;

import fr.epita.assistant.jws.data.model.GameModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameRepository implements PanacheRepository<GameModel> {
}
