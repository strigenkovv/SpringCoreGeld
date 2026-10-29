package be.vdab.geld;

import be.vdab.geld.mensen.MensService;
import be.vdab.geld.mensen.Mens;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        System.out.print("Naam: ");
        var naam = scanner.nextLine();
        System.out.print("Geld: ");
        var geld = new BigDecimal(scanner.nextLine());
        var mens = new Mens(0, naam, geld);
        var nieuweId = mensService.create(mens);
        System.out.println("Id van deze mens: " + nieuweId);
    }
}
