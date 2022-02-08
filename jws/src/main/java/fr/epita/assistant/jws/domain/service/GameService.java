package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.GameMapModel;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameMapRepository;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.*;
import fr.epita.assistant.jws.domain.service.exception.*;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class GameService {

    @Inject
    GameRepository gameRepository;
    @Inject
    GameMapRepository gameMapRepository;
    @Inject
    PlayerRepository playerRepository;

    @ConfigProperty(name = "JWS_MAP_PATH") String mapPath;
    @ConfigProperty(name = "JWS_TICK_DURATION") long tickDuration;
    @ConfigProperty(name = "JWS_DELAY_MOVEMENT") int moveDelay;
    @ConfigProperty(name = "JWS_DELAY_BOMB") int bombDelay;


    public List<GameEntity> getGames(){
        List<GameModel> games = gameRepository.findAll().stream().toList();
        List<GameEntity> gamesEntity = new ArrayList<>();
        for (GameModel game : games) {
            gamesEntity.add(GameEntity.of(game));
        }
        return gamesEntity;
    }

    public GameEntity getGameById(Long gameId) throws JwsGameNotExistException{
        GameModel game = gameRepository.findById(gameId);
        if (game == null){
            throw new JwsGameNotExistException();
        }
        return GameEntity.of(game);
    }

    @Transactional
    public Long createGame(String player_name){

        GameEntity newGame = GameEntity.newEmptyGame();

        MapEntity map = MapEntity.of(mapPath);
        newGame.setMap(map);

        PlayerEntity player = PlayerEntity.newPlayerAtPosition(player_name, newGame.players + 1, map);
        newGame.addPlayer(player);

        GameModel gameModel = GameModel.of(newGame);
        gameRepository.persist(gameModel);
        gameRepository.flush();

        return gameModel.getId();
    }

    @Transactional
    public void joinGame(String player_name, Long gameId) throws JwsAlreadyStartException,
            JwsGameFullException, JwsGameNotExistException
    {
        GameModel gameModel = gameRepository.findById(gameId);
        GameEntity game = GameEntity.of(gameModel);
        if (game == null){
            throw new JwsGameNotExistException();
        }
        if (!game.isJoinable()){
            throw new JwsAlreadyStartException();
        }
        if (game.isFull()){
            throw new JwsGameFullException();
        }

        PlayerEntity newPlayer = PlayerEntity.newPlayerAtPosition(player_name, game.players + 1, game.map);

        PlayerModel playerModel = PlayerModel.of(newPlayer);
        playerModel.setGame(gameModel);

        playerRepository.persist(playerModel);
    }

    @Transactional
    public void startGame(Long gameId) throws JwsGameNotExistException {
        GameModel gameModel = gameRepository.findById(gameId);
        if (gameModel == null){
            throw new JwsGameNotExistException();
        }
        GameEntity gameEntity = GameEntity.of(gameModel);

        gameEntity.setState(GameState.RUNNING);

        if (gameEntity.checkFinished()){
            gameEntity.setState(GameState.FINISHED);
        }

        gameModel.setState(gameEntity.state);
    }


    @Transactional
    public void movePlayer(Long gameId, Long playerId, int posX, int poxY) throws JwsGameNotExistException,
            JwsPlayerNotExistException, JwsPlayerDeadException, JwsNotStartException, JwsInvalidPositionException,
            JwsAlreadyMovedException {
        GameModel gameModel = gameRepository.findById(gameId);
        PlayerModel playerModel = playerRepository.findById(playerId);
        GameEntity game = GameEntity.of(gameModel);
        PlayerEntity player = PlayerEntity.of(playerModel);
        if (game == null){
            throw new JwsGameNotExistException();
        }
        if (player == null){
            throw new JwsPlayerNotExistException();
        }
        if (!game.isRunning()){
            throw new JwsNotStartException();
        }
        if (player.isDead()){
            throw new JwsPlayerDeadException();
        }
        if (!player.movementTimePassed(tickDuration * moveDelay)){
            throw new JwsAlreadyMovedException();
        }
        if (!player.canMoveHere(new Point(posX, poxY), game)){
            throw new JwsInvalidPositionException();
        }
        player.move(new Point(posX, poxY));

        playerModel.load(player);
    }

    @Transactional
    public void bombPlayer(Long gameId, Long playerId, int posX, int posY) throws JwsGameNotExistException,
            JwsPlayerNotExistException, JwsNotStartException, JwsPlayerDeadException, JwsAlreadyBombedException, JwsInvalidPositionException {

        GameModel gameModel = gameRepository.findById(gameId);
        GameEntity game = GameEntity.of(gameModel);
        PlayerEntity player = game.getPlayerById(playerId);
        if (game == null){
            throw new JwsGameNotExistException();
        }
        if (player == null){
            throw new JwsPlayerNotExistException();
        }
        if (!game.isRunning()){
            throw new JwsNotStartException();
        }
        if (player.isDead()){
            throw new JwsPlayerDeadException();
        }
        if (!player.bombTimePassed(tickDuration * bombDelay)){
            throw new JwsAlreadyBombedException();
        }
        if (!player.position.equals(new Point(posX, posY))){
            throw new JwsInvalidPositionException();
        }

        player.bomb(new Point(posX, posY), game, tickDuration * bombDelay, this);

        PlayerModel playerModel = playerRepository.findById(playerId);
        playerModel.load(player);

        MapEntity mapEntity = game.map;
        mapEntity.format();
        List<GameMapModel> sorted = gameModel.map.stream().sorted(Comparator.comparing(GameMapModel::getId)).toList();
        GameMapModel gameMapModel = sorted.get(posY);
        gameMapModel.setMap(mapEntity.map.get(posY).getRight());

    }

    @Transactional
    public void explodeBomb(Bomb bomb){
        GameModel gameModel = gameRepository.findById(bomb.gameId);
        GameEntity gameEntity = GameEntity.of(gameModel);

        MapEntity mapEntity = gameEntity.map;
        mapEntity.destroyWithBomb(bomb);

        List<PlayerEntity> players = gameEntity.players_list;
        players.stream().filter(playerEntity -> bomb.isInRadius(playerEntity))
                .forEach(PlayerEntity::takeDamage);

        if (gameEntity.checkFinished()){
            gameEntity.setState(GameState.FINISHED);
        }

        for(int i = 0; i < gameModel.players.size(); i++){
            gameModel.players.get(i).load(players.get(i));
        }
        //map
        mapEntity.format();
        List<GameMapModel> sorted = gameModel.map.stream().sorted(Comparator.comparing(GameMapModel::getId)).toList();
        for(int i = 0; i < gameModel.map.size(); i++){
            sorted.get(i).setMap(mapEntity.map.get(i).getRight());
        }
    }

}
