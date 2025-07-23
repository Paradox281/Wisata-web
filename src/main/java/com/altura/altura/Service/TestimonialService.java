package com.altura.altura.Service;

import com.altura.altura.DTO.TestimonialRequest;
import com.altura.altura.DTO.TestimonialResponse;
import com.altura.altura.Exception.ResourceNotFoundException;
import com.altura.altura.Model.Testimonial;
import com.altura.altura.Model.User;
import com.altura.altura.Repository.TestimonialRepository;
import com.altura.altura.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestimonialService {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TestimonialResponse> getAllTestimonials() {
        List<Testimonial> testimonials = testimonialRepository.findAll();
        return testimonials.stream()
                .map(this::mapToTestimonialResponse)
                .collect(Collectors.toList());
    }

    public List<TestimonialResponse> getTestimonialsByUserId(Long userId) {
        List<Testimonial> testimonials = testimonialRepository.findByUser_UserId(userId);
        return testimonials.stream()
                .map(this::mapToTestimonialResponse)
                .collect(Collectors.toList());
    }

    public TestimonialResponse getTestimonialById(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));
        return mapToTestimonialResponse(testimonial);
    }

    public TestimonialResponse createTestimonial(User user, TestimonialRequest request) {
        Testimonial testimonial = new Testimonial();
        testimonial.setTestimonial(request.getTestimonial());
        testimonial.setRating(request.getRating());
        testimonial.setUser(user);
        testimonial.setCreatedAt(LocalDateTime.now());

        Testimonial savedTestimonial = testimonialRepository.save(testimonial);
        return mapToTestimonialResponse(savedTestimonial);
    }

    public TestimonialResponse updateTestimonial(Long id, TestimonialRequest request) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));

        testimonial.setTestimonial(request.getTestimonial());
        testimonial.setRating(request.getRating());

        Testimonial updatedTestimonial = testimonialRepository.save(testimonial);
        return mapToTestimonialResponse(updatedTestimonial);
    }

    public void deleteTestimonial(Long id) {
        if (!testimonialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Testimonial not found with id: " + id);
        }
        testimonialRepository.deleteById(id);
    }

    private TestimonialResponse mapToTestimonialResponse(Testimonial testimonial) {
        TestimonialResponse response = new TestimonialResponse();
        response.setId(testimonial.getId());
        response.setTestimonial(testimonial.getTestimonial());
        response.setRating(testimonial.getRating());
        response.setUserName(testimonial.getUser().getFullname());
        response.setCreatedAt(testimonial.getCreatedAt());
        return response;
    }
}