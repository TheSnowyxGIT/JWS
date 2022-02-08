package fr.epita.assistant.jws.domain.entity;



import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.domain.service.GameService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@With
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerEntity {

    public static final int maxLifes = 3;

    public Long id;
    public String name;
    public Integer lives;
    public Point position;
    public int number;
    public LocalDateTime lastBomb;
    public LocalDateTime lastMovement;

    public boolean isAlive(){
        return lives > 0;
    }

    public boolean isDead(){
        return ! isAlive();
    }

    public void move(Point point){
        this.position = point;
        this.lastMovement = LocalDateTime.now();
    }

    public void takeDamage(){
        this.lives -= 1;
        this.lives = this.lives < 0 ? 0 : this.lives;
    }

    public void bomb(Point point, GameEntity game, Long timeExplode, GameService gameService){
        this.lastBomb = LocalDateTime.now();
        new Bomb(point, game.id, timeExplode, gameService);
        game.map.setBlock(point, Block.Bomb);
    }

    public boolean movementTimePassed(long timeBetweenMoves){
        if (this.lastMovement == null){
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        Long time = Math.abs(ChronoUnit.MILLIS.between(now, this.lastMovement));
        return time >= timeBetweenMoves;
    }

    public boolean bombTimePassed(long timeBetweenBombs){
        if (this.lastBomb == null){
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        Long time = Math.abs(ChronoUnit.MILLIS.between(now, this.lastBomb));
        return time >= timeBetweenBombs;
    }

    public boolean canMoveHere(Point coord, GameEntity game){
        if (!game.map.isEmpty(coord)){
            return false;
        }
        int distance = Math.abs(coord.y - position.y) + Math.abs(coord.x - position.x);
        if (distance != 1){
            return false;
        }
        return true;
    }

    public Long getId(){
        return this.id;
    }


    public static PlayerEntity newPlayerAtPosition(String name, int position, MapEntity map){
        return new PlayerEntity().withName(name).withLives(maxLifes)
                .withPosition(map.getCoordFromPosition(position))
                .withNumber(position)
                .withLastBomb(null)
                .withLastMovement(null);
    }

    public static PlayerEntity of(PlayerModel playerModel){
        if (playerModel == null){
            return null;
        }
        return new PlayerEntity(playerModel.id, playerModel.name,
                playerModel.lives, new Point(playerModel.posx, playerModel.posy),
                playerModel.position, playerModel.lastbomb, playerModel.lastmovement);
    }

}
