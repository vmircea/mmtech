package com.membership.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MmtechApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmtechApplication.class, args);
    }

    /**
     * TO DO:
     * 1. (DONE) Add Membership, Get Membership, Get Memberships (Controller)
     * 2. (DONE) Find By Id, Find All, Save-partial for POST (Repository)
     * 3. (DONE) Update, Patch for Models (membership and contactInfo)
     * 4. (DONE) PUT MAPPING: Update and Patch for Controller, Service and Repository.
     * 5. (DONE) Service, Repository: add logic for combining update and create and patch.
     * 6. (DONE) Service: Add New Member if does not exist (look after phone number)
     * 7. (DONE) Unit Testing for Membership service
     * 8. (DONE) Integration Testing for Membership service
     * 9. (DONE) ENDPOINTS for membership_skills: add skills
     * 10. (DONE) ENDPOINTS for membership fees: List of objects which contains paidInDate, paidInAmmount: AddNewAmmount
     * 11. (DONE) Fetch Members with all details including skills and fees => more repository => more services
     * 12. (DONE) Unit Testing for getMemberships, get and add skills, get and add fees
     * 13. (NOT DONE) ENDPOINTS for Branch  Model: create, getAll, getById
     * 14. (NOT DONE) Unit Testing for ...
     * 15. (NOT DONE) Implement Blacklist model + REST API + service + repo + DB. Do not allow adding new members if blacklisted
     * 16. (NOT DONE) Unit Testing for ...
     * 17. (NOT DONE) Implement a new model called Project (id, name, description, List<skills>) + REST API + DB
     * 18. (NOT DONE) Fetch all the members which have at least one skill associated with one project
     * 19. (NOT DONE) Unit Testing for ...
     * 20. (NOT DONE) Exception Handling: Return some JSON objects on Body and relevant HTTP response code in order to communicate well with front end
     */

}
