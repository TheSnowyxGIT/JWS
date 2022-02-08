package fr.epita.assistant.jws.domain.entity;

public enum GameState {
    FINISHED,
    RUNNING,
    STARTING
    ;

    public static GameState of(String value){
        return GameState.valueOf(value);
    }
}