package com.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.test.services.CoronaVirusDataService;

@Controller
public class MainController {
	
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("locationStats",coronaVirusDataService.getAllStats());
		model.addAttribute("totalCasesReportedToday",coronaVirusDataService.getTotalCases());
		return "home";
	}
}
