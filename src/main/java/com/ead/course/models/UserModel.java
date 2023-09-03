package com.ead.course.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor //tras todos os construtores com parametros
@NoArgsConstructor //vai ter lugares que não vai precisar de construtor com parametros
@JsonInclude(JsonInclude.Include.NON_NULL) //oculta todos os atributos que estão com valores nulos
@Entity
@Table(name = "TB_USERS")
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID userId;

    @Column(nullable = false, unique = true, length =  50)
    private String email;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false)
    private String userStatus;

    @Column(nullable = false)
    private String userType;

    @Column(length = 20)
    private String cpf;

    @Column
    private String imageUrl;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<CourseModel> courses;
}
