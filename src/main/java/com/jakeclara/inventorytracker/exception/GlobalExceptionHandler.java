package com.jakeclara.inventorytracker.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InactiveItemException.class)
        public String handleInactiveItemException(
            InactiveItemException ex,
            RedirectAttributes redirectAttributes
        ) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/dashboard";
        }
    
    @ExceptionHandler(ResourceNotFoundException.class)
        public String handleResourceNotFoundException(
            ResourceNotFoundException ex,
            RedirectAttributes redirectAttributes
        ) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/dashboard";
        }
    
}
