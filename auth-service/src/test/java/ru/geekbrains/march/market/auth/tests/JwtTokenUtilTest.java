package ru.geekbrains.march.market.auth.tests;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import ru.geekbrains.march.market.auth.utils.JwtTokenUtil;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class JwtTokenUtilTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void generateTokenTest() throws InterruptedException {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("Vova Pupkin", "12345", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = jwtTokenUtil.generateToken(userDetails);

        String username = jwtTokenUtil.getUsernameFromToken(token);
        List<String> roleList = jwtTokenUtil.getRoles(token);

        Assertions.assertEquals(username, "Vova Pupkin");
        Assertions.assertEquals(1, roleList.size());
        Assertions.assertEquals(roleList.get(0), "ROLE_ADMIN");

        Thread.sleep(1000);

        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtTokenUtil.getUsernameFromToken(token));
    }

}
