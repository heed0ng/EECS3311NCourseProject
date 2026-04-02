package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import model.core.*;
import model.payment.*;
import model.user.*;
import observer.*;
import paymentStrategy.PaymentStrategyFactory;
import repository.*;
import repository.sqlite.*;
import util.*;
import service.*;
import service.impl.*;

public class TerminalUI {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner = new Scanner(System.in);
    private final DatabaseManager databaseManager;
    private final EventPublisher eventPublisher;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final SavedPaymentMethodRepository savedPaymentMethodRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;
    private final BookingService bookingService;
    private final ConsultantService consultantService;
    private final PaymentService paymentService;
    private final AdminService adminService;
    private final AdminRepository adminRepository;

    public TerminalUI() {
        String databaseFilePath = System.getProperty("user.dir") + "/booking_platform_phase1.db";
        this.databaseManager = new DatabaseManager(databaseFilePath);
        new SchemaInitializer(databaseManager).initialize();
        this.eventPublisher = new EventPublisher();
        SqliteAdminRepository sqliteAdminRepository = new SqliteAdminRepository(databaseManager);
        SqliteClientRepository sqliteClientRepository = new SqliteClientRepository(databaseManager);
        SqliteConsultantRepository sqliteConsultantRepository = new SqliteConsultantRepository(databaseManager);
        SqliteConsultingServiceRepository sqliteConsultingServiceRepository = new SqliteConsultingServiceRepository(databaseManager);
        SqliteConsultantServiceOfferingRepository sqliteOfferingRepository = new SqliteConsultantServiceOfferingRepository(databaseManager);
        SqliteAvailabilitySlotRepository sqliteSlotRepository = new SqliteAvailabilitySlotRepository(databaseManager);
        SqliteBookingRepository sqliteBookingRepository = new SqliteBookingRepository(databaseManager);
        SqliteSavedPaymentMethodRepository sqliteSavedPaymentMethodRepository = new SqliteSavedPaymentMethodRepository(databaseManager, sqliteClientRepository);
        SqlitePaymentTransactionRepository sqlitePaymentTransactionRepository = new SqlitePaymentTransactionRepository(databaseManager, sqliteBookingRepository, sqliteClientRepository);
        SqlitePolicyRepository sqlitePolicyRepository = new SqlitePolicyRepository(databaseManager);
        IdGenerator idGenerator = new IdGenerator(this.databaseManager);
        
        this.clientRepository = sqliteClientRepository;
        this.consultantRepository = sqliteConsultantRepository;
        this.consultingServiceRepository = sqliteConsultingServiceRepository;
        this.offeringRepository = sqliteOfferingRepository;
        this.slotRepository = sqliteSlotRepository;
        this.bookingRepository = sqliteBookingRepository;
        this.savedPaymentMethodRepository = sqliteSavedPaymentMethodRepository;
        this.paymentTransactionRepository = sqlitePaymentTransactionRepository;
        this.policyRepository = sqlitePolicyRepository;
        this.adminRepository = sqliteAdminRepository;
        
        this.bookingService = new DefaultBookingService(clientRepository, offeringRepository, slotRepository, bookingRepository, paymentTransactionRepository, policyRepository, eventPublisher, idGenerator);
        this.consultantService = new DefaultConsultantService(consultantRepository, consultingServiceRepository, offeringRepository, slotRepository, bookingRepository, policyRepository, eventPublisher, idGenerator);
        this.paymentService = new DefaultPaymentService(clientRepository, bookingRepository, savedPaymentMethodRepository, paymentTransactionRepository, policyRepository, eventPublisher, new PaymentStrategyFactory(), idGenerator);
        this.adminService = new DefaultAdminService(adminRepository, consultantRepository, policyRepository, eventPublisher);
    }
    
    public static void main(String[] args) {
        new TerminalUI().run();
    }

    public void run() {
        seedDemoDataIfNeeded();
        subscribeDefaultObservers();
        System.out.println("Service Booking and Consulting Platform - Phase 1 Backend Demo");
        System.out.println("Database file: booking_platform_phase1.db");
        printDemoIds();
        while (true) {
            System.out.println("\nMain Menu");
            System.out.println("1. Client Menu");
            System.out.println("2. Consultant Menu");
            System.out.println("3. Admin Menu");
            System.out.println("4. Show Demo IDs and Data");
            System.out.println("0. Exit");
            String choice = prompt("Choose: ");
            try {
                switch (choice) {
                    case "1": clientMenu(); break;
                    case "2": consultantMenu(); break;
                    case "3": adminMenu(); break;
                    case "4": printDemoIds(); break;
                    case "0": System.out.println("Exiting."); return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void clientMenu() {
        String clientId = prompt("Enter client ID: ");
        while (true) {
            System.out.println("\nClient Menu");
            System.out.println("1. Browse active offerings");
            System.out.println("2. View all available slots");
            System.out.println("3. Request booking");
            System.out.println("4. View booking history");
            System.out.println("5. Check cancellation summary");
            System.out.println("6. Cancel booking");
            System.out.println("7. Add saved payment method");
            System.out.println("8. Update saved payment method");
            System.out.println("9. Remove saved payment method");
            System.out.println("10. View saved payment methods");
            System.out.println("11. Process payment for booking");
            System.out.println("12. View payment history");
            System.out.println("0. Back");
            String choice = prompt("Choose: ");
            switch (choice) {
                case "1": printOfferings(bookingService.browseAvailableOfferings()); break;
                case "2": printSlots(bookingService.getAllAvailableSlots()); break;
                case "3": {
                    String offeringId = prompt("Offering ID: ");
                    String slotId = prompt("Slot ID: ");
                    Booking booking = bookingService.requestBooking(clientId, offeringId, slotId);
                    System.out.println("Created booking: " + booking.getBookingId() + " state=" + booking.getStateName());
                    break;
                }
                case "4": printBookings(bookingService.getBookingHistory(clientId)); break;
                case "5": System.out.println(bookingService.getCancellationSummary(clientId, prompt("Booking ID: "))); break;
                case "6": {
                    Booking booking = bookingService.cancelBooking(clientId, prompt("Booking ID: "));
                    System.out.println("Cancelled booking: " + booking.getBookingId());
                    break;
                }
                case "7": addPaymentMethod(clientId); break;
                case "8": updatePaymentMethod(clientId); break;
                case "9": removePaymentMethod(clientId); break;
                case "10": printPaymentMethods(paymentService.getSavedPaymentMethods(clientId)); break;
                case "11": {
                    PaymentTransaction tx = paymentService.processPayment(clientId, prompt("Booking ID: "), prompt("Saved Method ID: "));
                    System.out.println("Payment transaction successful: " + tx.getTransactionId());
                    break;
                }
                case "12": printPaymentTransactions(paymentService.getPaymentHistory(clientId)); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void consultantMenu() {
        String consultantId = prompt("Enter consultant ID: ");
        while (true) {
            System.out.println("\nConsultant Menu");
            System.out.println("1. View my slots");
            System.out.println("2. Add availability slot");
            System.out.println("3. Add service offering");
            System.out.println("4. View pending booking requests");
            System.out.println("5. Accept booking request");
            System.out.println("6. Reject booking request");
            System.out.println("7. Complete paid booking");
            System.out.println("0. Back");
            String choice = prompt("Choose: ");
            switch (choice) {
                case "1": printSlots(consultantService.getAvailabilitySlots(consultantId)); break;
                case "2": {
                    LocalDateTime start = parseDateTime(prompt("Start (yyyy-MM-dd HH:mm): "));
                    LocalDateTime end = parseDateTime(prompt("End (yyyy-MM-dd HH:mm): "));
                    AvailabilitySlot slot = consultantService.addAvailabilitySlot(consultantId, start, end);
                    System.out.println("Added slot: " + slot.getSlotId());
                    break;
                }
                case "3": {
                    listServices();
                    String serviceId = prompt("Service ID: ");
                    String custom = prompt("Custom price (blank for base price): ");
                    Double customPrice = custom.isBlank() ? null : Double.parseDouble(custom);
                    ConsultantServiceOffering offering = consultantService.addServiceOffering(consultantId, serviceId, customPrice);
                    System.out.println("Added offering: " + offering.getOfferingId());
                    break;
                }
                case "4": printBookings(consultantService.getPendingBookingRequests(consultantId)); break;
                case "5": {
                    Booking booking = consultantService.acceptBookingRequest(consultantId, prompt("Booking ID: "));
                    System.out.println("Accepted booking. New state: " + booking.getStateName());
                    break;
                }
                case "6": {
                    Booking booking = consultantService.rejectBookingRequest(consultantId, prompt("Booking ID: "));
                    System.out.println("Rejected booking. New state: " + booking.getStateName());
                    break;
                }
                case "7": {
                    Booking booking = consultantService.completeBooking(consultantId, prompt("Booking ID: "));
                    System.out.println("Completed booking. New state: " + booking.getStateName());
                    break;
                }
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void adminMenu() {
        String adminId = prompt("Enter admin ID: ");
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1. View pending consultants");
            System.out.println("2. Approve consultant");
            System.out.println("3. Reject consultant");
            System.out.println("4. View policies");
            System.out.println("5. Update cancellation policy");
            System.out.println("6. Update refund policy");
            System.out.println("7. Update pricing policy");
            System.out.println("8. Update notification policy");
            System.out.println("0. Back");
            String choice = prompt("Choose: ");
            switch (choice) {
                case "1": printConsultants(adminService.getPendingConsultants()); break;
                case "2": System.out.println("Approved: " + adminService.approveConsultant(adminId, prompt("Consultant ID: ")).getName()); break;
                case "3": System.out.println("Rejected: " + adminService.rejectConsultant(adminId, prompt("Consultant ID: ")).getName()); break;
                case "4": printPolicies(); break;
                case "5": System.out.println("Updated cancellation deadline to " + adminService.updateCancellationPolicy(adminId, Integer.parseInt(prompt("Hours before booking start: "))).getCancellationDeadlineHours() + " hours."); break;
                case "6": System.out.println("Updated refund policy."); adminService.updateRefundPolicy(adminId, Double.parseDouble(prompt("Refund % before deadline: ")), Double.parseDouble(prompt("Refund % after deadline: "))); break;
                case "7": System.out.println("Updated pricing policy."); adminService.updatePricingPolicy(adminId, Boolean.parseBoolean(prompt("Allow consultant custom price (true/false): "))); break;
                case "8": updateNotificationPolicy(adminId); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void updateNotificationPolicy(String adminId) {
        adminService.updateNotificationPolicy(adminId,
                Boolean.parseBoolean(prompt("Notify on booking requested (true/false): ")),
                Boolean.parseBoolean(prompt("Notify on booking accepted (true/false): ")),
                Boolean.parseBoolean(prompt("Notify on booking rejected (true/false): ")),
                Boolean.parseBoolean(prompt("Notify on payment processed (true/false): ")),
                Boolean.parseBoolean(prompt("Notify on booking cancelled (true/false): ")),
                Boolean.parseBoolean(prompt("Notify on consultant approval decision (true/false): ")));
        System.out.println("Updated notification policy.");
    }

    private void addPaymentMethod(String clientId) {
        System.out.println("Payment method types: 1.CREDIT_CARD 2.DEBIT_CARD 3.PAYPAL 4.BANK_TRANSFER");
        String choice = prompt("Choose method type: ");
        PaymentMethodType type;
        switch (choice) {
            case "1": type = PaymentMethodType.CREDIT_CARD; break;
            case "2": type = PaymentMethodType.DEBIT_CARD; break;
            case "3": type = PaymentMethodType.PAYPAL; break;
            case "4": type = PaymentMethodType.BANK_TRANSFER; break;
            default: throw new IllegalArgumentException("Invalid payment method type.");
        }
        String displayLabel = prompt("Display label: ");
        SavedPaymentMethod method;

        switch (type) {
            case CREDIT_CARD:
            case DEBIT_CARD: {
                String cardNumber = prompt("Card number (16 digits): ");
                String expiry = prompt("Expiry (MM/YY): ");
                String cvv = prompt("CVV (3 or 4 digits): ");
                method = paymentService.addSavedPaymentMethod(clientId, type, displayLabel, cardNumber, expiry + "|" + cvv);
                break;
            }
            case PAYPAL: {
                String email = prompt("PayPal email: ");
                method = paymentService.addSavedPaymentMethod(clientId, type, displayLabel, email, "");
                break;
            }
            case BANK_TRANSFER: {
                String accountNumber = prompt("Account number (6 to 17 digits): ");
                String routingNumber = prompt("Routing number (9 digits): ");
                method = paymentService.addSavedPaymentMethod(clientId, type, displayLabel, accountNumber, routingNumber);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported payment method type.");
        }
        System.out.println("Saved method ID: " + method.getSavedMethodId());
    }
    
    private void updatePaymentMethod(String clientId) {
        String savedMethodId = prompt("Saved Method ID: ");
        List<SavedPaymentMethod> methods = paymentService.getSavedPaymentMethods(clientId);

        SavedPaymentMethod existingMethod = methods.stream().filter(method -> method.getSavedMethodId().equals(savedMethodId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saved payment method not found for this client."));

        String displayLabel = prompt("New display label: ");

        SavedPaymentMethod updatedMethod;

        switch (existingMethod.getMethodType()) {
            case CREDIT_CARD:
            case DEBIT_CARD: {
                String cardNumber = prompt("Card number (16 digits): ");
                String expiry = prompt("Expiry (MM/YY): ");
                String cvv = prompt("CVV (3 or 4 digits): ");
                updatedMethod = paymentService.updateSavedPaymentMethod(clientId, savedMethodId, displayLabel, cardNumber, expiry + "|" + cvv);
                break;
            }
            case PAYPAL: {
                String email = prompt("PayPal email: ");
                updatedMethod = paymentService.updateSavedPaymentMethod(clientId, savedMethodId, displayLabel, email, "");
                break;
            }
            case BANK_TRANSFER: {
                String accountNumber = prompt("Account number (6 to 17 digits): ");
                String routingNumber = prompt("Routing number (9 digits): ");
                updatedMethod = paymentService.updateSavedPaymentMethod(clientId, savedMethodId, displayLabel, accountNumber, routingNumber);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported payment method type.");
        }
        System.out.println("Updated saved payment method: " + updatedMethod.getSavedMethodId());
    }
    
    private void removePaymentMethod(String clientId) {
        String savedMethodId = prompt("Saved Method ID to remove: ");
        paymentService.removeSavedPaymentMethod(clientId, savedMethodId);
        System.out.println("Removed saved payment method: " + savedMethodId);
    }

    private void seedDemoDataIfNeeded() {
        if (!clientRepository.findAll().isEmpty()) return;
        clientRepository.save(new Client("client-1", "Alice Client", "alice@example.com"));
        clientRepository.save(new Client("client-2", "Bob Client", "bob@example.com"));
        consultantRepository.save(new Consultant("consultant-1", "Charlie Consultant", "char@example.com", ConsultantApprovalStatus.APPROVED));
        consultantRepository.save(new Consultant("consultant-2", "Dr. Doom Consultant", "doom@example.com", ConsultantApprovalStatus.PENDING));
        consultingServiceRepository.save(new ConsultingService("service-1", "Software Design Consulting", "UML, patterns, architecture review.", 60, 120.0, true));
        consultingServiceRepository.save(new ConsultingService("service-2", "Career Coaching", "Interview and resume consultation.", 45, 90.0, true));
        consultingServiceRepository.save(new ConsultingService("service-3", "Medical Checkup", "Basic Medical Examination for further check", 40, 300.0, true));
        consultingServiceRepository.save(new ConsultingService("service-4", "Time Management Advise", "Weekly/mothly/yearly time management strategies setup", 55, 50.0, true));
        offeringRepository.save(new ConsultantServiceOffering("offering-1", consultantRepository.findById("consultant-1").orElseThrow(), consultingServiceRepository.findById("service-1").orElseThrow(), 140.0, true));
        offeringRepository.save(new ConsultantServiceOffering("offering-2", consultantRepository.findById("consultant-1").orElseThrow(), consultingServiceRepository.findById("service-2").orElseThrow(), null, true));
        slotRepository.save(new AvailabilitySlot("slot-1", consultantRepository.findById("consultant-1").orElseThrow(), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0), true));
        slotRepository.save(new AvailabilitySlot("slot-2", consultantRepository.findById("consultant-1").orElseThrow(), LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0), true));
        slotRepository.save(new AvailabilitySlot("slot-3", consultantRepository.findById("consultant-1").orElseThrow(), LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(2).withHour(15).withMinute(0).withSecond(0).withNano(0), true));
        paymentService.addSavedPaymentMethod("client-1", PaymentMethodType.CREDIT_CARD, "Alice Visa", "1111111111111111", "12/30|123");
        paymentService.addSavedPaymentMethod("client-1", PaymentMethodType.PAYPAL, "Alice PayPal", "alice@example.com", "");
        insertAdminIfMissing("admin-1", "System Admin", "admin@example.com");
    }

    private void subscribeDefaultObservers() {
        eventPublisher.subscribe(new AdminObserver("observer-admin-1", "System Admin"));
        eventPublisher.subscribe(new ClientObserver("observer-client-1", "Alice Client"));
        eventPublisher.subscribe(new ConsultantObserver("observer-consultant-1", "Charlie Consultant"));
    }

    private void insertAdminIfMissing(String adminId, String name, String email) {
        if (adminRepository.findById(adminId).isPresent()) {
            return;
        }
        adminRepository.save(new Admin(adminId, name, email));
    }

    private void printDemoIds() {
    	System.out.println("\nDemo IDs");
        System.out.println("Admin: admin-1 | System Admin");
        
        System.out.println("Clients: ");
        printClients(clientRepository.findAll());
        System.out.println("Consultants:");
        printConsultants(consultantRepository.findAll());
        listServices();
        printOfferings(offeringRepository.findAllActive());
        printSlots(slotRepository.findAllAvailable());
    }

    private void listServices() {
        System.out.println("Available consulting services:");
        for (ConsultingService service : consultingServiceRepository.findAllActive()) {
            System.out.println("- " + service.getServiceId() + " | " + service.getName() + " | $" + service.getBasePrice());
        }
    }

    private void printOfferings(List<ConsultantServiceOffering> offerings) {
        if (offerings.isEmpty()) {
            System.out.println("No offerings found.");
            return;
        }
        for (ConsultantServiceOffering offering : offerings) {
            System.out.println(offering.getOfferingId() + " | consultant=" + offering.getConsultant().getName() + " | service=" + offering.getConsultingService().getName() + " | price=$" + offering.getEffectivePrice());
        }
    }

    private void printSlots(List<AvailabilitySlot> slots) {
        if (slots.isEmpty()) {
            System.out.println("No slots found.");
            return;
        }
        for (AvailabilitySlot slot : slots) {
            System.out.println(slot.getSlotId() + " | consultant=" + slot.getConsultant().getName() + " | " + slot.getStartDateTime().format(DATE_TIME_FORMATTER) + " -> " + slot.getEndDateTime().format(DATE_TIME_FORMATTER) + " | available=" + slot.isAvailable());
        }
    }

    private void printBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for (Booking booking : bookings) {
            System.out.println(booking.getBookingId() + " | client=" + booking.getClient().getName() + " | consultant=" + booking.getOffering().getConsultant().getName() + " | service=" + booking.getOffering().getConsultingService().getName() + " | state=" + booking.getStateName() + " | price=$" + booking.getAgreedPrice());
        }
    }

    private void printPaymentMethods(List<SavedPaymentMethod> methods) {
        if (methods.isEmpty()) {
            System.out.println("No payment methods found.");
            return;
        }
        for (SavedPaymentMethod method : methods) {
            System.out.println(method.getSavedMethodId() + " | type=" + method.getMethodType() + " | label=" + method.getDisplayLabel() + " | payment details =" + method.getMaskedPaymentDetails());
        }
    }

    private void printPaymentTransactions(List<PaymentTransaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No payment transactions found.");
            return;
        }
        for (PaymentTransaction transaction : transactions) {
            System.out.println(transaction.getTransactionId() + " | booking=" + transaction.getBooking().getBookingId() + " | type=" + transaction.getTransactionType() + " | status=" + transaction.getStatus() + " | method=" + transaction.getMethodType() + " | amount=$" + transaction.getAmount());
        }
    }

    private void printClients(List<Client> clients) {
        for (Client client : clients) System.out.println("- " + client.getUserId() + " | " + client.getName());
    }
    
    private void printConsultants(List<Consultant> consultants) {
        if (consultants.isEmpty()) {
            System.out.println("No consultants found.");
            return;
        }
        for (Consultant consultant : consultants) {
            System.out.println(consultant.getUserId() + " | " + consultant.getName() + " | status=" + consultant.getApprovalStatus());
        }
    }

    private void printPolicies() {
        System.out.println("Cancellation deadline hours: " + adminService.getCancellationPolicy().getCancellationDeadlineHours());
        System.out.println("Refund % before deadline: " + adminService.getRefundPolicy().getRefundPercentBeforeDeadline());
        System.out.println("Refund % after deadline: " + adminService.getRefundPolicy().getRefundPercentAfterDeadline());
        System.out.println("Allow consultant custom price: " + adminService.getPricingPolicy().isAllowConsultantCustomPrice());
        System.out.println("Notify on booking requested: " + adminService.getNotificationPolicy().isNotifyOnBookingRequested());
        System.out.println("Notify on booking accepted: " + adminService.getNotificationPolicy().isNotifyOnBookingAccepted());
        System.out.println("Notify on booking rejected: " + adminService.getNotificationPolicy().isNotifyOnBookingRejected());
        System.out.println("Notify on payment processed: " + adminService.getNotificationPolicy().isNotifyOnPaymentProcessed());
        System.out.println("Notify on booking cancelled: " + adminService.getNotificationPolicy().isNotifyOnBookingCancelled());
        System.out.println("Notify on consultant approval decision: " + adminService.getNotificationPolicy().isNotifyOnConsultantApprovalDecision());
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private LocalDateTime parseDateTime(String input) {
        return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
    }
}
