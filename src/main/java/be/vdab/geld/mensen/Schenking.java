package be.vdab.geld.mensen;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Schenking {
    private final long id = 0;
    private final long vanMensId;
    private final long aanMensId;
    private final BigDecimal bedrag;
    private final LocalDateTime wanneer = LocalDateTime.now();

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

    public long getId() {
        return id;
    }

    public long getAanMensId() {
        return aanMensId;
    }

    public BigDecimal getBedrag() {
        return bedrag;
    }

    public LocalDateTime getWanneer() {
        return wanneer;
    }
}
