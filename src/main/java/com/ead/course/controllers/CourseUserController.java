package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    CourseService courseService;

    @Autowired
    UserService userService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUseresByCourse( SpecificationTemplate.UserSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                              @PathVariable(value = "courseId") UUID courseId){
        //verifica se o curso existe
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public  ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                                @RequestBody @Valid SubscriptionDto subscriptionDto){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){ //curso não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }
        if(courseService.existsByCourseAndUser(courseId, subscriptionDto.getUserId())) { //já existe o usuario inscrito no curso
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
        }
        Optional<UserModel> userModelOptional = userService.findById(subscriptionDto.getUserId());
        if(!userModelOptional.isPresent()) { //usuario não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if(userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString())){ //se o usuario estiver bloqueado
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked.");
        }
        courseService.saveSubscriptionUserInCourse(courseModelOptional.get().getCourseId(), userModelOptional.get().getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully.");
    }

}
