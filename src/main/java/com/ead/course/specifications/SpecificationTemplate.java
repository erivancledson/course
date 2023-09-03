package com.ead.course.specifications;

import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {
 //para filtros avançados
    @And({
            @Spec(path = "courseLevel", spec = Equal.class),
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "name", spec = Like.class)
    })
    public interface CourseSpec extends Specification<CourseModel> {}

    @And({
            @Spec(path = "email", spec = Like.class),
            @Spec(path = "fullName", spec = Like.class),
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "userType", spec = Equal.class)
    })
    public interface UserSpec extends Specification<UserModel> {}

    @Spec(path = "title", spec = Like.class) //busca pelo titulo
    public interface ModuleSpec extends Specification<ModuleModel> {}

    @Spec(path = "title", spec = Like.class) //busca pelo titulo
    public interface LessonSpec extends Specification<LessonModel> {}

    public static Specification<ModuleModel> moduleCourseId(final UUID courseId) {
        return (root, query, cb) -> {
            query.distinct(true); //a query não vai ter valor duplicado
            Root<ModuleModel> module = root; //Entidade ModuleModule é a que vai fazer parte da consulta
            Root<CourseModel> course = query.from(CourseModel.class); //entidade CourseModel vai fazer parte da consulta
            Expression<Collection<ModuleModel>> coursesModules = course.get("modules"); //colecao da entidade a na entidade b
            return cb.and(cb.equal(course.get("courseId"), courseId), cb.isMember(module, coursesModules)); // courseId = construção criteriaBuilder utilizando AND, eu quero todos os cursos que tem o id x, e a segunda parte verifica os modulos que estão pertecendo a esta coleção
        };
    }

    public static Specification<LessonModel> lessonModuleId(final UUID moduleId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<LessonModel> lesson = root; //entidade a
            Root<ModuleModel> module = query.from(ModuleModel.class); //entidade b
            Expression<Collection<LessonModel>> moduleLessons = module.get("lessons"); //coleção de lições
            return cb.and(cb.equal(module.get("moduleId"), moduleId), cb.isMember(lesson, moduleLessons)); //criteria builder
        };
    }


    //retorna usuarios de acordo com um determinado curso
    public static Specification<UserModel> userCourseId(final UUID courseId){
        return (root, query, cb) ->{
            query.distinct(true);
            Root<UserModel> user = root;
            Root<CourseModel> course = query.from(CourseModel.class);
            Expression<Collection<UserModel>> coursesUsers = course.get("users"); //extrai a coleção de users
            return cb.and(cb.equal(course.get("courseId"), courseId), cb.isMember(user, coursesUsers)); //faz a consulta com criteria build
        };
    }

    //passo o id do usuario e vem a lista de cursos que ele pertence
    public static Specification<CourseModel> courseUserId(final UUID userId){
        return (root, query, cb) ->{
            query.distinct(true);
            Root<CourseModel> course = root;
            Root<UserModel> user = query.from(UserModel.class);
            Expression<Collection<UserModel>> coursesUsers = course.get("courses"); //extrai a coleção de cursos
            return cb.and(cb.equal(course.get("userId"), userId), cb.isMember(user, coursesUsers)); //faz a consulta com criteria build
        };
    }
}
