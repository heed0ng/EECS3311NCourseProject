package backend.util;

import java.time.LocalDateTime;

import backend.model.core.AvailabilitySlot;
import backend.model.core.ConsultantServiceOffering;
import backend.model.core.ConsultingService;
import backend.model.user.Admin;
import backend.model.user.Client;
import backend.model.user.Consultant;
import backend.repository.AdminRepository;
import backend.repository.AvailabilitySlotRepository;
import backend.repository.ClientRepository;
import backend.repository.ConsultantRepository;
import backend.repository.ConsultantServiceOfferingRepository;
import backend.repository.ConsultingServiceRepository;
import backend.service.PaymentService;

public class DemoDataInitializer {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository consultantServiceOfferingRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final PaymentService paymentService;

    public DemoDataInitializer(AdminRepository adminRepository, ClientRepository clientRepository, ConsultantRepository consultantRepository,
    	ConsultingServiceRepository consultingServiceRepository, ConsultantServiceOfferingRepository consultantServiceOfferingRepository,
        AvailabilitySlotRepository availabilitySlotRepository,PaymentService paymentService) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.consultantRepository = consultantRepository;
        this.consultingServiceRepository = consultingServiceRepository;
        this.consultantServiceOfferingRepository = consultantServiceOfferingRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.paymentService = paymentService;
    }

    public void seedDemoDataIfNeeded() {
        seedDemoClientsIfMissing();
        seedDemoConsultantsIfMissing();
        seedDemoServicesIfMissing();
        seedDemoOfferingsIfMissing();
        seedDemoSlotsIfMissing();
        seedDemoPaymentMethodsIfMissing();
        insertAdminIfMissing("admin-1", "System Admin", "admin@example.com");
    }

    public void seedDemoClientsIfMissing() {
        if (this.clientRepository.findById("client-1").isEmpty()) this.clientRepository.save(new Client("client-1", "Alice Client", "alice@example.com"));
        if (this.clientRepository.findById("client-2").isEmpty()) this.clientRepository.save(new Client("client-2", "Bob Client", "bob@example.com"));
    }

    public void seedDemoConsultantsIfMissing() {
        if (this.consultantRepository.findById("consultant-1").isEmpty()) {
            this.consultantRepository.save(new Consultant("consultant-1", "Charlie Consultant", "char@example.com", ConsultantApprovalStatus.APPROVED));
        }
        if (this.consultantRepository.findById("consultant-2").isEmpty()) {
            this.consultantRepository.save(new Consultant("consultant-2", "Dr. Doom Consultant", "doom@example.com", ConsultantApprovalStatus.PENDING));
        }
        if (this.consultantRepository.findById("consultant-3").isEmpty()) {
            this.consultantRepository.save(new Consultant("consultant-3", "Eric Consultant", "eric@example.com", ConsultantApprovalStatus.PENDING));
        }
        if (this.consultantRepository.findById("consultant-4").isEmpty()) {
            this.consultantRepository.save(new Consultant("consultant-4", "Fred Consultant", "fred@example.com", ConsultantApprovalStatus.PENDING));
        }
    }

    public void seedDemoServicesIfMissing() {
        if (this.consultingServiceRepository.findById("service-1").isEmpty()) {
            this.consultingServiceRepository.save(new ConsultingService("service-1", "Software Design Consulting", "UML, patterns, architecture review.", 60, 120.0, true));
        }
        if (this.consultingServiceRepository.findById("service-2").isEmpty()) {
            this.consultingServiceRepository.save(new ConsultingService("service-2", "Career Coaching", "Interview and resume consultation.", 45, 90.0, true));
        }
        if (this.consultingServiceRepository.findById("service-3").isEmpty()) {
            this.consultingServiceRepository.save(new ConsultingService("service-3", "Medical Checkup", "Basic Medical Checkup routine.", 50, 500.0, true));
        }
        if (this.consultingServiceRepository.findById("service-4").isEmpty()) {
            this.consultingServiceRepository.save(new ConsultingService("service-4", "Time Management Coaching", "Weekly/Monthly/Yearly Time managing strategy setup.", 30, 60.0, true));
        }
    }

    public void seedDemoOfferingsIfMissing() {
        Consultant consultant = this.consultantRepository.findById("consultant-1").orElseThrow(() -> new IllegalStateException("Missing seeded consultant consultant-1."));
        ConsultingService service1 = this.consultingServiceRepository.findById("service-1").orElseThrow(() -> new IllegalStateException("Missing seeded service service-1."));
        ConsultingService service2 = this.consultingServiceRepository.findById("service-2").orElseThrow(() -> new IllegalStateException("Missing seeded service service-2."));
        ConsultingService service3 = this.consultingServiceRepository.findById("service-3").orElseThrow(() -> new IllegalStateException("Missing seeded service service-3."));
        ConsultingService service4 = this.consultingServiceRepository.findById("service-4").orElseThrow(() -> new IllegalStateException("Missing seeded service service-4."));

        if (this.consultantServiceOfferingRepository.findById("offering-1").isEmpty()) {
            this.consultantServiceOfferingRepository.save(new ConsultantServiceOffering("offering-1", consultant, service1, 140.0, true));
        }
        if (this.consultantServiceOfferingRepository.findById("offering-2").isEmpty()) {
            this.consultantServiceOfferingRepository.save(new ConsultantServiceOffering("offering-2", consultant, service2, 10000.0, true));
        }
        if (this.consultantServiceOfferingRepository.findById("offering-3").isEmpty()) {
            this.consultantServiceOfferingRepository.save(new ConsultantServiceOffering("offering-3", consultant, service3, null, true));
        }
        if (this.consultantServiceOfferingRepository.findById("offering-4").isEmpty()) {
            this.consultantServiceOfferingRepository.save(new ConsultantServiceOffering("offering-4", consultant, service4, null, true));
        }
    }

    public void seedDemoSlotsIfMissing() {
        Consultant consultant = this.consultantRepository.findById("consultant-1").orElseThrow(() -> new IllegalStateException("Missing seeded consultant consultant-1."));

        LocalDateTime slot1Start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slot1End = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime slot2Start = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slot2End = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime slot3Start = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slot3End = LocalDateTime.now().plusDays(2).withHour(15).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime slot4Start = LocalDateTime.now().plusDays(3).withHour(16).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slot4End = LocalDateTime.now().plusDays(3).withHour(17).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime slot5Start = LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slot5End = LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0);

        if (this.availabilitySlotRepository.findById("slot-1").isEmpty()) {
            this.availabilitySlotRepository.save(new AvailabilitySlot("slot-1", consultant, slot1Start, slot1End, true));
        }
        if (this.availabilitySlotRepository.findById("slot-2").isEmpty()) {
            this.availabilitySlotRepository.save(new AvailabilitySlot("slot-2", consultant, slot2Start, slot2End, true));
        }
        if (this.availabilitySlotRepository.findById("slot-3").isEmpty()) {
            this.availabilitySlotRepository.save(new AvailabilitySlot("slot-3", consultant, slot3Start, slot3End, true));
        }
        if (this.availabilitySlotRepository.findById("slot-4").isEmpty()) {
            this.availabilitySlotRepository.save(new AvailabilitySlot("slot-4", consultant, slot4Start, slot4End, true));
        }
        if (this.availabilitySlotRepository.findById("slot-5").isEmpty()) {
            this.availabilitySlotRepository.save(new AvailabilitySlot("slot-5", consultant, slot5Start, slot5End, true));
        }
    }

    public void seedDemoPaymentMethodsIfMissing() {
        boolean hasAliceVisa = this.paymentService.getSavedPaymentMethods("client-1").stream()
            .anyMatch(method -> "Alice Visa".equals(method.getDisplayLabel()));
        if (!hasAliceVisa) {
            this.paymentService.addSavedPaymentMethod("client-1", PaymentMethodType.CREDIT_CARD, "Alice Visa", "1111111111111111", "12/30|123");
        }

        boolean hasAlicePaypal = this.paymentService.getSavedPaymentMethods("client-1").stream()
            .anyMatch(method -> "Alice PayPal".equals(method.getDisplayLabel()));
        if (!hasAlicePaypal) {
            this.paymentService.addSavedPaymentMethod("client-1", PaymentMethodType.PAYPAL, "Alice PayPal", "alice@example.com", "");
        }

        boolean hasAliceDebit = this.paymentService.getSavedPaymentMethods("client-1").stream()
            .anyMatch(method -> "Delete/Update Me".equals(method.getDisplayLabel()));
        if (!hasAliceDebit) {
            this.paymentService.addSavedPaymentMethod("client-1", PaymentMethodType.DEBIT_CARD, "Delete/Update Me", "2222222222222222", "12/30|123");
        }
    }

    public void insertAdminIfMissing(String adminId, String name, String email) {
        if (this.adminRepository.findById(adminId).isPresent()) return;
        this.adminRepository.save(new Admin(adminId, name, email));
    }
}