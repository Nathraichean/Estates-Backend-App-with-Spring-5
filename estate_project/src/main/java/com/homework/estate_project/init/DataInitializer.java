package com.homework.estate_project.init;

import com.homework.estate_project.dao.AgencyRepository;
import com.homework.estate_project.dao.ImageDataRepository;
import com.homework.estate_project.dao.UserRepository;
import com.homework.estate_project.entity.Agency;
import com.homework.estate_project.entity.Estate;
import com.homework.estate_project.entity.User;
import com.homework.estate_project.entity.enums.*;
import com.homework.estate_project.service.AgencyService;
import com.homework.estate_project.service.EstateService;
import com.homework.estate_project.service.ImageDataService;
import com.homework.estate_project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    private static UserService userService;
    private static UserRepository userRepo;
    private static AgencyService agencyService;
    private static AgencyRepository agencyRepo;
    private static ImageDataService imageDataService;
    private static EstateService estateService;
    private static ImageDataRepository imageDataRepo;

    @Autowired
    public DataInitializer(UserService userService,
                           AgencyService agencyService,
                           AgencyRepository agencyRepo,
                           ImageDataService imageDataService,
                           EstateService estateService,
                           ImageDataRepository imageDataRepo,
                           UserRepository userRepo) {
        this.userService = userService;
        this.agencyService = agencyService;
        this.agencyRepo = agencyRepo;
        this.imageDataService = imageDataService;
        this.estateService = estateService;
        this.imageDataRepo = imageDataRepo;
        this.userRepo = userRepo;
    }

    private static final List<User> SAMPLE_USERS = List.of(
            new User("Svetozar", "Blazhev", "sblazhev","sblazhev@abv.bg", "Admin123!", UserRole.ADMIN),
            new User("George", "Bush", "gbush","gbush@abv.bg", "President123!", UserRole.STANDARD),
            new User("Milo", "Kochran", "mkochran","mkochran@abv.bg", "Mkochran123!", UserRole.STANDARD),
            new User("Kellan", "Townsend", "ktownsend","ktownsend@abv.bg", "Ktownsend123!", UserRole.STANDARD),
            new User("Tyreke", "Macfarlane", "tmacfarlane","tmacfarlane@abv.bg", "Tmacfarlane123!", UserRole.STANDARD),
            new User("Ronaldo", "Cano", "rcano55","rcano@abv.bg", "Rcano123!", UserRole.STANDARD),
            new User("Imoten", "Prodavach", "prodavach","prodavach@abv.bg", "Prodavam123!", UserRole.BROKER),
            new User("Gospodin", "Broker", "broker", "broker@abv.bg","Broker123!", UserRole.BROKER),
            new User("Rhonda", "Mccartney", "rmccartney", "rmccartney@gmail.com","Rmccartney123!", UserRole.BROKER),
            new User("Velma", "Whelan", "vwhelan", "vwhelan@abv.bg","Vwhelan123!", UserRole.BROKER),
            new User("Catrina", "Whitaker", "cwhitaker", "cwhitaker@abv.bg","Cwhitaker123!", UserRole.BROKER),
            new User("Natalie", "Beattie", "nbeattie", "nbeattie@abv.bg","Nbeattie123!", UserRole.BROKER),
            new User("Shtedur", "Paraliya", "paraliya", "paraliya@abv.bg","Platen123!", UserRole.PREMIUM),
            new User("Douglas", "Frederick", "dfrederick", "dfrederick@abv.bg","Dfrederick123!", UserRole.PREMIUM),
            new User("Jacqueline", "Cullen", "jcullen", "jcullen@abv.bg","Jcullen123!", UserRole.PREMIUM),
            new User("Shantelle", "Mercer", "smercer", "smercer@abv.bg","Smercer123!", UserRole.PREMIUM),
            new User("Bezizhoden", "Kupuvach", "grajdanin", "grajdanin@abv.bg","Kupuvam123!", UserRole.PREMIUM)
    );

    private static final List<Estate> SAMPLE_ESTATES = List.of(
            new Estate(SAMPLE_USERS.get(0),"This is a house estate in sofia lozenets.",
                    Location.SOFIA_LOZENETS, "ul. Cherni Vrah", 15, 500000F,
                    165, 165, 0, 2,EstateType.HOUSE, EstateFloorType.MID_FLOOR,
                    GenericStatus.ACTIVE,1995, EstateBuildType.BRICK, new ArrayList<>(
                            List.of(EstateFeature.GARAGE,EstateFeature.BALCONY,EstateFeature.CITY_VIEW,EstateFeature.HYPERMARKET,
                                    EstateFeature.SOUTHEAST,EstateFeature.METRO))),
            new Estate(SAMPLE_USERS.get(1), "This is a small two room apartment in Mladost 4.",
                    Location.SOFIA_MLADOST4, "ul. Doktor Atanas Moskov", 115, 75000F,
                    60, 50, 10, 5,EstateType.TWO_ROOM, EstateFloorType.MID_FLOOR,
                    GenericStatus.PENDING, 1985, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.CITY_VIEW,EstateFeature.HYPERMARKET,
                            EstateFeature.WEST,EstateFeature.METRO,EstateFeature.CENTRAL_HEATING))),
            new Estate(SAMPLE_USERS.get(2), "This is an attic in the center of Sofia. Its newly built and perfect for a young adult due to its proximity to the city's heart.",
                    Location.SOFIA_CENTAR, "bul. Aleksandar Stamboliyski", 30, 110000F,
                    46,  46, 0, 8,EstateType.WORKSHOP_ATTIC, EstateFloorType.LAST_FLOOR,
                    GenericStatus.SUSPENDED, 1950, EstateBuildType.BRICK, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.CITY_VIEW,EstateFeature.CENTRAL_HEATING,EstateFeature.HYPERMARKET,
                            EstateFeature.NORTH,EstateFeature.METRO,EstateFeature.ELEVATOR,EstateFeature.SLOPED_CEILINGS))),
            new Estate(SAMPLE_USERS.get(3), "A medium sized three room apartment in arguably one of the best neighborhoods in town.",
                    Location.GABROVO_SIRMANI, "ul. Mirni Dni", 12, 2499000.99F,
                    64,64, 0,2,EstateType.THREE_ROOM, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD,1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(4), "n/a", Location.SOFIA_ABDOVITSA, "n/a",
                    12, 2499000,64, 64, 0, 2,EstateType.OFFICE, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(5), "n/a", Location.SOFIA_DRUZBA_2, "n/a",
                    1, 100000F,50, 45, 5, 2,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(6), "n/a", Location.SOFIA_BANISHORA, "n/a",
                    12, 200000,30, 25, 5, 1,EstateType.COMMERCIAL, EstateFloorType.FIRST_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.ASSEMBLED, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(7), "n/a", Location.SOFIA_BOROVO, "n/a",
                    12, 300000,40, 35, 5, 3,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(8), "n/a", Location.SOFIA_BOYANA, "n/a",
                    12, 50000,45, 40, 5, 4,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(9), "n/a", Location.SOFIA_DIANABAD, "n/a",
                    12, 25000,50, 45, 5, 5,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(9), "n/a", Location.SOFIA_CHELOPECHENE, "n/a",
                    12, 359000,55, 50, 5, 6,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(10), "n/a", Location.SOFIA_HLADILNIKA, "n/a",
                    12, 65000,60, 55, 5, 7,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(2), "n/a", Location.SOFIA_MALASHEVTSI, "n/a",
                    12, 70000,65, 60, 5, 8,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(11), "n/a", Location.SOFIA_MLADOST4, "n/a",
                    12, 80000,70, 65, 5, 9,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(12), "n/a", Location.SOFIA_ILIYANCI, "n/a",
                    12, 90000,75, 70, 5, 10,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(13), "n/a", Location.SOFIA_LAGERA, "n/a",
                    12, 85000,80, 75, 5, 4,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(1), "n/a", Location.SOFIA_MLADOST1A, "n/a",
                    12, 74000,85, 80, 5, 5,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(2), "n/a", Location.GABROVO_SIRMANI, "n/a",
                    12, 55000,90, 85, 5, 6,EstateType.COMMERCIAL, EstateFloorType.MID_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH))),
            new Estate(SAMPLE_USERS.get(1), "n/a", Location.GABROVO_ETARA, "n/a",
                    12, 61000,95, 90, 5, 8,EstateType.COMMERCIAL, EstateFloorType.LAST_FLOOR,
                    GenericStatus.SOLD, 1987, EstateBuildType.PANEL, new ArrayList<>(
                    List.of(EstateFeature.BALCONY,EstateFeature.FURNISHED,EstateFeature.NORTH,
                            EstateFeature.SOUTH)))

    );


    private static final List<Agency> SAMPLE_AGENCIES = List.of(
            new Agency(SAMPLE_USERS.get(1),"Agenciya 1","Agenciya za sofiya",GenericStatus.SUSPENDED),
            new Agency(SAMPLE_USERS.get(2),"Orange County","Only the most luxurious estates available in the country!",GenericStatus.ACTIVE),
            new Agency(SAMPLE_USERS.get(3),"Dostupen Komfort","This agency is aimed to provide great homes with a lot of features.",GenericStatus.ACTIVE),
            new Agency(SAMPLE_USERS.get(4),"Byrzi imoti","Our target market is the fast paced one. The estates we advertise are always at the most competitive pricing and thus they sell incredibly fast so think fast and act fast because our listings aren't here to stay.",GenericStatus.ACTIVE)
    );

    static final String imagesFolderPath = "D:\\Coding\\Homework projects\\estate_project\\src\\main\\java\\com\\homework\\estate_project\\init\\img\\";
    private static final List<File> SAMPLE_IMAGES = List.of(
            new File(imagesFolderPath + "noimg.jpg"),
            new File(imagesFolderPath + "агенция.jpg"),
            new File(imagesFolderPath + "брокер.jpg"),
            new File(imagesFolderPath + "имот1.jpg"),
            new File(imagesFolderPath + "имот2.jpg"),
            new File(imagesFolderPath + "имот3.jpg"),
            new File(imagesFolderPath + "имот4.jpg"),
            new File(imagesFolderPath + "потребител.jpg")
    );




    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (File file : SAMPLE_IMAGES) {
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("image",
                    file.getName(), "image/jpg", IOUtils.toByteArray(input));
            imageDataService.uploadImage(multipartFile, -10L, -15L, -30L);
        }
        if(userService.getUsersCount() == 0) {
            SAMPLE_USERS.forEach(userService::create);
            log.info("Sample users created: {}", userService.getAllUsers());
        }
        if(agencyService.getAgencyCount() == 0) {
            SAMPLE_AGENCIES.forEach(agencyService::createAgency);
        }
        if(estateService.getEstateCount() == 0){
            SAMPLE_ESTATES.forEach(estateService::createEstate);
        }
    }
}
