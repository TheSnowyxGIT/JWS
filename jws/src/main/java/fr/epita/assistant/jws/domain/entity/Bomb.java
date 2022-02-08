package fr.epita.assistant.jws.domain.entity;


import fr.epita.assistant.jws.domain.service.GameService;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bomb implements Runnable {

    public Long gameId;
    public Point coord;
    public GameService gameService;

    public Bomb(Point coord, Long gameId, Long timeExplode, GameService gameService) {
        this.coord = coord;
        this.gameId = gameId;
        this.gameService = gameService;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(this, timeExplode, TimeUnit.MILLISECONDS);
    }

    public boolean isInRadius(PlayerEntity playerEntity){
        Point playerCoord = playerEntity.position;
        int distance = Math.abs(coord.y - playerCoord.y) + Math.abs(coord.x - playerCoord.x);
        if (distance <= 1){
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        // On timeout
        gameService.explodeBomb(this);
    }
}
