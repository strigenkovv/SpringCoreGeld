package be.vdab.geld.mensen;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MensTest {
    private Mens jan;
    private Mens mie;

    @BeforeEach
    void beforeEach() {
        jan = new Mens(1, "Jan", BigDecimal.TEN);
        mie = new Mens(1, "Mie", BigDecimal.ONE);
    }

    @Test
    void schenkWijzigtHetGeldVanDeBetrokkenMensen() {
        jan.schenk(mie, BigDecimal.ONE);
        assertThat(jan.getGeld()).isEqualByComparingTo("9");
        assertThat(mie.getGeld()).isEqualByComparingTo("2");
    }

    @Test
    void schenkMisluktBijOnvoldoendeGeld() {
        assertThatExceptionOfType(OnvoldoendeGeldException.class)
                .isThrownBy(() -> jan.schenk(mie, BigDecimal.valueOf(11)));
    }

    @Test
    void schenkMisluktAlsAanMensLeegIs() {
        assertThatNullPointerException()
                .isThrownBy(() -> jan.schenk(null, BigDecimal.ONE));
    }

    @Test
    void schenkMisluktAlsBedragLeegIs() {
        assertThatNullPointerException()
                .isThrownBy(() -> jan.schenk(mie, null));
    }
}
