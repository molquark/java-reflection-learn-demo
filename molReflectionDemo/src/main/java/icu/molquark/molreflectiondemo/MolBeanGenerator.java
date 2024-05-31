package icu.molquark.molreflectiondemo;

import icu.molquark.molreflectiondemo.domain.Student;
import icu.molquark.molreflectiondemo.domain.Teacher;
import org.springframework.context.annotation.Bean;

public class MolBeanGenerator {

    @Bean
    public static Student student(){
        return new Student();
    }

    @Bean
    public Teacher teacher(){
        return new Teacher();
    }

    @Bean
    public Integer test(int num, String str, String ... args){
        return 0;
    }
}
