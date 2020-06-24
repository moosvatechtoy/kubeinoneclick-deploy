package com.prokarma.oneclick.teams.controller;

import com.prokarma.oneclick.teams.repository.TeamRepository;
import com.prokarma.oneclick.teams.Team;
import com.prokarma.oneclick.teams.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamsRestController {

    private TeamRepository teamRepository;

    @Autowired
    public TeamsRestController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public List<Team> teams(){
        return this.teamRepository.findAll();
    }

}
