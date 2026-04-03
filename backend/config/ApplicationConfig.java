package backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import observer.EventPublisher;
import paymentStrategy.PaymentStrategyFactory;
import repository.AdminRepository;
import repository.AvailabilitySlotRepository;
import repository.BookingRepository;
import repository.ClientRepository;
import repository.ConsultantRepository;
import repository.ConsultantServiceOfferingRepository;
import repository.ConsultingServiceRepository;
import repository.PaymentTransactionRepository;
import repository.PolicyRepository;
import repository.SavedPaymentMethodRepository;
import repository.sqlite.DatabaseManager;
import repository.sqlite.IdGenerator;
import repository.sqlite.SchemaInitializer;
import repository.sqlite.SqliteAdminRepository;
import repository.sqlite.SqliteAvailabilitySlotRepository;
import repository.sqlite.SqliteBookingRepository;
import repository.sqlite.SqliteClientRepository;
import repository.sqlite.SqliteConsultantRepository;
import repository.sqlite.SqliteConsultantServiceOfferingRepository;
import repository.sqlite.SqliteConsultingServiceRepository;
import repository.sqlite.SqlitePaymentTransactionRepository;
import repository.sqlite.SqlitePolicyRepository;
import repository.sqlite.SqliteSavedPaymentMethodRepository;
import service.AdminService;
import service.BookingService;
import service.ConsultantService;
import service.PaymentService;
import service.impl.DefaultAdminService;
import service.impl.DefaultBookingService;
import service.impl.DefaultConsultantService;
import service.impl.DefaultPaymentService;

@Configuration
public class ApplicationConfig {

    @Bean
    public DatabaseManager databaseManager() {
        String databaseFilePath = System.getProperty("user.dir") + "/booking_platform_phase1.db";
        DatabaseManager databaseManager = new DatabaseManager(databaseFilePath);
        new SchemaInitializer(databaseManager).initialize();
        return databaseManager;
    }

    @Bean
    public EventPublisher eventPublisher() {
        return new EventPublisher();
    }

    @Bean
    public IdGenerator idGenerator(DatabaseManager databaseManager) {
        return new IdGenerator(databaseManager);
    }

    @Bean
    public AdminRepository adminRepository(DatabaseManager databaseManager) {
        return new SqliteAdminRepository(databaseManager);
    }

    @Bean
    public ClientRepository clientRepository(DatabaseManager databaseManager) {
        return new SqliteClientRepository(databaseManager);
    }

    @Bean
    public ConsultantRepository consultantRepository(DatabaseManager databaseManager) {
        return new SqliteConsultantRepository(databaseManager);
    }

    @Bean
    public ConsultingServiceRepository consultingServiceRepository(DatabaseManager databaseManager) {
        return new SqliteConsultingServiceRepository(databaseManager);
    }

    @Bean
    public ConsultantServiceOfferingRepository offeringRepository(DatabaseManager databaseManager) {
        return new SqliteConsultantServiceOfferingRepository(databaseManager);
    }

    @Bean
    public AvailabilitySlotRepository availabilitySlotRepository(DatabaseManager databaseManager) {
        return new SqliteAvailabilitySlotRepository(databaseManager);
    }

    @Bean
    public BookingRepository bookingRepository(DatabaseManager databaseManager) {
        return new SqliteBookingRepository(databaseManager);
    }

    @Bean
    public SavedPaymentMethodRepository savedPaymentMethodRepository(
            DatabaseManager databaseManager,
            ClientRepository clientRepository) {
        return new SqliteSavedPaymentMethodRepository(databaseManager, clientRepository);
    }

    @Bean
    public PaymentTransactionRepository paymentTransactionRepository(
            DatabaseManager databaseManager,
            BookingRepository bookingRepository,
            ClientRepository clientRepository) {
        return new SqlitePaymentTransactionRepository(databaseManager, bookingRepository, clientRepository);
    }

    @Bean
    public PolicyRepository policyRepository(DatabaseManager databaseManager) {
        return new SqlitePolicyRepository(databaseManager);
    }

    @Bean
    public BookingService bookingService(
            ClientRepository clientRepository,
            ConsultantServiceOfferingRepository offeringRepository,
            AvailabilitySlotRepository availabilitySlotRepository,
            BookingRepository bookingRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher,
            IdGenerator idGenerator) {

        return new DefaultBookingService(
                clientRepository,
                offeringRepository,
                availabilitySlotRepository,
                bookingRepository,
                paymentTransactionRepository,
                policyRepository,
                eventPublisher,
                idGenerator);
    }

    @Bean
    public ConsultantService consultantService(
            ConsultantRepository consultantRepository,
            ConsultingServiceRepository consultingServiceRepository,
            ConsultantServiceOfferingRepository offeringRepository,
            AvailabilitySlotRepository availabilitySlotRepository,
            BookingRepository bookingRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher,
            IdGenerator idGenerator) {

        return new DefaultConsultantService(
                consultantRepository,
                consultingServiceRepository,
                offeringRepository,
                availabilitySlotRepository,
                bookingRepository,
                paymentTransactionRepository,
                policyRepository,
                eventPublisher,
                idGenerator);
    }

    @Bean
    public PaymentService paymentService(
            ClientRepository clientRepository,
            BookingRepository bookingRepository,
            SavedPaymentMethodRepository savedPaymentMethodRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher,
            IdGenerator idGenerator) {

        return new DefaultPaymentService(
                clientRepository,
                bookingRepository,
                savedPaymentMethodRepository,
                paymentTransactionRepository,
                policyRepository,
                eventPublisher,
                new PaymentStrategyFactory(),
                idGenerator);
    }

    @Bean
    public AdminService adminService(
            AdminRepository adminRepository,
            ConsultantRepository consultantRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher) {

        return new DefaultAdminService(
                adminRepository,
                consultantRepository,
                policyRepository,
                eventPublisher);
    }
}