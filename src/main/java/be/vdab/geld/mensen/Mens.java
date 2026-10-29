package be.vdab.geld.mensen;

import java.math.BigDecimal;

public class Mens {
    private final long id;
    private final String naam;
    private final BigDecimal geld;

    public Mens(long id, String naam, BigDecimal geld) {
        this.id = id;
        this.naam = naam;
        this.geld = geld;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public BigDecimal getGeld() {
        return geld;
    }

    @Override
    public String toString() {
        return id + ": " + naam + ": " + geld + "€";
    }
}
