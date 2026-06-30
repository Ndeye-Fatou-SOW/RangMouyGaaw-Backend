package sn.rangmoygaaw.transport.application.query;

import org.springframework.stereotype.Service;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.model.BusStatut;
import sn.rangmoygaaw.transport.domain.port.in.ListerBusActifsUseCase;
import sn.rangmoygaaw.transport.domain.port.out.BusRepository;

import java.util.List;

@Service
public class ListerBusActifsService implements ListerBusActifsUseCase {

    private final BusRepository busRepository;

    public ListerBusActifsService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public List<Bus> listerBusActifs() {
        return busRepository.findByStatut(BusStatut.EN_SERVICE);
    }
}