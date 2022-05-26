package com.example.lab1.Controller;

import com.example.lab1.Validations.Results;
import com.example.lab1.logger.Logger;
import com.example.lab1.Repository;
import com.example.lab1.DataClass;
import com.example.lab1.Cache.cache;
import com.example.lab1.App;
import com.example.lab1.Results.Solution;
import com.example.lab1.Validations.InputValidation;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.example.lab1.Stats.Statistics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class MainController {

    private final AtomicLong counter = new AtomicLong();
    RequestCounterController NumberOfRequests = new RequestCounterController();
    Repository rep = new Repository();
    @GetMapping("/app")
    public Results Enter (
            @RequestParam(value = "year", required = true, defaultValue = "2022") int iYear,
            @RequestParam(value = "month", required = true, defaultValue = "4") int iMonth,
            @RequestParam(value = "date", required = true, defaultValue = "5") int iDate)
    {
        NumberOfRequests.IncremetNumber();
        App ThisDay = new App(iYear, iMonth, iDate);
        Logger.log(Level.INFO,  "Successfully getMapping");
        if(rep.isContain(ThisDay)) {
            var b = new Solution(ThisDay);
            b.calculateRoot();
            Logger.log(Level.INFO,  "This date is already in the cache");
            return rep.getParameters(ThisDay);
        }
        else {
            var b = new Solution(ThisDay);
            b.calculateRoot();
            Logger.log(Level.INFO,  "This date is added in the cache");
            return rep.addToMap(ThisDay,InputValidation.optionsValidation(counter.incrementAndGet(),ThisDay));
        }
    }


    @GetMapping("/cache")
    public ResponseEntity<String> printCache() {
        return new ResponseEntity<>(cache.getStaticStringCache(), HttpStatus.OK);
    }


    @PostMapping(value = "/post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity alternativeCalculation(@RequestBody int[] array) {
        return new ResponseEntity<>(Statistics.post(array), HttpStatus.OK);
    }


    private static final String template = "January: %d,  December: %d";

    @PostMapping("/stats")
    public ResponseEntity<?> EnterStream(@Valid @RequestBody List<DataClass> bodyList){
        Statistics st = new Statistics();
        List<String> res_output = new ArrayList<>();
        NumberOfRequests.IncremetNumber();

        res_output.add(String.format(template, st.findJanuary(bodyList), st.findDecember(bodyList)));

        return new ResponseEntity<>(res_output, HttpStatus.OK);
    }

}
