package be.vdab.geld.mensen;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

@JdbcTest
@Import(MensRepository.class)
@Sql("/mensen.sql")
class MensRepositoryTest {
    private static final String MENSEN_TABLE = "mensen";

    private final MensRepository mensRepository;
    private final JdbcClient jdbcClient;

    MensRepositoryTest(MensRepository mensRepository, JdbcClient jdbcClient) {
        this.mensRepository = mensRepository;
        this.jdbcClient = jdbcClient;
    }

    @Test
    void findAantalGeeftHetJuisteAantalMensen() {
        var aantalRecords = countRowsInTable(jdbcClient, MENSEN_TABLE);
        assertThat(mensRepository.findAantal()).isEqualTo(aantalRecords);
    }

    @Test
    void findAllGeeftAlleMensenGesorteerdOpId() {
        var aantalRecords = countRowsInTable(jdbcClient, MENSEN_TABLE);
        assertThat(mensRepository.findAll())
                .hasSize(aantalRecords)
                .extracting(Mens::getId)
                .isSorted();
    }

    @Test
    void createVoegtEenMensToe() {
        var id = mensRepository.create(new Mens(0, "test3", BigDecimal.TEN));
        assertThat(id).isPositive();
        var aantalRecordsMetDeIdVanDeToegevoegdeMens =
                countRowsInTableWhere(jdbcClient, MENSEN_TABLE, "id=" + id);
        assertThat(aantalRecordsMetDeIdVanDeToegevoegdeMens).isOne();
    }

    private long idVanTestMens1() {
        return jdbcClient.sql("select id from mensen where naam = 'test1'")
                .query(Long.class)
                .single();
    }

    @Test
    void deleteVerwijdertEenMens() {
        var id = idVanTestMens1();
        mensRepository.delete(id);
        var aantalRecordsMetDeIdVanDeVerwijderdeMens =
                countRowsInTableWhere(jdbcClient, MENSEN_TABLE, "id=" + id);
        assertThat(aantalRecordsMetDeIdVanDeVerwijderdeMens).isZero();
    }

    @Test
    void findByIdMetBestaandeIdVindtEenMens() {
        assertThat(mensRepository.findById(idVanTestMens1()))
                .hasValueSatisfying(mens ->
                        assertThat(mens.getNaam()).isEqualTo("test1"));
    }

    @Test
    void findByIdMetOnbestaandeIdVindtGeenMens() {
        assertThat(mensRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void updateWijzigtEenMens() {
        var id = idVanTestMens1();
        var mens = new Mens(id, "mens1", BigDecimal.TEN);
        mensRepository.update(mens);
        var aantalGewijzigdeRecords = countRowsInTableWhere(
                jdbcClient, MENSEN_TABLE, "geld = 10 and id =" + id);
        assertThat(aantalGewijzigdeRecords).isOne();
    }

    @Test
    void updateOnbestaandeMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class)
                .isThrownBy(() -> mensRepository.update(
                        new Mens(Long.MAX_VALUE, "test3", BigDecimal.TEN)));
    }

    @Test
    void findByGeldBetweenVindtDeJuisteMensen() {
        var van = BigDecimal.ONE;
        var tot = BigDecimal.TEN;
        var aantalRecords = countRowsInTableWhere(
                jdbcClient, MENSEN_TABLE, "geld between 1 and 10");
        assertThat(mensRepository.findByGeldBetween(van, tot))
                .hasSize(aantalRecords)
                .extracting(Mens::getGeld)
                .allSatisfy(geld -> assertThat(geld).isBetween(van, tot))
                .isSorted();
    }

    @Test
    void findbyIdsMetEenLegeLijstIdsGeeftEenLegeLijstMensen() {
        assertThat(mensRepository.findByIds(List.of())).isEmpty();
    }

    private long idVanTestMens2() {
        return jdbcClient.sql("select id from mensen where naam = 'test2'")
                .query(Long.class)
                .single();
    }

    @Test
    void findByIdsVindtDeJuisteMensen() {
        var idVanTestMens1 = idVanTestMens1();
        var idVanTestMens2 = idVanTestMens2();
        assertThat(mensRepository.findByIds(
                List.of(idVanTestMens1, idVanTestMens2)))
                .hasSize(2)
                .extracting(Mens::getId)
                .containsOnly(idVanTestMens1, idVanTestMens2);
    }
}
