package wz.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wz.model.Flight;
import wz.store.FlightStore;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by florinbotis on 05/07/2016.
 */
public class Parser {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FlightStore store;

    private SimpleDateFormat folderSimpleDateFormat = new SimpleDateFormat("ddMMyyyy");
    private SimpleDateFormat fileDateFormat = new SimpleDateFormat("ddMMyyyy-HH-mm");
    private SimpleDateFormat flightDateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final Path rootFolder;

    public Parser(Path rootFolder, FlightStore store) {
        this.rootFolder = rootFolder;
        this.store = store;
    }

    public void parse() {
        List<Path> dataFolders = getDataFolders(rootFolder);
        for (Path path : dataFolders) {
            processDataFolder(path);
        }
    }


    private void processDataFolder(Path path) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
                //if not already processed
                if (file.getFileName().toString().startsWith("_")) {
                    continue;

                }

                processFile(file);
            }
        } catch (IOException | DirectoryIteratorException x) {
            log.error("Error while processing data folder path={}", path, x);
        }

    }


    private void processFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file));
            Date fetcheDate = fileDateFormat.parse(file.getFileName().toString());
            List<Flight> flights = getFlights(fetcheDate, content);
            saveFlights(flights);
            Path newFileName = Paths.get(file.getParent().toString(), "_" + file.getFileName().toString());
            Files.move(file, newFileName);
        } catch (Exception e) {
            log.error("Error processing file={}", file, e);
        }
    }

    private void saveFlights(List<Flight> flights) {
        log.info("Saving flights={}", flights);
        store.save(flights);
    }

    private List<Path> getDataFolders(Path dir) {
        List<Path> dataFolders = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                //if has an invalid format skip
                if (file.getFileName().toString().length() != 8) {
                    continue;

                }

                try {
                    Date date = folderSimpleDateFormat.parse(file.getFileName().toString());
                    dataFolders.add(file);
                } catch (ParseException e) {
                    log.debug("Invalid folder={}" + file, e);
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            log.error("Error getting data folders", x);
        }
        return dataFolders;
    }

    private List<Flight> getFlights(Date fetchedDate, String data) {
        List<Flight> flights = new ArrayList<>();

        //FROM -> TO
        String[] lines = data.split("\n");
        String[] fromTo = lines[1].split("\\→");
        String from = fromTo[0].trim();
        String to = fromTo[1].trim();


        Flight f = null;

        Date flightDate = null;
        boolean dateFound = false;
        for (String line : lines) {
            if (dateFound) {
                String[] timeAndPrices = line.split(" ");
                String departure = timeAndPrices[0];
                String arrival = timeAndPrices[2];
                String price = timeAndPrices[3];
                f.setDeparture(departure);
                f.setArrival(arrival);
                f.setPrice(getPriceInRON(price));
                flights.add(f);
                f = null;
                dateFound = false;
            }


            if (line.contains("No flight")) {
                dateFound = false;
            } else if (startWithWeekDay(line)) {
                try {
                    flightDate = getFlightDate(line);
                    f = new Flight();
                    f.setFetchedDate(fetchedDate);
                    f.setFromm(from);
                    f.setTo(to);
                    f.setFlightDate(flightDate);
                    dateFound = true;
                } catch (ParseException e) {
                    log.error("Error parsing line={}", line);
                }
            }
        }
        return flights;

    }

    private BigDecimal getPriceInRON(String price) {
        //TODO add conversion rate
        return BigDecimal.valueOf(Double.valueOf(price.replaceAll("[^\\d.]", "")));
    }


    private Date getFlightDate(String line) throws ParseException {
        String[] dayDate = line.split(",");
        String date = dayDate[1].trim();
        date = date + " " + LocalDate.now().getYear();
        return flightDateFormat.parse(date);
    }

    private boolean startWithWeekDay(String line) {
        return line.startsWith("Mon") || line.startsWith("Tue") || line.startsWith("Wed") || line.startsWith("Thu")
                || line.startsWith("Fri") || line.startsWith("Sat") || line.startsWith("Sun");
    }

}
