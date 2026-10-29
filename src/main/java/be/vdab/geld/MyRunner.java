package be.vdab.geld;

import be.vdab.geld.mensen.MensService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    private final MensService mensService;

    public MyRunner(MensService mensService) {
        this.mensService = mensService;
    }

    @Override
    public void run(String... args) {
        var scanner = new Scanner(System.in);
        System.out.print("naam: ");
        var naam = scanner.nextLine();
        mensService.verhoogGeldMet10Procent(naam);

        // Oude code uit 18.6:
        // mensService.findAll()
        //         .forEach(mens -> System.out.println(mens));
    }
}
