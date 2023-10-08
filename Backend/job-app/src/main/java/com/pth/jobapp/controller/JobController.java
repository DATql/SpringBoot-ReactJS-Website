package com.pth.jobapp.controller;

import com.pth.jobapp.ResponseModels.ApplicationResponse;
import com.pth.jobapp.ResponseModels.FavoriteJobResponse;
import com.pth.jobapp.entity.*;
import com.pth.jobapp.requestmodels.EmployerRegistrationRequest;
import com.pth.jobapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    EmployerService employerService;
    @Autowired
    CandidateService candidateService;
    @PostMapping("/create")
    public ResponseEntity<String> createJob(@RequestHeader("Authorization") String token,  @RequestBody Job job) {
        try {
            String employerName = jwtService.extractUsername(token.substring(7)); // Remove "Bearer " prefix from token
            Employer employer = employerService.findByAccountUsername(employerName); // Assuming you have a method
            UUID uuid = UUID.randomUUID();
            job.setId(uuid.toString());
            job.setFromDate(new Date());
            job.setEmployerId(employer.getId());
            job.setState("pending");
            jobService.save(job);
            return ResponseEntity.ok("Job created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create job");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateJob(@RequestHeader("Authorization") String token, @RequestBody String jobId, @RequestBody Job updatedJob) {
        try {
            String employerName = jwtService.extractUsername(token.substring(7)); // Remove "Bearer " prefix from token
            Employer employer = employerService.findByAccountUsername(employerName); // Assuming you have a method
            if (employer == null)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Not employer");

            Optional<Job> existingJob = jobService.findById(jobId);
            if (existingJob.isPresent()) {
                Job job = existingJob.get();

                if (!(job.getState().equals("active")||job.getState().equals("pending"))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Job is not in an active state and cannot be updated.");
                }

                Date currentDate = new Date();


                if (job.getToDate().before(currentDate)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The updated toDate must be after the current date.");
                }

                job.setTitle(updatedJob.getTitle());
                job.setDescription(updatedJob.getDescription());
                job.setSalary(updatedJob.getSalary());
                job.setFromDate(updatedJob.getFromDate());
                job.setToDate(updatedJob.getToDate());
                job.setAddress(updatedJob.getAddress());
                job.setState("pending");
                job.setCategoryId(updatedJob.getCategoryId());
                jobService.save(job);
                return ResponseEntity.ok("Job updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The job with ID " + jobId + " was not found.");

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update job");
        }
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteJob(
//            @RequestHeader("Authorization") String token,
//            @RequestParam String jobId
//    ) {
//        try {
//            String employerName = jwtService.extractUsername(token.substring(7));
//            Employer employer = employerService.findByAccountUsername(employerName);
//            if (employer == null)
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unauthorized");
//
//            Optional<Job> existingJob = jobService.findById(jobId);
//            if (existingJob.isPresent()) {
//                Job job = existingJob.get();
//
//                if (!job.getEmployerId().equals(employer.getId())) {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Permission denied");
//                }
//
//                List<Application> applications = applicationService.findApplicationsByJobId(jobId);
//
//                for (Application application : applications) {
//                    applicationService.delete(application);
//                }
//
//                jobService.delete(job);
//
//                return ResponseEntity.ok("Job deleted successfully");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Job not found");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete job");
//        }
//    }

    @GetMapping("/jobsEmployer")
    public ResponseEntity<Page<Job>> jobsEmployer(@RequestHeader("Authorization") String token, Pageable pageable) {
        try {
            String employerName = jwtService.extractUsername(token.substring(7)); // Remove "Bearer " prefix from token
            Employer employer = employerService.findByAccountUsername(employerName); // Assuming you have a method to find an employer by username

            if (employer != null) {
                Page<Job> employerJobs = jobService.findByEmployerId(employer.getId(), pageable);
                return ResponseEntity.ok(employerJobs);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle the exception and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PostMapping("/new-favorite-job")
//    public ResponseEntity<String> newFavoriteJobs(@RequestHeader("Authorization") String token,
//                                                  @RequestBody JobIdRequest jobIdRequest) {
//        System.out.println(token);
//        String candidateEmail = jwtService.extractUsername(token.substring(7));
//        Candidate candidate = candidateService.findCandidateByAccountUsername(candidateEmail).orElse(null);
//
//        if (candidate == null) {
//            // Handle the case where the candidate is not found
//            return new ResponseEntity<>("Candidate not found", HttpStatus.NOT_FOUND);
//        }
//
//        String candidateId = candidate.getId();
//        String jobId = jobIdRequest.getJobId();
//        Favorite favorite = new Favorite(candidateId, jobId);
//
//        if (favoriteService.findByCandidateIdAndJobId(candidateId, jobId).isPresent()) {
//            return new ResponseEntity<>("Đã thêm công việc này thành yêu thích", HttpStatus.OK);
//        } else {
//            favoriteService.save(favorite);
//            return new ResponseEntity<>("Công việc đã được thêm vào yêu thích", HttpStatus.CREATED);
//        }
//    }

//    @DeleteMapping("/delete-favorite")
//    public ResponseEntity<String> deleteFavoriteJob(@RequestHeader("Authorization") String token, @RequestBody JobIdRequest jobIdRequest) {
//        String candidateEmail = jwtService.extractUsername(token.substring(7));
//        Candidate candidate = candidateService.findCandidateByAccountUsername(candidateEmail).orElse(null);
//
//        if (candidate == null) {
//            // Handle the case where the candidate is not found
//            return new ResponseEntity<>("Candidate not found", HttpStatus.NOT_FOUND);
//        }
//
//        String candidateId = candidate.getId();
//        String jobId = jobIdRequest.getJobId();
//
//        Optional<Favorite> existingFavorite = favoriteService.findByCandidateIdAndJobId(candidateId, jobId);
//
//        if (existingFavorite.isPresent()) {
//            // The favorite exists, so we can delete it
//            favoriteService.delete(existingFavorite.get());
//            return new ResponseEntity<>("Favorite job deleted", HttpStatus.OK);
//        } else {
//            // The favorite doesn't exist, return a response indicating it was not found
//            return new ResponseEntity<>("Favorite job not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/favorite-jobs")
//    public ResponseEntity<List<FavoriteJobResponse>> getFavoriteJobs(
//            @PageableDefault(page = 0, size = 10) Pageable pageable,
//            @RequestHeader("Authorization") String token
//
//    ) {
//        try {
//            String email = jwtService.extractUsername(token.substring(7)); // Remove "Bearer " prefix from token
//            Candidate candidate = candidateService.findCandidateByAccountUsername(email).get(); // Assuming you have a method to find an employer by username
//            if (candidate != null) {
//
//                List<Favorite> favorites = favoriteService.findByCandidateId(candidate.getId());
//                List<FavoriteJobResponse> responseList = new ArrayList<>();
//
//                for (Favorite favorite : favorites) {
//                    Optional<Job> jobOptional = jobService.findById(favorite.getJobId());
//                    Optional<Employer> employerOptional = employerService.findById(jobOptional.get().getEmployerId());
//
//                    if (jobOptional.isPresent() && employerOptional.isPresent()) {
//                        Job job = jobOptional.get();
//                        Employer employer = employerOptional.get();
//
//                        FavoriteJobResponse response = new FavoriteJobResponse();
//                        response.setJobId(jobOptional.get().getId());
//                        response.setImage(employer.getImage());
//                        response.setName(employer.getName());
//                        response.setAddress(employer.getAddress());
//                        response.setSalary(job.getSalary());
//                        response.setTitle(job.getTitle());
//                        responseList.add(response);
//                    } else {
//                        // Xử lý trường hợp khi không tìm thấy Candidate hoặc Job
//                        System.err.println("Không tìm thấy Candidate hoặc Job cho ứng viên " + candidate.getId() + " và công việc " + jobOptional.get().getId());
//                    }
//                }
//
//                return ResponseEntity.ok(responseList);
//            } else {
//                return ResponseEntity.notFound().build(); // Employer not found
//            }
//        } catch (Exception e) {
//            // Handle the exception and return an appropriate response
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//



}
