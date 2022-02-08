package fr.epita.assistant.jws.domain.entity;

public enum Block {
    Wall,
    Emtpy,
    Box,
    Bomb
    ;

    public static Block of(char c){
        if (c == 'M'){
            return Wall;
        } else if (c == 'G'){
            return Emtpy;
        } else if (c == 'W'){
            return Box;
        } else {
            return Bomb;
        }
    }

    public static String get(Block block){
        if (block == Wall){
            return "M";
        } else if (block == Emtpy){
            return "G";
        } else if (block == Box){
            return "W";
        } else {
            return "B";
        }
    }
}
