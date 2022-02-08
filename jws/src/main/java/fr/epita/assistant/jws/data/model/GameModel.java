package fr.epita.assistant.jws.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.*;
import org.gradle.internal.Pair;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="Game", schema="jws")
@AllArgsConstructor
@NoArgsConstructor
@With
@Getter @Setter
public class GameModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime starttime;
    public GameState state;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<PlayerModel> players = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<GameMapModel> map = new ArrayList<>();

    public void addPlayer(PlayerModel player){
        players.add(player);
        player.setGame(this);
    }

    public void addMap(GameMapModel gameMapModel){
        map.add(gameMapModel);
        gameMapModel.setGame(this);
    }


    public static GameModel of(GameEntity gameEntity){
        GameModel game = new GameModel().withId(gameEntity.id)
                .withStarttime(gameEntity.startTime)
                .withState(gameEntity.state);
        for (PlayerEntity playerEntity : gameEntity.players_list) {
            PlayerModel player = PlayerModel.of(playerEntity);
            game.addPlayer(player);
        }
        gameEntity.map.format();
        for (Pair<Long, String> longStringPair : gameEntity.map.map) {
            GameMapModel map = new GameMapModel().withMap(longStringPair.getRight())
                    .withId(longStringPair.getLeft());
            game.addMap(map);
        }
        return game;
    }
}
