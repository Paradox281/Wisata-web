package com.altura.altura.Service;

import com.altura.altura.DTO.*;
import com.altura.altura.Model.*;
import com.altura.altura.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private DestinationRepository destinationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PromoRepository promoRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private TestimonialRepository testimonialRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.bucketName}")
    private String minioBucket;

    public DashboardResponse getDashboardData() {
        DashboardResponse response = new DashboardResponse();
        
        // Total Destinations
        response.setTotalDestinations(destinationRepository.count());
        
        // Total Users
        response.setTotalUsers(userRepository.count());
        
        // Total Bookings (jumlah booking yang Confirmed)
        response.setTotalBookings(bookingRepository.findAll().stream()
                .filter(booking -> "Confirmed".equals(booking.getStatus()))
                .count());
        
        // Total Revenue (selisih antara harga asli per booking dan harga diskon yang tersimpan di booking Confirmed)
        response.setTotalRevenue(calculateTotalRevenueForConfirmedBookings());
        
        // Destination Bookings
        List<DestinationBookingResponse> destinationBookings = destinationRepository.findAll().stream()
                .map(destination -> {
                    DestinationBookingResponse dbr = new DestinationBookingResponse();
                    dbr.setNamaDestinasi(destination.getName());
                    dbr.setJumlahBooking((int) destination.getBookings().stream()
                            .filter(booking -> "Confirmed".equals(booking.getStatus()))
                            .count());
                    return dbr;
                })
                .collect(Collectors.toList());
        response.setDestinationBookings(destinationBookings);
        
        // Booking Status
        Map<String, Double> bookingStatus = new HashMap<>();
        List<Booking> allBookings = bookingRepository.findAll();
        long totalBookings = allBookings.size();
        
        if (totalBookings > 0) {
            bookingStatus.put("Confirmed", 
                (double) allBookings.stream().filter(b -> "Confirmed".equals(b.getStatus())).count() / totalBookings * 100);
            bookingStatus.put("Cancelled", 
                (double) allBookings.stream().filter(b -> "Cancelled".equals(b.getStatus())).count() / totalBookings * 100);
            bookingStatus.put("Pending", 
                (double) allBookings.stream().filter(b -> "Pending".equals(b.getStatus())).count() / totalBookings * 100);
        }
        response.setBookingStatus(bookingStatus);
        
        return response;
    }

    private double calculateTotalRevenueForConfirmedBookings() {
        List<Booking> confirmedBookings = bookingRepository.findAll().stream()
                .filter(booking -> "Confirmed".equals(booking.getStatus()))
                .collect(Collectors.toList());

        double totalOriginalPrice = confirmedBookings.stream()
                .mapToDouble(booking -> booking.getDestination().getPrice() * booking.getTotal_persons())
                .sum();

        double totalDiscountedPrice = confirmedBookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        return totalOriginalPrice - totalDiscountedPrice;
    }

    public List<DestinationResponse> getAllDestinations() {
        return destinationRepository.findAll().stream()
                .map(destination -> {
                    DestinationResponse response = new DestinationResponse();
                    response.setId(destination.getId());
                    response.setName(destination.getName());
                    response.setLocation(destination.getLocation());
                    response.setPrice(destination.getPrice());
                    response.setQuota(destination.getQuota());
                    // Ubah imageUrl menjadi URL lengkap
                    String imageUrl = destination.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        response.setImageUrl(minioEndpoint + "/" + minioBucket + "/" + imageUrl);
                    } else {
                        response.setImageUrl(null);
                    }
                    response.setDescription(destination.getDescription());
                    
                    // Set Itinerary
                    response.setItinerary(destination.getItinerary());
                    
                    // Set Facilities
                    List<FacilityResponse> facilities = destination.getFacilities().stream()
                            .map(fd -> {
                                FacilityResponse facilityResponse = new FacilityResponse();
                                facilityResponse.setId(fd.getFacility().getId());
                                facilityResponse.setName(fd.getFacility().getName());
                                return facilityResponse;
                            })
                            .collect(Collectors.toList());
                    response.setFacilities(facilities);
                    
                    // Set Galleries
                    List<GalleryResponse> galleries = destination.getGalleries().stream()
                            .map(gallery -> {
                                GalleryResponse galleryResponse = new GalleryResponse();
                                galleryResponse.setId(gallery.getId());
                                galleryResponse.setImageUrl(gallery.getImageUrl());
                                return galleryResponse;
                            })
                            .collect(Collectors.toList());
                    response.setGalleries(galleries);
                    
                    // Set Jumlah Booking
                    Integer jumlahBooking = (int) destination.getBookings().stream()
                            .filter(booking -> "Confirmed".equals(booking.getStatus()))
                            .mapToLong(Booking::getTotal_persons)
                            .sum();
                    response.setJumlahBooking(jumlahBooking);
                    
                    // Set Promo Info
                    Optional<Promo> activePromo = promoRepository.findAll().stream()
                            .filter(promo -> promo.getDestination() != null && 
                                    promo.getDestination().getId().equals(destination.getId()) &&
                                    promo.getTanggalBerakhir().isAfter(java.time.LocalDateTime.now()))
                            .findFirst();
                    
                    if (activePromo.isPresent()) {
                        Promo promo = activePromo.get();
                        response.setPromoId(promo.getId());
                        response.setPersentaseDiskon(promo.getPersentaseDiskon());
                        response.setHargaDiskon(promo.getHargaDiskon());
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDestinationDetail(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found"));
        
        Map<String, Object> response = new HashMap<>();
        
        // Destination Info
        Map<String, Object> destinationInfo = new HashMap<>();
        destinationInfo.put("id", destination.getId());
        destinationInfo.put("name", destination.getName());
        destinationInfo.put("location", destination.getLocation());
        destinationInfo.put("price", destination.getPrice());
        destinationInfo.put("quota", destination.getQuota());
        // Ubah imageUrl menjadi URL lengkap
        String imageUrl = destination.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            destinationInfo.put("image", minioEndpoint + "/" + minioBucket + "/" + imageUrl);
        } else {
            destinationInfo.put("image", null);
        }
        destinationInfo.put("description", destination.getDescription());
        response.put("destination", destinationInfo);
        
        // Gallery Images
        List<Map<String, Object>> galleries = destination.getGalleries().stream()
                .map(gallery -> {
                    Map<String, Object> galleryMap = new HashMap<>();
                    galleryMap.put("id", gallery.getId());
                    // Ubah url menjadi URL lengkap MinIO
                    String galleryUrl = gallery.getImageUrl();
                    if (galleryUrl != null && !galleryUrl.isEmpty()) {
                        galleryMap.put("url", minioEndpoint + "/" + minioBucket + "/" + galleryUrl);
                    } else {
                        galleryMap.put("url", null);
                    }
                    return galleryMap;
                })
                .collect(Collectors.toList());
        response.put("galleryImages", galleries);
        
        // Facilities
        List<Map<String, Object>> facilities = destination.getFacilities().stream()
                .map(fd -> {
                    Facility facility = fd.getFacility();
                    Map<String, Object> facilityMap = new HashMap<>();
                    facilityMap.put("id", facility.getId());
                    facilityMap.put("facility", facility.getName());
                    return facilityMap;
                })
                .collect(Collectors.toList());
        response.put("destinationFacilities", facilities);
        
        // Promos
        List<Map<String, Object>> promos = promoRepository.findAll().stream()
                .filter(promo -> promo.getDestination() != null && promo.getDestination().getId().equals(destination.getId()))
                .map(promo -> {
                    Map<String, Object> promoMap = new HashMap<>();
                    promoMap.put("id", promo.getId());
                    promoMap.put("namaPromo", promo.getNamaDiskon());
                    promoMap.put("discount", promo.getPersentaseDiskon());
                    promoMap.put("tanggalMulai", promo.getTanggalMulai());
                    promoMap.put("tanggalBerakhir", promo.getTanggalBerakhir());
                    return promoMap;
                })
                .collect(Collectors.toList());
        response.put("destinationPromos", promos);
        
        return response;
    }

    public List<Map<String, Object>> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> {
                    Map<String, Object> bookingMap = new HashMap<>();
                    bookingMap.put("id", booking.getBookingId());
                    bookingMap.put("fullname", booking.getUser().getFullname());
                    bookingMap.put("destination", booking.getDestination().getName());
                    bookingMap.put("booking_date", booking.getBooking_date());
                    bookingMap.put("departure_date", booking.getDeparture_date());
                    bookingMap.put("return_date", booking.getReturn_date());
                    bookingMap.put("total_person", booking.getTotal_persons());
                    bookingMap.put("total_price", booking.getTotalPrice());
                    bookingMap.put("harga_asli", booking.getDestination().getPrice());
                    bookingMap.put("status", booking.getStatus());
                    // Ambil upload bukti dari payment pertama jika ada
                    var payments = paymentRepository.findByBooking_BookingId(booking.getBookingId());
                    if (!payments.isEmpty() && payments.get(0).getUploadBukti() != null) {
                        String bukti = payments.get(0).getUploadBukti();
                        String url = String.format("%s/%s/%s", minioEndpoint, minioBucket, bukti);
                        bookingMap.put("upload_bukti", url);
                    } else {
                        bookingMap.put("upload_bukti", null);
                    }
                    return bookingMap;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAllBookingsWithRevenue() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> bookings = getAllBookings();
        double totalRevenue = calculateTotalRevenueForConfirmedBookings();
        response.put("bookings", bookings);
        response.put("totalRevenue", totalRevenue);
        return response;
    }

    public Map<String, Object> getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        Map<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("id", booking.getBookingId());
        bookingMap.put("fullname", booking.getUser().getFullname());
        bookingMap.put("destination", booking.getDestination().getName());
        bookingMap.put("booking_date", booking.getBooking_date());
        bookingMap.put("departure_date", booking.getDeparture_date());
        bookingMap.put("harga_asli", booking.getDestination().getPrice());
        bookingMap.put("status", booking.getStatus());
        
        // Cek apakah ada promo aktif untuk destinasi ini
        Optional<Promo> activePromo = promoRepository.findAll().stream()
                .filter(promo -> promo.getDestination() != null && 
                        promo.getDestination().getId().equals(booking.getDestination().getId()) &&
                        promo.getTanggalBerakhir().isAfter(java.time.LocalDateTime.now()))
                .findFirst();
        
        if (activePromo.isPresent()) {
            Promo promo = activePromo.get();
            bookingMap.put("harga_diskon", promo.getHargaDiskon());
        }
        
        return bookingMap;
    }

    public Map<String, Object> getAllPromos() {
        List<Promo> promos = promoRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        
        response.put("totalPromos", promos.size());
        response.put("promoActive", promos.stream()
                .filter(promo -> promo.getTanggalBerakhir().isAfter(java.time.LocalDateTime.now()))
                .count());
        
        List<Map<String, Object>> promoList = promos.stream()
                .map(promo -> {
                    Map<String, Object> promoMap = new HashMap<>();
                    promoMap.put("id", promo.getId());
                    promoMap.put("nama", promo.getNamaDiskon());
                    promoMap.put("tanggalMulai", promo.getTanggalMulai());
                    promoMap.put("tanggalBerakhir", promo.getTanggalBerakhir());
                    promoMap.put("persentaseDiskon", promo.getPersentaseDiskon());
                    promoMap.put("hargaDiskon", promo.getHargaDiskon());
                    return promoMap;
                })
                .collect(Collectors.toList());
        response.put("promoList", promoList);
        
        return response;
    }

    public List<Map<String, Object>> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(facility -> {
                    Map<String, Object> facilityMap = new HashMap<>();
                    facilityMap.put("id", facility.getId());
                    facilityMap.put("name", facility.getName());
                    return facilityMap;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getUserId());
                    userMap.put("fullname", user.getFullname());
                    userMap.put("email", user.getEmail());
                    
                    List<Booking> userBookings = bookingRepository.findAll().stream()
                            .filter(booking -> booking.getUser().getUserId().equals(user.getUserId()))
                            .collect(Collectors.toList());
                    
                    long totalBookings = userBookings.size();
                    double totalSpent = userBookings.stream()
                            .mapToDouble(Booking::getTotalPrice)
                            .sum();
                    
                    userMap.put("totalBookings", totalBookings);
                    userMap.put("totalSpent", totalSpent);
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAllUsersWithRevenue() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> users = getAllUsers();
        double totalRevenue = calculateTotalRevenueForConfirmedBookings();
        response.put("users", users);
        response.put("totalRevenue", totalRevenue);
        return response;
    }

    public Map<String, Object> getAllTestimonials() {
        List<Testimonial> testimonials = testimonialRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        
        response.put("totalTestimonial", testimonials.size());
        response.put("averageRating", testimonials.stream()
                .mapToDouble(Testimonial::getRating)
                .average()
                .orElse(0.0));
        
        List<Map<String, Object>> testimonialList = testimonials.stream()
                .map(testimonial -> {
                    Map<String, Object> testimonialMap = new HashMap<>();
                    testimonialMap.put("id", testimonial.getId());
                    testimonialMap.put("fullname", testimonial.getUser().getFullname());
                    testimonialMap.put("rating", testimonial.getRating());
                    testimonialMap.put("testimonial", testimonial.getTestimonial());
                    testimonialMap.put("created_at", testimonial.getCreatedAt());
                    return testimonialMap;
                })
                .collect(Collectors.toList());
        response.put("allTestimonials", testimonialList);
        
        return response;
    }

    // Promo Management
    public Promo createPromo(PromoRequest request) {
        Destination destination = destinationRepository.findById(request.getId_destinasi())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        Promo promo = new Promo();
        promo.setDestination(destination);
        promo.setDeskripsi(request.getDeskripsi());
        promo.setHargaDiskon(request.getHargaDiskon());
        promo.setNamaDiskon(request.getNama_diskon());
        promo.setPersentaseDiskon(request.getPersentase_diskon().doubleValue());
        promo.setTanggalBerakhir(request.getTanggal_berakhir());
        promo.setTanggalMulai(request.getTanggal_mulai());

        return promoRepository.save(promo);
    }

    public Promo updatePromo(Long id, PromoRequest request) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promo not found"));
        
        Destination destination = destinationRepository.findById(request.getId_destinasi())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        promo.setDestination(destination);
        promo.setDeskripsi(request.getDeskripsi());
        promo.setHargaDiskon(request.getHargaDiskon());
        promo.setNamaDiskon(request.getNama_diskon());
        promo.setPersentaseDiskon(request.getPersentase_diskon().doubleValue());
        promo.setTanggalBerakhir(request.getTanggal_berakhir());
        promo.setTanggalMulai(request.getTanggal_mulai());

        return promoRepository.save(promo);
    }

    public void deletePromo(Long id) {
        promoRepository.deleteById(id);
    }

    // Booking Status Management
    public BookingResponse updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
        
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToBookingResponse(updatedBooking);
    }

    // Facility Management
    public Facility createFacility(FacilityRequest request) {
        Facility facility = new Facility();
        facility.setName(request.getNama());
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(Long id, FacilityRequest request) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        facility.setName(request.getNama());
        return facilityRepository.save(facility);
    }

    public void deleteFacility(Long id) {
        facilityRepository.deleteById(id);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setUserId(booking.getUser().getUserId());
        response.setPackageId(booking.getDestination().getId());
        response.setTotalPersons(booking.getTotal_persons());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setDepartureDate(booking.getDeparture_date());
        response.setReturnDate(booking.getReturn_date());
        
        return response;
    }
} 