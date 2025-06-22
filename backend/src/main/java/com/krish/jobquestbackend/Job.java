package com.krish.jobquestbackend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "jobs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @Id
    private ObjectId id;
    private String position;
    private String company;
    private String location;
    private String experience;
    private String description;
    private List<String> skills;

    public Job(String position, String company, String location, String experience, String description, List<String> skills) {
        this.position = position;
        this.company = company;
        this.location = location;
        this.experience = experience;
        this.description = description;
        this.skills = skills;
    }

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }
}
