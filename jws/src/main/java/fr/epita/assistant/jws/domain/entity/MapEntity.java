package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.data.model.GameMapModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.gradle.internal.Pair;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@With
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MapEntity {

    public List<Pair<Long, String>> map = new ArrayList<>();

    public List<List<Block>> matrice = new ArrayList<>();


    public void setBlock(Point point, Block block){
        matrice.get(point.y).set(point.x, block);
    }

    public void destroyWithBomb(Bomb bomb){
        Point point = bomb.coord;
        setBlock(point, Block.Emtpy);
        point = new Point(bomb.coord.x + 1, bomb.coord.y);
        if (isIn(point) && this.matrice.get(point.y).get(point.x) == Block.Box){
            setBlock(point, Block.Emtpy);
        }
        point = new Point(bomb.coord.x - 1, bomb.coord.y);
        if (isIn(point) && this.matrice.get(point.y).get(point.x) == Block.Box){
            setBlock(point, Block.Emtpy);
        }
        point = new Point(bomb.coord.x, bomb.coord.y + 1);
        if (isIn(point) && this.matrice.get(point.y).get(point.x) == Block.Box){
            setBlock(point, Block.Emtpy);
        }
        point = new Point(bomb.coord.x, bomb.coord.y - 1);
        if (isIn(point) && this.matrice.get(point.y).get(point.x) == Block.Box){
            setBlock(point, Block.Emtpy);
        }
    }

    public Point getSize(){
        return new Point(this.matrice.get(0).size(), this.matrice.size());
    }

    public boolean isIn(Point coord){
        Point size = getSize();
        if (coord.x >= 0 && coord.x < size.x){
            if (coord.y >= 0 && coord.y < size.y){
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty(Point coord){
        if (!isIn(coord)){
            return false;
        }
        Block b = matrice.get(coord.y).get(coord.x);
        return b == Block.Emtpy;
    }

    public Point getCoordFromPosition(int position){
        int value = (position - 1) % 4;
        Point size = getSize();
        if (value == 0){
            return new Point(1,1);
        } else if (value == 1){
            return new Point(size.x - 2, size.y - 2);
        } else if (value == 3) {
            return new Point(size.x - 2, 1);
        } else {
            return new Point(1, size.y - 2);
        }
    }
    
    
    public String formatLine(int index){
        List<Block> blocks = this.matrice.get(index);
        Block current = null;
        int number = 0;
        StringBuilder str_b = new StringBuilder();
        for (Block block : blocks) {
            if ((block != current && number > 0) || number == 9){
                str_b.append(number + Block.get(current));
                number = 0;
            }
            number += 1;
            current = block;
        }
        if (number > 0){
            str_b.append(number + Block.get(current));
        }
        return new String(str_b);
    }

    public void format(){
        for (int i = 0; i < this.map.size(); i++){
            var currentPair = this.map.get(i);
            String line = formatLine(i);
            Pair<Long, String> pair = Pair.of(currentPair.getLeft(), line);
            this.map.set(i, pair);
        }
    }

    public void generate(){
        this.matrice = new ArrayList<>();
        for (int y = 0; y < map.size(); y++) {
            this.matrice.add(new ArrayList<>());
            String line = map.get(y).getRight();
            for (int i = 0; i + 1 < line.length(); i+=2){
                int number = Integer.parseInt(String.valueOf(line.charAt(i)));
                for (int a = 0; a < number; a++){
                    this.matrice.get(y).add(Block.of(line.charAt(i + 1)));
                }
            }
        }
    }

    public static MapEntity of(String filePath){
        MapEntity map = new MapEntity();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                map.map.add(Pair.of(null, line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.generate();
        return map;
    }

    public static MapEntity of(List<GameMapModel> gameMapModels){
        MapEntity map = new MapEntity();
        gameMapModels.stream().sorted(Comparator.comparing(GameMapModel::getId))
                .forEach(gameMapModel -> {
                    map.map.add(Pair.of(gameMapModel.id, gameMapModel.map));
                });
        map.generate();
        return map;
    }
}
