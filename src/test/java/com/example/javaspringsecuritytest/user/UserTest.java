package com.example.javaspringsecuritytest.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {
    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void initEach(){
        user1 = new User("username", "password", "email");
        user2 = new User("username", "password", "email");
        user3 = new User("username2", "password2", "email2");
    }

    @Test
    void toStringTest(){
        assertEquals("User{username='username', password='password', email='email'}", user1.toString());
    }

    @Test
    void equalsTestTrue(){
        assertEquals(user1, user2);
    }

    @Test
    void equalsTestFalse(){
        assertNotEquals(user1, user3);
    }
}