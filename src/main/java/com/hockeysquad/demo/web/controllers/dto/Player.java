package com.hockeysquad.demo.web.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private Long id;
    private String firstName;
    private String lastName;
    List<Skills> skills = new ArrayList<>();
    double playerAverage;
    double playerAbsolueDeviation;

    public String getFullName(){
        return firstName+" "+lastName;
    }
    public void computePlayerAverage(){
        playerAverage = skills.stream().collect(Collectors.averagingInt(s->s.getRating()));
    }

    public int getScoreForSkill(String type){
        return skills.stream().filter(skill->skill.getType().equalsIgnoreCase(type)).findFirst().get().getRating();
    }


    public static Comparator<Player> sortByAvgDev = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            if(o1.getPlayerAbsolueDeviation()<=o2.getPlayerAbsolueDeviation())
                return -1;
            else
                return 1;
        }
    };


}
