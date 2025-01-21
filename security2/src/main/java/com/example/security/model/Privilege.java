package com.example.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//@Table
//@Getter
//@Setter
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
public class Privilege {

    //@Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // @ManyToMany(mappedBy = "privileges")
    private List<Role> roles = new ArrayList<>();

    private String name;

}
