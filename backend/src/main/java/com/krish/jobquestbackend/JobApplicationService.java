package com.krish.jobquestbackend;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public List<JobApplication> allJobApplications() {
        return jobApplicationRepository.findAll();
    }

    public Optional<JobApplication> singleJobApplication(ObjectId jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    public JobApplication createJobApplication(JobApplication jobApplication) {
        return jobApplicationRepository.insert(jobApplication);
    }

    public JobApplication updateStatus(ObjectId applicationId, String newStatus) {
        JobApplication application = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new RuntimeException("Job application not found"));

        application.setStatus(newStatus);
        jobApplicationRepository.save(application);
        return application;
    }
}
