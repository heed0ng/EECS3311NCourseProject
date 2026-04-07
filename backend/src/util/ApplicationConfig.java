package backend.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import backend.observer.AdminObserver;
import backend.observer.ClientObserver;
import backend.observer.ConsultantObserver;
import backend.observer.EventPublisher;
import backend.paymentStrategy.PaymentStrategyFactory;
import backend.repository.*;
import backend.repository.sqlite.*;
import backend.service.*;
import backend.service.impl.*;
import backend.ui.TerminalUI;
import backend.ai.GroqChatClient;

@Configuration
public class ApplicationConfig {

    @Bean
    public DatabaseManager databaseManager() {
    		String databaseFilePath = DatabasePaths.databaseFilePath();
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
            SqliteClientRepository clientRepository) {
        return new SqliteSavedPaymentMethodRepository(databaseManager, clientRepository);
    }

    @Bean
    public PaymentTransactionRepository paymentTransactionRepository(
            DatabaseManager databaseManager,
            SqliteBookingRepository bookingRepository,
            SqliteClientRepository clientRepository) {
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
    
    @Bean
    public ClientAssistantService clientAssistantService(
            BookingService bookingService,
            PolicyRepository policyRepository,
            GroqChatClient groqChatClient) {

        return new DefaultClientAssistantService(
                bookingService,
                policyRepository,
                groqChatClient);
    }
    
@Bean
public CommandLineRunner initializeDemoDataAndObservers(
        EventPublisher eventPublisher,
        AdminRepository adminRepository,
        ClientRepository clientRepository,
        ConsultantRepository consultantRepository) {

    return args -> {
        TerminalUI terminalUI = new TerminalUI();
        terminalUI.seedDemoDataIfNeeded();

        for (var currentConsultant : consultantRepository.findAll()) {
            eventPublisher.subscribe(new ConsultantObserver("observer-consultant-" + currentConsultant.getUserId(), currentConsultant.getName()));
        }

        for (var currentClient : clientRepository.findAll()) {
            eventPublisher.subscribe(new ClientObserver("observer-client-" + currentClient.getUserId(), currentClient.getName()));
        }

        for (var currentAdmin : adminRepository.findAll()) {
            eventPublisher.subscribe(new AdminObserver("observer-admin-" + currentAdmin.getUserId(), currentAdmin.getName()));
        }
    };
}
    
    @Bean
    public GroqChatClient groqChatClient() {
        String apiKey = System.getenv("GROQ_API_KEY");
        String model = System.getenv("GROQ_MODEL");
        return new GroqChatClient(apiKey, model);
    }
    
}