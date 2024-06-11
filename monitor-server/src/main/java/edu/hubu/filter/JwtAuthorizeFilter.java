package edu.hubu.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import edu.hubu.entity.RestBean;
import edu.hubu.entity.dto.Client;
import edu.hubu.service.ClientService;
import edu.hubu.utils.Const;
import edu.hubu.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {
    @Resource
    JwtUtils jwtUtils;
    @Resource
    ClientService clientService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        if (uri.startsWith("/monitor")) {
            if(!uri.endsWith("/register")){
                Client clientByToken = clientService.findClientByToken(authorization);
                if(clientByToken == null){
                    response.setStatus(403);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write(RestBean.failure(403,"未注册").asJsonString());
                    return;
                }else {
                    request.setAttribute(Const.ATTR_Client,clientByToken.getId());
                }
            }
        }else {
            DecodedJWT jwt = jwtUtils.resolveJwt(authorization);
            if(jwt != null){
                UserDetails userDetails = jwtUtils.toUser(jwt);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                request.setAttribute(Const.ATTR_USER_ID,jwtUtils.toInt(jwt));
                request.setAttribute(Const.Attr_USER_ROLE,new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority());
            }
        }
        filterChain.doFilter(request,response);
    }
}
