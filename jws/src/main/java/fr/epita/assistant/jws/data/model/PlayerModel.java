package fr.epita.assistant.jws.data.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="Player", schema="jws")
@AllArgsConstructor @NoArgsConstructor @With @Getter @Setter
public class PlayerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public GameModel game;

    public LocalDateTime lastbomb;
    public LocalDateTime lastmovement;
    public Integer lives;
    public String name;
    public Integer posx;
    public Integer posy;
    public Integer position;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlayerModel )) return false;
        return id != null && id.equals(((PlayerModel) obj).getId());
    }

    public void load(PlayerEntity playerEntity){
        setLastbomb(playerEntity.lastBomb);
        setLastmovement(playerEntity.lastMovement);
        setLives(playerEntity.lives);
        setName(playerEntity.name);
        setPosx(playerEntity.position.x);
        setPosy(playerEntity.position.y);
        setPosition(playerEntity.number);
    }

    public static PlayerModel of(PlayerEntity playerEntity){
        PlayerModel player = new PlayerModel().withName(playerEntity.name)
                .withPosition(playerEntity.number)
                .withId(playerEntity.id)
                .withLives(playerEntity.lives)
                .withLastbomb(playerEntity.lastBomb)
                .withLastmovement(playerEntity.lastMovement)
                .withPosx(playerEntity.position.x)
                .withPosy(playerEntity.position.y)
                .withGame(null);
        return player;
    }


}
