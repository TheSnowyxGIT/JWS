package fr.epita.assistant.jws.presentation;


import fr.epita.assistant.jws.data.model.GameMapModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.exception.*;
import fr.epita.assistant.jws.presentation.utils.RequestManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;
import org.gradle.internal.Pair;
import org.jboss.resteasy.annotations.ResponseObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/games")
public class GameRessource {

    @Inject
    GameService gameService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGames(){
        List<GameEntity> games = gameService.getGames();
        List<GameListResponse> gamesDTO = games.stream()
                .map(GameListResponse::of).toList();
        return Response.status(Response.Status.OK)
                .entity(gamesDTO)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addGames(final CreateGameRequest createGameRequest){
        if (createGameRequest == null || !RequestManager.hasParams(createGameRequest)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Long gameId = gameService.createGame(createGameRequest.name);
        GameEntity game = null;
        try {
            game = gameService.getGameById(gameId);
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gameId}")
    public Response getGameInfo(@PathParam("gameId") final Long gameId){
        GameEntity game = null;
        try {
            game = gameService.getGameById(gameId);
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gameId}")
    public Response joinGame(@PathParam("gameId") final Long gameId, final CreateGameRequest createGameRequest) {
        if (!RequestManager.hasParams(createGameRequest)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        GameEntity game = null;
        try {
            gameService.joinGame(createGameRequest.name, gameId);
            game = gameService.getGameById(gameId);
        } catch (JwsAlreadyStartException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsGameFullException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{gameId}/start")
    public Response startGame(@PathParam("gameId") final Long gameId) {
        GameEntity game = null;
        try {
            gameService.startGame(gameId);
            game = gameService.getGameById(gameId);
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{gameId}/players/{playerId}/move")
    public Response move(@PathParam("gameId") final Long gameId, @PathParam("playerId") final Long playerId,
                              CoordRequest coordRequest) {
        if (coordRequest == null || !RequestManager.hasParams(coordRequest)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        GameEntity game = null;
        try {
            gameService.movePlayer(gameId, playerId, coordRequest.posX, coordRequest.posY);
            game = gameService.getGameById(gameId);
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (JwsNotStartException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsPlayerNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (JwsPlayerDeadException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsInvalidPositionException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsAlreadyMovedException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).build();
        }

        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{gameId}/players/{playerId}/bomb")
    public Response bomb(@PathParam("gameId") final Long gameId, @PathParam("playerId") final Long playerId,
                         CoordRequest coordRequest) {
        if (coordRequest == null || !RequestManager.hasParams(coordRequest)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        GameEntity game = null;
        try {
            gameService.bombPlayer(gameId, playerId, coordRequest.posX, coordRequest.posY);
            game = gameService.getGameById(gameId);
        } catch (JwsGameNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (JwsNotStartException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsPlayerNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (JwsPlayerDeadException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsInvalidPositionException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JwsAlreadyBombedException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).build();
        }

        GameDetailResponse gameDTO = GameDetailResponse.of(game);
        return Response.status(Response.Status.OK)
                .entity(gameDTO).build();
    }




    @With
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateGameRequest {
        public String name;
    }

    @With
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameListResponse {
        public Long id;
        public Integer players;
        public GameState state;

        public static GameListResponse of(GameEntity gameEntity){
            return new GameListResponse(gameEntity.id, gameEntity.players,
                    gameEntity.state);
        }

    }

    @With
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameDetailResponse {
        public LocalDateTime startTime;
        public GameState state;
        public List<PlayerResponse> players;
        public List<String> map;
        public Long id;

        public static GameDetailResponse of(GameEntity gameEntity){
            List<PlayerResponse> players = gameEntity.players_list.stream()
                    .map(PlayerResponse::of).toList();
            List<String> map = gameEntity.map.map.stream()
                    .sorted(Comparator.comparing(Pair::getLeft))
                    .map(Pair::getRight).toList();
            return new GameDetailResponse().withStartTime(gameEntity.startTime)
                    .withState(gameEntity.state)
                    .withPlayers(players)
                    .withMap(map)
                    .withId(gameEntity.id);
        }
    }

    @With
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerResponse {
        public Long id;
        public String name;
        public Integer lives;
        public Integer posX;
        public Integer posY;

        public static PlayerResponse of(PlayerEntity playerEntity){
            return new PlayerResponse().withId(playerEntity.id)
                    .withName(playerEntity.name)
                    .withLives(playerEntity.lives)
                    .withPosX(playerEntity.position.x)
                    .withPosY(playerEntity.position.y);
        }
    }

    @With
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoordRequest {
        public int posX;
        public int posY;
    }


}
