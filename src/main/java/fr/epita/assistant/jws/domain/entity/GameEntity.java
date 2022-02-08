package fr.epita.assistant.jws.domain.entity;

public class GameEntity {
    public Integer id;
    public Integer players;
    public GameStateEntity state;


    public static enum GameStateEntity {
        FINISHED,
        RUNNING,
        STARTING
    }
}
