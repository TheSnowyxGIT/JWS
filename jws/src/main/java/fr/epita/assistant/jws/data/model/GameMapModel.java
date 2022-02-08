package fr.epita.assistant.jws.data.model;

import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.MapEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="GameMap", schema="jws")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@With
@Getter
@Setter
public class GameMapModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;


    @ManyToOne
    private GameModel game;

    public String map;
}
