package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import backend.ui.TerminalUI;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	
    	TerminalUI forDBSeed = new TerminalUI();
    	forDBSeed.seedDemoDataIfNeeded();
        forDBSeed.subscribeDefaultObservers();
        
        SpringApplication.run(Application.class, args);
    }
}