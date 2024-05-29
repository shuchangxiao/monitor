package edu.hubu.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.hubu.service.Imp.MyUserDetail;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    String KEY;
    @Value("${spring.security.jwt.expire}")
    int EXPIRE;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        JWTVerifier jwtVerify = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerify.verify(token);
            return deleteToken(jwt.getId(),jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }
    public boolean deleteToken(String uuid,Date time){
        if(this.isInvalidJwt(uuid)){
            return false;
        }
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(),0L);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }
    public boolean isInvalidJwt(String uuid){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        JWTVerifier jwtVerify = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerify.verify(token);
            if(this.isInvalidJwt(jwt.getId()))
                return null;
            Date expiresAt = jwt.getExpiresAt();
            return new Date().after(expiresAt) ?null:jwt;
        }catch (JWTVerificationException e){
            return null;
        }
    }

//    public String creatJwt(UserDetails userDetails,int id,String username){
//        // 使用HMAC256算法创建JWT签名算法
//        Algorithm algorithm = Algorithm.HMAC256(KEY);
//        return JWT.create() // 创建JWT
//                .withJWTId(UUID.randomUUID().toString())
//                .withClaim("id",id) // 添加用户ID作为JWT的声明
//                .withClaim("username",username) // 添加用户名作为JWT的声明
//                // 将用户权限转换为字符串列表，并添加到JWT的声明中
//                .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
//                .withExpiresAt(expireTime()) // 设置JWT的过期时间
//                .withIssuedAt(new Date()) // 设置JWT的发行时间
//                .sign(algorithm); // 使用算法签名JWT
//
//    }
    // 修改后写法
public String creatJwt(MyUserDetail userDetails){
    // 使用HMAC256算法创建JWT签名算法
    Algorithm algorithm = Algorithm.HMAC256(KEY);
    Date X = expireTime();
    return JWT.create() // 创建JWT
            .withJWTId(UUID.randomUUID().toString())
            .withClaim("id",userDetails.getId()) // 添加用户ID作为JWT的声明
            .withClaim("username",userDetails.getUsername()) // 添加用户名作为JWT的声明
            // 将用户权限转换为字符串列表，并添加到JWT的声明中
//            .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .withExpiresAt(X) // 设置JWT的过期时间
            .withIssuedAt(new Date()) // 设置JWT的发行时间
            .sign(algorithm); // 使用算法签名JWT

}
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,EXPIRE*24);
        return calendar.getTime();
    }
    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer ")) return null;
        return headerToken.substring(7);

    }
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claimMap = jwt.getClaims();
        return User.withUsername(claimMap.get("username").asString())
//                .authorities(claimMap.get("authorities").asString())
                .password("******")
                .build();
    }
    public Integer toInt(DecodedJWT jwt){
        Map<String, Claim> claimMap = jwt.getClaims();
        return claimMap.get("id").asInt();
    }
}
