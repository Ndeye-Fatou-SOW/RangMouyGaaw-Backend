package sn.rangmoygaaw.transport.domain.port.in;

import sn.rangmoygaaw.transport.domain.model.Bus;
import java.util.List;

public interface ListerBusActifsUseCase {

    List<Bus> listerBusActifs();
}