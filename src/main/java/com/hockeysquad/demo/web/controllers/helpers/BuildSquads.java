package com.hockeysquad.demo.web.controllers.helpers;

import com.hockeysquad.demo.web.controllers.dto.AllPlayers;
import com.hockeysquad.demo.web.controllers.dto.Player;
import com.hockeysquad.demo.web.controllers.dto.Squad;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/*Author: Manish Mohan
* This class is a helper class that will use Squad Objects to add players to them.
* Step1. Figure out the squad size and final count of player in a squad.
*
* Step 2. Initialize player - Get each players avgerage score.
* Step 3. Calculate the total average for all players. This is the average we will try a build the squad to.
* Step 4. Build squad:
*           - Everytime players are added to the squad, keep track of the current average of the squad.
*           - Add the player with lower deviation between players avg score from the total mean avg to the squad that is the farther from mean.
*           - Make sure that the squad size is balanced as players are being added.
*
* */

@NoArgsConstructor
@Getter
@Setter
public class BuildSquads {

    private List<Player> waitingList;
    private double avgScore;
    private int squadCount = 2;
    private int playersPerSquad;
    Map<Integer, Squad> squadMap = new HashMap<Integer, Squad>();
    Map<Integer, Double> squadScore = new HashMap<Integer, Double>();
    ArrayList<String> errors = new ArrayList<>();

    public BuildSquads(AllPlayers allPlayers, int squadCount){
        if(allPlayers==null || allPlayers.getPlayers()==null){
            errors.add("No players are available.");
            return;
        }
        this.squadCount = squadCount;
        this.waitingList = allPlayers.getPlayers();
        playersPerSquad = this.waitingList.size()/squadCount;

        if(allPlayers.getPlayers().size()<squadCount){
            errors.add("Not allowed to have more squads then the number of players available.");
            return;
        }
        initializePlayers();
        intitializeSquads();
        buildSquad();
    }

    public List<Player> getWaitingList(){
        return this.waitingList;
    }

    public List<Squad> getSquads(){
        if(squadMap==null || squadMap.size()==0){
            return new ArrayList<Squad>();
        }
        List<Squad> squads = new ArrayList<Squad>();
        for(int i=1;i<=squadCount;i++){
            squads.add(squadMap.get(i));
        }
        return squads;
    }

    private  void initializePlayers(){
        this.waitingList.forEach(s->s.computePlayerAverage());//individual player average
        avgScore = this.waitingList.stream().collect(Collectors.averagingDouble(s->s.getPlayerAverage()));
        this.waitingList.forEach(s->s.setPlayerAbsolueDeviation(Math.abs(s.getPlayerAverage()- avgScore)));
        Collections.sort(this.waitingList,Player.sortByAvgDev);
    }
    private void intitializeSquads(){
        squadMap.put(0,new Squad(this.waitingList));
        squadScore.put(0,squadMap.get(0).compteSquadDeviation(avgScore));
        for(int i=1; i<=squadCount;i++ ){
            squadMap.put(i,new Squad(new ArrayList<Player>()));
            squadScore.put(i,squadMap.get(i).compteSquadDeviation(avgScore));
        }
    }
    private void buildSquad(){
        for( int i=0;i<playersPerSquad;i++) {
            List<Player> newList = squadMap.get(0).removePlayers(squadCount);

            assingPlayers(newList,i);
        }
    }
    private void assingPlayers(List<Player> newList, int playerRank){
        Iterator<Player> iter = newList.iterator();
        while(iter.hasNext()){
            addToSquad(iter.next(), playerRank);
        }
    }

    private void addToSquad(Player player, int playerRank){
        nextSquad(playerRank).addNewPlayer(player);
    }
    private Squad nextSquad(int playerRank){
        int squadIndex = -1;
        double squadDeviation = 0.0;
        for(int i=1;i<=squadCount;i++) {
            if (squadMap.get(i).getSquadSize() > playerRank)
                continue;
            if(squadMap.get(i).compteSquadDeviation(avgScore)>squadDeviation){
                squadIndex=i;
                squadDeviation = squadMap.get(i).compteSquadDeviation(avgScore);
            }
        }

        return squadMap.get(squadIndex);
    }







}
