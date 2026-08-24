package com.stateless.stateless.controller.web;

@Controller
public class ForgotPasswordController {

    @Autowired private PasswordResetService resetService;
    @Autowired private EmailService emailService;
    @Autowired private UserRepository userRepository;
    @Value("${app.base-url}") private String baseUrl;

    @GetMapping("/forgot-password")
    public String showForgotForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam String email, RedirectAttributes ra) throws MessagingException {
        String token = resetService.createToken(email);
        
        // Respuesta genérica por seguridad (Igual que en tu AuthController.php)
        if (token != null) {
            String url = baseUrl + "/reset-password/" + token;
            User user = userRepository.findByEmail(email).get();
            emailService.sendResetPasswordEmail(email, url, user.getName());
        }

        ra.addFlashAttribute("status", "Si ese correo existe, recibirás un enlace en breve.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password/{token}")
    public String showResetForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processReset(@RequestParam String token, 
                               @RequestParam String password, 
                               @RequestParam String password_confirmation, 
                               RedirectAttributes ra) {
        
        if (!password.equals(password_confirmation)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/reset-password/" + token;
        }

        if (resetService.validateAndReset(token, password)) {
            ra.addFlashAttribute("success", "¡Contraseña actualizada! Ya puedes iniciar sesión.");
            return "redirect:/login";
        }

        ra.addFlashAttribute("error", "El enlace expiró o es inválido.");
        return "redirect:/forgot-password";
    }
}