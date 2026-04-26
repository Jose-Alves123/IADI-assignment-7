package pt.unl.fct.iadi.novaevents.controller

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController {

    @GetMapping("/login")
    fun loginPage(authentication: Authentication?): String {
        return if (authentication != null &&
                        authentication.isAuthenticated &&
                        authentication !is AnonymousAuthenticationToken
        ) {
            "redirect:/clubs"
        } else {
            "auth/login"
        }
    }
}
