package be.vdab.geld.mensen;

import java.math.BigDecimal;

public class Schenking {
    private final long vanMensId;
    private final long aanMensId;
    private final BigDecimal bedrag;

    public Schenking(long vanMensId, long aanMensId, BigDecimal bedrag) {
        if (vanMensId <= 0) {
            throw new IllegalArgumentException("Id van mens moet positief zijn");
        }
        if (aanMensId <= 0) {
            throw new IllegalArgumentException("Id aan mens moet positief zijn");
        }
        if (vanMensId == aanMensId) {
            throw new IllegalArgumentException("Je kan niet aan jezelf schenken");
        }
        if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bedrag moet positief zijn");
        }
        this.vanMensId = vanMensId;
        this.aanMensId = aanMensId;
        this.bedrag = bedrag;
    }

    public long getVanMensId() {
        return vanMensId;
    }

    public long getAanMensId() {
        return aanMensId;
    }

    public BigDecimal getBedrag() {
        return bedrag;
    }
}
