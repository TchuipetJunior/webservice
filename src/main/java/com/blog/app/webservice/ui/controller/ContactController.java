package com.blog.app.webservice.ui.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @GetMapping("/contact")
    public String getContactInquiryDetails() {
        return "Inquiry contact details are saved to the DB";
    }

}
