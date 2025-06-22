package com.krish.jobquestbackend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/candidates")
@CrossOrigin(origins = "https://job-quest-client.vercel.app")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return new ResponseEntity<List<Candidate>>(candidateService.allCandidates(), HttpStatus.OK);
    }

    //    TO BE REMOVED LATER, USING JUST FOR TESTING PURPOSES
    @GetMapping("/{email}")
    public ResponseEntity<Optional<Candidate>> getSingleCandidate(@PathVariable String email) {
        return new ResponseEntity<Optional<Candidate>>(candidateService.singleCandidate(email), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Candidate candidate) {
        Optional<Candidate> existingCandidate = candidateService.singleCandidate(candidate.getEmail());
        if (existingCandidate.isPresent()) {
            return new ResponseEntity<String>("Email already taken", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Candidate>(candidateService.createCandidate(candidate), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload, HttpServletRequest httpServletRequest) {
        String email = payload.get("email");
        String password = payload.get("password");

        try {
            Optional<Candidate> candidate = candidateService.singleCandidate(email);
            if (candidate.isEmpty()) {
                return new ResponseEntity<Map<String, Object>>(Map.of("error", "Email not found"), HttpStatus.NOT_FOUND);
            }

            String hashedPassword = candidate.get().getPassword();

            if (!passwordEncoder.matches(password, hashedPassword)) {
                return new ResponseEntity<Map<String, Object>>(Map.of("error", "Wrong password"), HttpStatus.UNAUTHORIZED);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            HttpSession session = httpServletRequest.getSession(true);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", session.getId());
            responseBody.put("candidate", candidate);

            return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<Map<String, Object>>(Map.of("error", "Authentication error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return new ResponseEntity<String>("Logged out successfully", HttpStatus.OK);
    }
}
