package wz.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by florinbotis on 05/07/2016.
 */

@Entity
public class Flight {


    @Id
    @GeneratedValue
    private long id;
    private Date fetchedDate;
    private Date flightDate;
    private String departure;
    private String arrival;
    private String fromm;
    private String to;
    private BigDecimal price;


    public Flight() {

    }


    public void setFetchedDate(Date fetchedDate) {
        this.fetchedDate = fetchedDate;
    }


    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }


    public void setDeparture(String departure) {
        this.departure = departure;
    }


    public void setArrival(String arrival) {
        this.arrival = arrival;
    }


    public void setFromm(String from) {
        this.fromm = from;
    }


    public void setTo(String to) {
        this.to = to;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fetchedDate", fetchedDate)
                .add("flightDate", flightDate)
                .add("departure", departure)
                .add("arrival", arrival)
                .add("fromm", fromm)
                .add("to", to)
                .add("price", price)
                .toString();
    }


    public long getId() {
        return id;
    }

    public Date getFetchedDate() {
        return fetchedDate;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getFromm() {
        return fromm;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
