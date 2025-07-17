package com.banking.banking.service;

import com.banking.banking.dto.UserDto;
import com.banking.banking.mapper.UserMapper;
import com.banking.banking.model.User;
import com.banking.banking.request.LoginRequest;
import com.banking.banking.response.LoginResponse;
import com.banking.banking.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final DaoAuthenticationProvider authenticationProvider;
    private final JWTService jwtService;
    private final CookieService cookieService;
    private final UserMapper userMapper;

    @Value("${access_token_expiration}")
    private Integer accessTokenExpiration;
    @Value("${jwt.security.secret_key_accessToken}")
    private String accessTokenKey;

    public AuthenticationService(UserDetailsService userDetailsService, DaoAuthenticationProvider authenticationProvider, JWTService jwtService, CookieService cookieService, UserMapper userMapper) {
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        User user = ((User) userDetailsService.loadUserByUsername(loginRequest.getUsername()));

        String salt = user.getSalt();

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword() + salt);
        Authentication authUser = authenticationProvider.authenticate(authentication);

        String accessToken = jwtService.generateToken(accessTokenKey, accessTokenExpiration, authUser);
        cookieService.addCookie(httpServletResponse, "accessToken", accessToken, accessTokenExpiration, true);

        UserDto userDto = userMapper.userToDto(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserDto(userDto);

        return loginResponse;
    }

    public LoginResponse me() {
        User user = SecurityUtil.getCurrentUser();
        UserDto userDto = userMapper.userToDto(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserDto(userDto);
        return loginResponse;
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SecurityContextHolder.clearContext();
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession != null)
            httpSession.invalidate();
        if (httpServletRequest.getCookies() != null) {
            cookieService.addCookie(httpServletResponse, "accessToken", "", 0, true);
        }
    }
}
