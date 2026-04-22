package com.aluguelcarros.web.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpResponse;

import java.net.URI;

@Controller
public class HomeController {

    @Get("/")
    public HttpResponse<?> index() {
        return HttpResponse.seeOther(URI.create("/login"));
    }
}
