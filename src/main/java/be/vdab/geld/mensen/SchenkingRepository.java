package be.vdab.geld.mensen;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class SchenkingRepository {
    private final JdbcClient jdbcClient;

    public SchenkingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(Schenking schenking) {
        var sql = """
                insert into schenkingen(vanMensId, aanMensId, wanneer, bedrag)
                values (?, ?, ?, ?)
                """;
        jdbcClient.sql(sql)
                .params(schenking.getVanMensId(), schenking.getAanMensId(),
                        schenking.getWanneer(), schenking.getBedrag())
                .update();
    }
}
