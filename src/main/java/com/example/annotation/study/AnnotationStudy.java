package com.example.annotation.study;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

@Settings(version = 1.1, author = {"A","B"})
public class AnnotationStudy {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {


        System.out.println("< Annotation 값 얻어오기 >");
        Settings settings = AnnotationStudy.class.getAnnotation(Settings.class);
        System.out.println("version : " + settings.version());
        System.out.println("author : " + Arrays.toString(settings.author()));
        System.out.println();

        System.out.println("< @Repeatable >");
        Role[] arrRole = Car.class.getAnnotationsByType(Role.class);

        Arrays.stream(arrRole).forEach(k -> System.out.println(k.value()));
//        Arrays.stream(arrRole).forEach(System.out::println);

        System.out.println();

        System.out.println("< @Inherited >");
        System.out.println(Arrays.toString(NewCar.class.getAnnotations()));
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        Map<String, Method> handlerMapping = new HashMap<>();

        for (Method method : Car.class.getDeclaredMethods()) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if(requestMapping != null) handlerMapping.put(requestMapping.value(), method);
        }
        handlerMapping.forEach((k,v) -> System.out.println("Command: " + k +", method: " + v));
        System.out.println();

        System.out.println();

        while (true) {
            System.out.println("명령어를 입력하세요(name, quit)");
            String command = scanner.nextLine();
            try {
                if(handlerMapping.get(command) != null) {
                    String returnStr = (String)handlerMapping.get(command).invoke(Car.class.newInstance());
                    System.out.println(returnStr);
                } else {
                    System.out.println("잘못된 명령어 입력하였습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Settings {
    String lang() default "kor";
    double version() default 1.0;
    String[] author();
}

@Role("Manager")
@Role("User")
class Car {
    @SuppressWarnings("rawtypes")
    private List tires = new ArrayList();

    @RequestMapping("name")
    public String getName() {
        return "자동차는 K5 입니다.";
    }

    @RequestMapping("quit")
    public String quit(){
        System.out.println("프로그램이 종로됩니다.");
        System.exit(0);
        return null;
    }
}

class NewCar extends Car {
    @Override
    public String getName() {
        return "[NEW] " + super.getName();
    }
}

@Inherited
@Repeatable(value = Roles.class)
@Retention(RetentionPolicy.RUNTIME)
@interface Role {
    String value();
}

@Inherited
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface Roles {
    Role[] value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface RequestMapping {
    String value();
}