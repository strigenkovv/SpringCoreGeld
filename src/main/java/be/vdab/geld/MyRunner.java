package be.vdab.geld;

import be.vdab.geld.mensen.MensNietGevondenException;
import be.vdab.geld.mensen.MensService;
import be.vdab.geld.mensen.OnvoldoendeGeldException;
import be.vdab.geld.mensen.Schenking;
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
        System.out.print("Id van mens: ");
        var vanMensId = Long.parseLong(scanner.nextLine());
        System.out.print("Id aan mens: ");
        var aanMensId = Long.parseLong(scanner.nextLine());
        System.out.print("Bedrag: ");
        var bedrag = new BigDecimal(scanner.nextLine());
        try {
            var schenking = new Schenking(vanMensId, aanMensId, bedrag);
            mensService.schenk(schenking);
            System.out.println("Schenking gelukt");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (MensNietGevondenException ex) {
            System.out.println("Schenking mislukt. Mens ontbreekt. Id:"
                    + ex.getId());
        } catch (OnvoldoendeGeldException ex) {
            System.out.println("Schenking mislukt. Onvoldoende geld.");
        }
    }
}
