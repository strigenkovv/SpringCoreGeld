package be.vdab.geld.mensen;

public class MensNietGevondenException extends RuntimeException {
    private final long id;

    public MensNietGevondenException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
