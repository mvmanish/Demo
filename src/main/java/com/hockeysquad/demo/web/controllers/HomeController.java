package com.hockeysquad.demo.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hockeysquad.demo.web.controllers.dto.AllPlayers;
import com.hockeysquad.demo.web.controllers.dto.UserInputForm;
import com.hockeysquad.demo.web.controllers.helpers.BuildSquads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Controller
public class HomeController {

    @Autowired
    private Environment environment;
    private ArrayList<String> errors = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/")
    public String home(Model model) throws IOException {
        errors = new ArrayList<>();
        AllPlayers allPlayers = getData();
        UserInputForm userInputForm= new UserInputForm();
        model.addAttribute("userInputForm",userInputForm);
        model.addAttribute("waitingList", allPlayers.getPlayers());
        model.addAttribute("allowSquadCreation",allPlayers.getPlayers().isEmpty()?false:true);
        model.addAttribute("allowSquadReset", false);
        model.addAttribute("errors", errors);
        return "home/welcome";
    }

    @PostMapping("/resetSquads")
    public String resetSquads(Model model) {
        errors = new ArrayList<>();
        AllPlayers allPlayers = getData();
        UserInputForm userInputForm= new UserInputForm();
        model.addAttribute("userInputForm",userInputForm);
        model.addAttribute("waitingList", allPlayers.getPlayers());
        model.addAttribute("allowSquadCreation",true);
        model.addAttribute("allowSquadReset", false);
        model.addAttribute("errors", errors);

        return "redirect:/";
    }

    @PostMapping("/createSquads")
    public String createSquads(@ModelAttribute UserInputForm userInputForm, Model model, RedirectAttributes redirectAttributes){
        errors = new ArrayList<>();
        BuildSquads buildSquads = new BuildSquads(getData(), userInputForm.getSquadCount());
        buildSquads.getErrors().forEach(error -> this.errors.add(error));
        model.addAttribute("waitingList",buildSquads.getWaitingList());
        model.addAttribute("squadsList",buildSquads.getSquads());
        model.addAttribute("allowSquadCreation", buildSquads.getSquads().size()>0?false:true);
        model.addAttribute("allowSquadReset", buildSquads.getSquads().size()>0?true:false);
        model.addAttribute("errors", this.errors);
        return "home/welcome";
    }

    /*
    * If user provided file location is provided, then use that to load data.
    * If not then use the default 40 players list that is part of the project as json file.
    * */
    private AllPlayers readDataFromFile(){
        errors = new ArrayList<>();
        AllPlayers allPlayers= new AllPlayers();
        String fileLocation = environment.getProperty("json.default.file.location");
        String userFileLocation = environment.getProperty("json.file.location");
        if(userFileLocation!=null && !userFileLocation.isEmpty()){
            allPlayers = getDataFromUserFile(userFileLocation);
        }else{
            allPlayers = getDataFromDefaultFile(fileLocation);
        }
        return allPlayers;
    }

    private AllPlayers getDataFromDefaultFile(String defaultFileLocation){
        AllPlayers allPlayers= new AllPlayers();

        ClassPathResource resource = new ClassPathResource(defaultFileLocation);
        InputStream resourceInputStream = null;
        try {
            resourceInputStream = resource.getInputStream();
            allPlayers = mapper.readValue(resourceInputStream,AllPlayers.class);
        } catch (IOException e) {
            errors.add("Default JSON file is invalid");
        }
        return  allPlayers;
    }


    private AllPlayers getDataFromUserFile(String userFileLocation){
        AllPlayers allPlayers= new AllPlayers();
        try {
            FileInputStream fis = new FileInputStream(new File(userFileLocation));
            allPlayers = mapper.readValue(fis,AllPlayers.class);
        }catch (IOException e){
            errors.add("JSON file provided "+userFileLocation+" is invalid.");
        }
        return  allPlayers;
    }

    /*Get data to build squad.
    * Step 1. Get the REST URL if it is provided.
    * Step 2. If REST URL is not provided, get it from the json file.*/
    private AllPlayers getData(){

        RestTemplate restTemplate = new RestTemplate();
        AllPlayers allPlayers = new AllPlayers();
        String restUrl = environment.getProperty("service.resturl");

        if(restUrl!=null && !restUrl.isEmpty()){
            try{
                AllPlayers players = restTemplate.getForObject(restUrl, AllPlayers.class);
            } catch(Exception e){
                StringBuilder sb = new StringBuilder();
                errors.add("RestURL "+restUrl+" provided is invalid.");
            }
        }else{
            allPlayers=readDataFromFile();
        }
        return allPlayers;
    }
}
