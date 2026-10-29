package be.vdab.geld.mensen;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MensService {
    private final MensRepository mensRepository;

    public MensService(MensRepository mensRepository) {
        this.mensRepository = mensRepository;
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
}
