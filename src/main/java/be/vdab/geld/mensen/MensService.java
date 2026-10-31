package be.vdab.geld.mensen;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MensService {
    private final MensRepository mensRepository;
    private final SchenkingRepository schenkingRepository;

    public MensService(MensRepository mensRepository,
                       SchenkingRepository schenkingRepository) {
        this.mensRepository = mensRepository;
        this.schenkingRepository = schenkingRepository;
    }

    public List<Mens> findAll() {
        return mensRepository.findAll();
    }

    public Optional<Mens> findById(long id) {
        return mensRepository.findById(id);
    }

    @Transactional
    public long create(Mens mens) {
        return mensRepository.create(mens);
    }

    @Transactional
    public void verhoogGeldMet10Procent(String naam) {
        mensRepository.verhoogGeldMet10Procent(naam);
    }

    @Transactional
    public void schenk(Schenking schenking) {
        var vanMensId = schenking.getVanMensId();
        var vanMens = mensRepository.findAndLockById(vanMensId)
                .orElseThrow(() -> new MensNietGevondenException(vanMensId));
        var aanMensId = schenking.getAanMensId();
        var aanMens = mensRepository.findAndLockById(aanMensId)
                .orElseThrow(() -> new MensNietGevondenException(aanMensId));
        vanMens.schenk(aanMens, schenking.getBedrag());
        mensRepository.update(vanMens);
        mensRepository.update(aanMens);
        schenkingRepository.create(schenking);
    }
}
