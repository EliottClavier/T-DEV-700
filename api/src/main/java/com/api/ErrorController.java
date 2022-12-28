package com.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping("/error")
    public String error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        logger.error("Error status code: " + status);
        logger.error("Error exception thrown: " + request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404 - Not Found";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "500 - Internal Server Error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "403 - Forbidden";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "401 - Unauthorized";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "400 - Bad Request";
            } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return "405 - Method Not Allowed";
            }
        }
        return "Unhandled error";
    }
}