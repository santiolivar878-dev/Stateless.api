package com.stateless.stateless.security;

import com.stateless.stateless.model.User;
import com.stateless.stateless.service.CarritoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CarritoService carritoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        User user = (User) authentication.getPrincipal();

        // 1. MIGRACIÓN: Pasa los productos agregados en sesión a la base de datos del usuario
        try {
            carritoService.migrarCarritoSesionAUsuario(session, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Si venía de intentar pagar en el checkout, devuélvelo al checkout
        String redirectUrl = (String) session.getAttribute("REDIRECT_AFTER_LOGIN");
        if (redirectUrl != null) {
            session.removeAttribute("REDIRECT_AFTER_LOGIN");
            response.sendRedirect(redirectUrl);
            return;
        }

        // 3. Redirección por roles
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_EMPLEADO"));

        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");
        } else {
            response.sendRedirect("/checkout"); // Si tiene cosas en el carrito lo mandamos al checkout o a "/"
        }
    }
}