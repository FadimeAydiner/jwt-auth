package com.tpe.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private  JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        //get token from request header
        String jwtToken=parseJwt(request);

        try {
            if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {
                //we are extracting username from token
                String userName = jwtUtils.getUserNameFromJwToken(jwtToken);
                //we are getting userdetails which is recognized from security
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                //authenticated user
                UsernamePasswordAuthenticationToken authenticationUser =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, //user itself
                                null,//credentials:referans
                                userDetails.getAuthorities()//user roles
                        );
                //after we get validated user we need to place/populate userdetails in Security context
                SecurityContextHolder.getContext().setAuthentication(authenticationUser);

            }
        }catch (UsernameNotFoundException e){
            e.printStackTrace();
        }

        //to add filter
        filterChain.doFilter(request,response);
    }

    //getting token from httprequest header
    private String parseJwt(HttpServletRequest request){
        String header=request.getHeader("Authorization");
        if(StringUtils.hasText(header)&& header.startsWith("Bearer ")){
            return header.substring(7);//Bearer 'dan sonrası yani 7. index
        }
        return null;
    }


    //optional
    //to specify which end points should not be filtered
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        return antPathMatcher.match("/register",request.getServletPath()) ||
                antPathMatcher.match("/login",request.getServletPath());
    }
}
