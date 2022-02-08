package fr.epita.assistant.jws.data.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    private Long game_id;

    private Date lastbomb;
    private Date lastmovement;
    private Integer lives;
    private String name;
    private Integer posx;
    private Integer posy;
    private Integer position;


    public void setId(Long id) {
        this.id = id;
    }

    public void setGame_id(Long game_id) {
        this.game_id = game_id;
    }

    public void setLastbomb(Date lastbomb) {
        this.lastbomb = lastbomb;
    }

    public void setLastmovement(Date lastmovement) {
        this.lastmovement = lastmovement;
    }

    public void setLives(Integer lives) {
        this.lives = lives;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosx(Integer posx) {
        this.posx = posx;
    }

    public void setPosy(Integer posy) {
        this.posy = posy;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Long getGame_id() {
        return game_id;
    }

    public Date getLastbomb() {
        return lastbomb;
    }

    public Date getLastmovement() {
        return lastmovement;
    }

    public Integer getLives() {
        return lives;
    }

    public String getName() {
        return name;
    }

    public Integer getPosx() {
        return posx;
    }

    public Integer getPosy() {
        return posy;
    }

    public Integer getPosition() {
        return position;
    }
}
