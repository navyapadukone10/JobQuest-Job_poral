package com.krish.jobquestbackend;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/applications")
@CrossOrigin(origins = "https://job-quest-client.vercel.app")
public class JobApplicationController {
    final List<String> VALID_STATUS_OPTIONS = Arrays.asList("Pending", "Accepted", "Rejected");

    @Autowired
    private JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllJobApplications() {
        return new ResponseEntity<List<JobApplication>>(jobApplicationService.allJobApplications(), HttpStatus.OK);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Optional<JobApplication>> getSingleJobApplication(@PathVariable String jobId) {
        ObjectId singleJobId = new ObjectId(jobId);
        return new ResponseEntity<Optional<JobApplication>>(jobApplicationService.singleJobApplication(singleJobId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JobApplication> applyForJob(@RequestBody JobApplication jobApplication) {
        return new ResponseEntity<JobApplication>(jobApplicationService.createJobApplication(jobApplication), HttpStatus.CREATED);
    }

    @PostMapping("/{applicationId}")
    public ResponseEntity<?> updateJobApplicationStatus(@PathVariable String applicationId, @RequestBody String newStatus) {
        ObjectId applicationObjectId = new ObjectId(applicationId);

        if (VALID_STATUS_OPTIONS.contains(newStatus)) {
            return new ResponseEntity<JobApplication>(jobApplicationService.updateStatus(applicationObjectId, newStatus), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Invalid option", HttpStatus.BAD_REQUEST);
        }
    }
}
