package wz.store;

import org.springframework.data.repository.CrudRepository;
import wz.model.Flight;

/**
 * Created by florinbotis on 05/07/2016.
 */
public interface FlightStore extends CrudRepository<Flight, Long> {
}
