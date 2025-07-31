package com.altura.altura.Service;

import com.altura.altura.DTO.DestinationRequest;
import com.altura.altura.DTO.DestinationResponse;
import com.altura.altura.DTO.FacilityDTO;
import com.altura.altura.DTO.TourPackageDiskonResponse;
import com.altura.altura.Model.Destination;
import com.altura.altura.Model.Facility;
import com.altura.altura.Model.FacilityDestination;
import com.altura.altura.Model.Gallery;
import com.altura.altura.Model.Promo;
import com.altura.altura.Model.Booking;
import com.altura.altura.Model.BookingParticipant;
import com.altura.altura.Repository.DestinationRepository;
import com.altura.altura.Repository.FacilityRepository;
import com.altura.altura.Repository.FacilityDestinationRepository;
import com.altura.altura.Repository.PromoRepository;
import com.altura.altura.Repository.BookingRepository;
import com.altura.altura.Repository.BookingParticipantRepository;
import com.altura.altura.Repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.altura.altura.DTO.DestinationDetailResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.repository.query.Param;
import java.util.Comparator;
import com.altura.altura.DTO.FacilityResponse;
import com.altura.altura.DTO.GalleryResponse;
import org.springframework.beans.factory.annotation.Value;

@Service
public class DestinationService {
    @Autowired
    private MinioService minioService;
    
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    @Value("${minio.bucketName}")
    private String minioBucket;
    
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private FacilityDestinationRepository facilityDestinationRepository;
    
    @Autowired
    private PromoRepository promoRepository;
    
    @Autowired
    private BookingParticipantRepository bookingParticipantRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    // Mendapatkan semua destinasi dengan filter dan sorting
    public List<DestinationResponse> getAllDestinations(String location, String sortBy) {
        List<Destination> destinations;
        
        // Filter berdasarkan lokasi jika parameter tidak null
        if (location != null && !location.isEmpty()) {
            destinations = destinationRepository.findByLocationContainingIgnoreCase(location);
        } else {
            destinations = destinationRepository.findAll();
        }
        
        // Sorting berdasarkan parameter
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "booking":
                    // Karena tidak bisa melakukan sort setelah query, kita perlu mengambil data baru
                    destinations = destinationRepository.findAllOrderByBookingCountDesc();
                    // Jika ada filter lokasi, filter lagi hasil sorting
                    if (location != null && !location.isEmpty()) {
                        destinations = destinations.stream()
                            .filter(d -> d.getLocation().toLowerCase().contains(location.toLowerCase()))
                            .collect(Collectors.toList());
                    }
                    break;
                case "price_asc":
                    if (location != null && !location.isEmpty()) {
                        destinations = destinationRepository.findByLocationContainingIgnoreCase(location);
                        destinations.sort(Comparator.comparing(Destination::getPrice));
                    } else {
                        destinations = destinationRepository.findAllByOrderByPriceAsc();
                    }
                    break;
                case "price_desc":
                    if (location != null && !location.isEmpty()) {
                        destinations = destinationRepository.findByLocationContainingIgnoreCase(location);
                        destinations.sort(Comparator.comparing(Destination::getPrice).reversed());
                    } else {
                        destinations = destinationRepository.findAllByOrderByPriceDesc();
                    }
                    break;
                default:
                    // No sorting or unknown sorting parameter
                    break;
            }
        }
        
        return destinations.stream()
                .map(this::mapToDestinationResponse)
                .collect(Collectors.toList());
    }
    
    // Mendapatkan semua lokasi unik untuk dropdown filter
    public List<String> getAllLocations() {
        return destinationRepository.findAllDistinctLocations();
    }

    // Membuat destinasi baru
    public DestinationResponse createDestination(DestinationRequest request) {
        Destination destination = new Destination();
        mapRequestToDestination(request, destination);
        Destination savedDestination = destinationRepository.save(destination);
        return mapToDestinationResponse(savedDestination);
    }

    // Update destinasi
    public DestinationResponse updateDestination(Long id, DestinationRequest request) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Destination not found"));
        mapRequestToDestination(request, destination);
        Destination updatedDestination = destinationRepository.save(destination);
        return mapToDestinationResponse(updatedDestination);
    }

    // Hapus destinasi
    public void deleteDestination(Long id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Destination not found"));
            
        // Hapus semua booking participant yang terkait dengan booking dari destination ini
        if (destination.getBookings() != null) {
            for (Booking booking : destination.getBookings()) {
                // Hapus semua participant dari booking ini terlebih dahulu
                List<BookingParticipant> participants = bookingParticipantRepository.findAll().stream()
                    .filter(bp -> bp.getBooking() != null && bp.getBooking().getBookingId().equals(booking.getBookingId()))
                    .collect(Collectors.toList());
                    
                for (BookingParticipant participant : participants) {
                    bookingParticipantRepository.delete(participant);
                }
                
                // Kemudian hapus booking
                bookingRepository.delete(booking);
            }
        }
            
        // Hapus semua promo yang terkait dengan destination ini
        List<Promo> promos = promoRepository.findAll().stream()
            .filter(promo -> promo.getDestination() != null && 
                    promo.getDestination().getId().equals(destination.getId()))
            .collect(Collectors.toList());
        for (Promo promo : promos) {
            promoRepository.delete(promo);
        }

        // Hapus semua fasilitas yang terkait
        if (destination.getFacilities() != null) {
            for (FacilityDestination fd : destination.getFacilities()) {
                facilityDestinationRepository.delete(fd);
            }
        }

        // Hapus semua galeri yang terkait (hanya data, tidak hapus file di MinIO)
        if (destination.getGalleries() != null) {
            for (Gallery gallery : destination.getGalleries()) {
                galleryRepository.delete(gallery);
            }
        }

        // Hapus destinasi
        destinationRepository.delete(destination);
    }

    // Mendapatkan destinasi berdasarkan ID
    public DestinationResponse getDestinationById(Long id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Destination not found"));
        return mapToDestinationResponse(destination);
    }

    // Mendapatkan detail destinasi
    public DestinationDetailResponse getDestinationDetailById(Long id) {
        Destination destination = destinationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Destination not found"));
        
        DestinationDetailResponse response = new DestinationDetailResponse();
        response.setId(destination.getId());
        response.setNama(destination.getName());
        // Ubah image menjadi URL lengkap
        String imageUrl = destination.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            response.setImage(minioEndpoint + "/" + minioBucket + "/" + imageUrl);
        } else {
            response.setImage(null);
        }
        response.setDescription(destination.getDescription());
        response.setHarga(destination.getPrice());
        response.setLokasi(destination.getLocation());
        response.setJumlah_orang(destination.getQuota());
        
        String itinerary = destination.getItinerary();
        if (itinerary != null) {
            response.setItenary(Arrays.asList(itinerary.split("\\n")));
        }
        
        // Ambil data fasilitas
        List<FacilityDTO> facilities = destination.getFacilities().stream()
            .map(fd -> {
                Facility facility = fd.getFacility();
                if (facility != null) {
                    return new FacilityDTO(facility.getId(), facility.getName());
                }
                return null;
            })
            .filter(f -> f != null)
            .collect(Collectors.toList());
        
        response.setFacilities(facilities);
        
        // Hitung jumlah booking berdasarkan total_persons
        int totalBookings = 0;
        if (destination.getBookings() != null) {
            totalBookings = destination.getBookings().stream()
                .filter(booking -> "Confirmed".equals(booking.getStatus()))
                .mapToInt(Booking::getTotal_persons)
                .sum();
        }
        response.setJumlahBooking(totalBookings);
        
        // Set galleries
        if (destination.getGalleries() != null) {
            List<String> galleryUrls = destination.getGalleries().stream()
                .map(gallery -> {
                    String fileName = gallery.getImageUrl();
                    if (fileName != null && !fileName.isEmpty()) {
                        return minioEndpoint + "/" + minioBucket + "/" + fileName;
                    } else {
                        return null;
                    }
                })
                .filter(url -> url != null)
                .collect(Collectors.toList());
            response.setGalleries(galleryUrls);
        }
        
        // Set informasi diskon
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
    }

    // Menambahkan fasilitas ke destinasi
    public Destination addFacilitiesToDestination(Long destinationId, List<String> facilityNames) {
        Destination destination = destinationRepository.findById(destinationId)
            .orElseThrow(() -> new RuntimeException("Destination not found"));
    
        for (String facilityName : facilityNames) {
            Facility facility = new Facility();
            facility.setName(facilityName);
            Facility savedFacility = facilityRepository.save(facility);
            
            FacilityDestination facilityDestination = new FacilityDestination();
            facilityDestination.setFacility(savedFacility); // Menggunakan setter baru
            facilityDestination.setDestination(destination);
            facilityDestinationRepository.save(facilityDestination);
        }
    
        return destinationRepository.save(destination);
    }

    // Helper method untuk mapping
    private DestinationResponse mapToDestinationResponse(Destination destination) {
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
        response.setItinerary(destination.getItinerary());
        
        // Set Facilities
        List<FacilityResponse> facilities = destination.getFacilities() != null ? 
            destination.getFacilities().stream()
                .map(fd -> {
                    FacilityResponse facilityResponse = new FacilityResponse();
                    facilityResponse.setId(fd.getFacility().getId());
                    facilityResponse.setName(fd.getFacility().getName());
                    return facilityResponse;
                })
                .collect(Collectors.toList()) : 
            new ArrayList<>();
        response.setFacilities(facilities);
        
        // Set Galleries
        List<GalleryResponse> galleries = destination.getGalleries() != null ? 
            destination.getGalleries().stream()
                .map(gallery -> {
                    GalleryResponse galleryResponse = new GalleryResponse();
                    galleryResponse.setId(gallery.getId());
                    galleryResponse.setImageUrl(gallery.getImageUrl());
                    return galleryResponse;
                })
                .collect(Collectors.toList()) : 
            new ArrayList<>();
        response.setGalleries(galleries);
        
        // Set Jumlah Booking
        Integer jumlahBooking = destination.getBookings() != null ? 
            (int) destination.getBookings().stream()
                .filter(booking -> "Confirmed".equals(booking.getStatus()))
                .mapToLong(Booking::getTotal_persons)
                .sum() : 
            0;
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
    }

    private void mapRequestToDestination(DestinationRequest request, Destination destination) {
        destination.setName(request.getName());
        destination.setLocation(request.getLocation());
        destination.setPrice(request.getPrice());
        destination.setDescription(request.getDescription());
        destination.setQuota(request.getQuota());
        destination.setItinerary(request.getItinerary());

        // Handle image upload
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = minioService.uploadFile(request.getImage());
            destination.setImageUrl(imageUrl);
        }
    }

    public List<DestinationResponse> getTopDestinations() {
        List<Destination> destinations = destinationRepository.findAll();
        return destinations.stream()
                .map(this::mapToDestinationResponse)
                .sorted((d1, d2) -> d2.getJumlahBooking().compareTo(d1.getJumlahBooking()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<TourPackageDiskonResponse> getTourPackageDiskon() {
        List<Destination> destinations = destinationRepository.findAll();
        List<TourPackageDiskonResponse> responses = new ArrayList<>();
    
        for (Destination destination : destinations) {
            // Cek promo aktif
            Optional<Promo> activePromo = promoRepository.findActivePromoByDestination(
                destination.getId(), 
                LocalDateTime.now()
            );
    
            // Hanya tambahkan destinasi yang memiliki promo aktif
            if (activePromo.isPresent()) {
                Promo promo = activePromo.get();
                TourPackageDiskonResponse response = new TourPackageDiskonResponse();
                response.setPromoId(promo.getId());
                response.setIdDestinasi(destination.getId());
                response.setNamaDestinasi(destination.getName());
                response.setDeskripsiDestinasi(destination.getDescription());
                response.setHargaAsli(destination.getPrice());
                // Ubah gambarDestinasi menjadi URL lengkap MinIO
                String gambarDestinasi = destination.getImageUrl();
                if (gambarDestinasi != null && !gambarDestinasi.isEmpty()) {
                    response.setGambarDestinasi(minioEndpoint + "/" + minioBucket + "/" + gambarDestinasi);
                } else {
                    response.setGambarDestinasi(null);
                }
                response.setLokasiDestinasi(destination.getLocation());
                
                // Hitung jumlah booking berdasarkan total_persons
                int totalBookings = 0;
                if (destination.getBookings() != null) {
                    totalBookings = destination.getBookings().stream()
                            .filter(booking -> "Confirmed".equals(booking.getStatus()))
                            .mapToInt(booking -> booking.getTotal_persons())
                            .sum();
                }
                response.setJumlahBooking(Long.valueOf(totalBookings));
                
                response.setHargaDiskon(promo.getHargaDiskon());
                response.setPersentaseDiskon(promo.getPersentaseDiskon());
                responses.add(response);
            }
        }
    
        return responses;
    }
}