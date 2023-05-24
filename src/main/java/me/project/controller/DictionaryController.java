package me.project.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/dictionary")
@AllArgsConstructor
public class DictionaryController {

    @GetMapping(path = "/index")
    public void hello(){

    }

}
