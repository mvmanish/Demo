package com.hockeysquad.demo.web.controllers.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Squad {
    @NonNull
    private List<Player> squad;
    private double squadAverageScore;
    private int squadSize=0;
    private int totalSkatingScore=0;
    private int totalShootingScore=0;
    private int totalCheckingScore=0;
    private double totalSkatingAvg;
    private double totalShootingAvg;
    private double totalCheckingAvg;

    public void computeAverages(){
        squadAverageScore = squad.stream().collect(Collectors.averagingDouble(s->s.getPlayerAverage()));

    }


    private void computeTotalSkills(Player player){
        player.getSkills().forEach(skills -> {
            switch (skills.getType()) {
                case "Skating": totalSkatingScore += skills.rating;
                totalSkatingAvg = totalSkatingScore/squadSize;
                break;
                case "Shooting": totalShootingScore += skills.rating;
                    totalShootingAvg = totalShootingScore/squadSize;
                break;
                case "Checking": totalCheckingScore += skills.rating;
                    totalCheckingAvg=totalCheckingScore/squadSize;
            }
        } );
    }

    public double compteSquadDeviation(double averageScore){
        return Math.abs(averageScore-squadAverageScore);
    }

    public void addNewPlayer(Player player){
        squad.add(player);
        squadSize++;
        computeTotalSkills(player);
        computeAverages();
    }

    public List<Player> removePlayers(int count){
        List<Player> removedPlayers = new ArrayList<>();
        for(int i=0;i<count;i++){
            removedPlayers.add(squad.get(0));
            squad.remove(0);
        }
        return removedPlayers;
    }


}
