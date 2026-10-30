package be.vdab.geld.mensen;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MensRepository {
    private final JdbcClient jdbcClient;

    public MensRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public long findAantal() {
        var sql = """
                select count(*)
                from mensen
                """;
        return jdbcClient.sql(sql)
                .query(Long.class)
                .single();
    }

    public void delete(long id) {
        var sql = """
                delete from mensen
                where id = ?
                """;
        jdbcClient.sql(sql)
                .param(id)
                .update();
    }

    public void update(Mens mens) {
        var sql = """
                update mensen
                set naam = ?, geld = ?
                where id = ?
                """;
        if (jdbcClient.sql(sql)
                .params(mens.getNaam(), mens.getGeld(), mens.getId())
                .update() == 0) {
            throw new MensNietGevondenException(mens.getId());
        }
    }

    public long create(Mens mens) {
        var sql = """
                insert into mensen(naam, geld)
                values (?, ?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .params(mens.getNaam(), mens.getGeld())
                .update(keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Mens> findAll() {
        var sql = """
                select id, naam, geld
                from mensen
                order by id
                """;
        return jdbcClient.sql(sql)
                .query(Mens.class)
                .list();
    }

    public List<Mens> findByGeldBetween(BigDecimal van, BigDecimal tot) {
        var sql = """
                select id, naam, geld
                from mensen
                where geld between ? and ?
                order by geld
                """;
        return jdbcClient.sql(sql)
                .params(van, tot)
                .query(Mens.class)
                .list();
    }

    public Optional<Mens> findById(long id) {
        var sql = """
                select id, naam, geld
                from mensen
                where id = ?
                """;
        return jdbcClient.sql(sql)
                .param(id)
                .query(Mens.class)
                .optional();
    }

    public Optional<Mens> findAndLockById(long id) {
        var sql = """
                select id, naam, geld
                from mensen
                where id = ?
                for update
                """;
        return jdbcClient.sql(sql)
                .param(id)
                .query(Mens.class)
                .optional();
    }

    public List<Mens> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        var sql = """
                select id, naam, geld
                from mensen
                where id in (
                """
                + "?,".repeat(ids.size() - 1)
                + "?)";
        return jdbcClient.sql(sql)
                .params(ids)
                .query(Mens.class)
                .list();
    }

    public void verhoogGeldMet10Procent(String naam) {
        var sql = "update mensen set geld = geld * 1.1 where naam = ?";
        jdbcClient.sql(sql)
                .param(naam)
                .update();
    }
}
