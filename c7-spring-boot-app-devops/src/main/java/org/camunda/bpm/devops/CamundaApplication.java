package org.camunda.bpm.devops;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("devops-training")
public class CamundaApplication {

    public static void main( String[] args ) {
        System.out.println( "Hello World! Give me cookies" );

        SpringApplication.run(CamundaApplication.class, args);
    }
}
