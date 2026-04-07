package backend.util;

import backend.repository.*;
import backend.ui.TerminalUI;

public class DemoDataInitializer {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository consultantServiceOfferingRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingRepository bookingRepository;
    private final SavedPaymentMethodRepository savedPaymentMethodRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;

    public DemoDataInitializer(
        AdminRepository adminRepository, ClientRepository clientRepository, ConsultantRepository consultantRepository, 
        ConsultingServiceRepository consultingServiceRepository, ConsultantServiceOfferingRepository consultantServiceOfferingRepository,
        AvailabilitySlotRepository availabilitySlotRepository, BookingRepository bookingRepository,
        SavedPaymentMethodRepository savedPaymentMethodRepository, PaymentTransactionRepository paymentTransactionRepository,
        PolicyRepository policyRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.consultantRepository = consultantRepository;
        this.consultingServiceRepository = consultingServiceRepository;
        this.consultantServiceOfferingRepository = consultantServiceOfferingRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingRepository = bookingRepository;
        this.savedPaymentMethodRepository = savedPaymentMethodRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.policyRepository = policyRepository;
    }

    
    public void seedDemoDataIfNeeded() {
        TerminalUI ui = new TerminalUI();
    	ui.seedDemoClientsIfMissing();
        ui.seedDemoConsultantsIfMissing();
        ui.seedDemoServicesIfMissing();
        ui.seedDemoOfferingsIfMissing();
        ui.seedDemoSlotsIfMissing();
        ui.seedDemoPaymentMethodsIfMissing();
        ui.insertAdminIfMissing("admin-1", "System Admin", "admin@example.com");
    }
}