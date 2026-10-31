package be.vdab.geld.mensen;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

@JdbcTest
@Import({MensService.class, MensRepository.class, SchenkingRepository.class})
@Sql("/mensen.sql")
class MensServiceIntegrationTest {
    private static final String MENSEN_TABLE = "mensen";
    private static final String SCHENKINGEN_TABLE = "schenkingen";

    private final MensService mensService;
    private final JdbcClient jdbcClient;

    MensServiceIntegrationTest(MensService mensService, JdbcClient jdbcClient) {
        this.mensService = mensService;
        this.jdbcClient = jdbcClient;
    }

    private long idVanTestMens1() {
        return jdbcClient.sql("select id from mensen where naam = 'test1'")
                .query(Long.class)
                .single();
    }

    private long idVanTestMens2() {
        return jdbcClient.sql("select id from mensen where naam = 'test2'")
                .query(Long.class)
                .single();
    }

    @Test
    void schenkVoegtEenSchenkingToeEnWijzigtHetGeldVanDeMensen() {
        var vanMensId = idVanTestMens1();
        var aanMensId = idVanTestMens2();
        mensService.schenk(new Schenking(vanMensId, aanMensId,
                BigDecimal.ONE));
        assertThat(countRowsInTableWhere(jdbcClient, MENSEN_TABLE,
                "geld = 999 and id = " + vanMensId)).isOne();
        assertThat(countRowsInTableWhere(jdbcClient, MENSEN_TABLE,
                "geld = 2001 and id = " + aanMensId)).isOne();
        assertThat(countRowsInTableWhere(jdbcClient, SCHENKINGEN_TABLE,
                "bedrag = 1 and vanMensId = " + vanMensId
                        + " and aanMensId = " + aanMensId)).isOne();
    }

    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class)
                .isThrownBy(() -> mensService.schenk(
                        new Schenking(Long.MAX_VALUE, idVanTestMens2(),
                                BigDecimal.ONE)));
    }

    @Test
    void schenkingMetOnbestaandAanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class)
                .isThrownBy(() -> mensService.schenk(
                        new Schenking(idVanTestMens1(), Long.MAX_VALUE,
                                BigDecimal.ONE)));
    }

    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        var vanMensId = idVanTestMens1();
        var aanMensId = idVanTestMens2();
        assertThatExceptionOfType(OnvoldoendeGeldException.class)
                .isThrownBy(() -> mensService.schenk(
                        new Schenking(vanMensId, aanMensId,
                                BigDecimal.valueOf(1_001))));
    }
}
