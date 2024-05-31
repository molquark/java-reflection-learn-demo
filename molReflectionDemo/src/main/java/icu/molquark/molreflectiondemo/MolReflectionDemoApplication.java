package icu.molquark.molreflectiondemo;

import icu.molquark.molreflectiondemo.domain.Classroom;
import icu.molquark.molreflectiondemo.domain.Student;
import icu.molquark.molreflectiondemo.domain.Teacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.InvocationTargetException;

public class MolReflectionDemoApplication {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {

        ContextContainer container = ContextContainer.getContainer();
        Classroom classroom = container.getClassRoom();
        classroom.run();

    }

}
