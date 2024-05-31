package icu.molquark.molreflectiondemo.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Classroom {

    private Teacher teacher;
    private Student student;

//    public Classroom(){
//        this.student = new Student();
//        this.teacher = new Teacher();
//    }

    public Classroom(Teacher teacher, Student student){
        this.student = student;
        this.teacher = teacher;
    }

    public void run(){
        teacher.show();
        student.whisper();
    }
}
