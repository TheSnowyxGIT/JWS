package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.data.model.GameMapModel;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.presentation.GameRessource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@With @NoArgsConstructor
@AllArgsConstructor @ToString
public class GameEntity {

    public static final int maxPlayer = 4;

    public Long id;
    public Integer players;
    public GameState state;
    public LocalDateTime startTime;
    public List<PlayerEntity> players_list = new ArrayList<>();
    public MapEntity map;

    public PlayerEntity getPlayerById(Long playerId){
        Optional<PlayerEntity> p = this.players_list.stream()
                .filter(playerEntity -> playerEntity.id.equals(playerId))
                .findFirst();
        if (p.isEmpty()){
            return null;
        }
        return p.get();
    }

    public boolean isFull(){
        return players >= maxPlayer;
    }

    public boolean isJoinable(){
        return state == GameState.STARTING;
    }

    public boolean isRunning(){
        return state == GameState.RUNNING;
    }

    public boolean checkFinished(){
        int numberAlive = 0;
        for (PlayerEntity playerEntity : this.players_list) {
            if (playerEntity.isAlive()){
                numberAlive += 1;
            }
        }
        if (numberAlive <= 1){
            return true;
        }
        return false;
    }

    public void addPlayer(PlayerEntity player){
        this.players_list.add(player);
        this.players += 1;
    }

    public void setMap(MapEntity map){
        this.map = map;
    }

    public void setState(GameState state){
        this.state = state;
    }


    public static GameEntity of(GameModel gameModel){
        if (gameModel == null){
            return null;
        }
        List<PlayerEntity> players = gameModel.players.stream()
                .sorted(Comparator.comparing(PlayerModel::getId))
                .map(PlayerEntity::of).toList();
        MapEntity map = MapEntity.of(gameModel.map);
        return new GameEntity().withId(gameModel.id)
                .withPlayers(gameModel.players.size())
                .withState(gameModel.state)
                .withStartTime(gameModel.starttime)
                .withPlayers_list(players)
                .withMap(map);
    }

    public static GameEntity newEmptyGame(){
        GameEntity game = new GameEntity().withPlayers(0)
                .withPlayers_list(new ArrayList<>())
                .withState(GameState.STARTING)
                .withStartTime(LocalDateTime.now())
                .withMap(null);
        return game;
    }


}
