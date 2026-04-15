package com.aluguelcarros.web.filter;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

/**
 * Garante que todas as respostas HTTP 3xx retornem Cache-Control: no-store,
 * evitando que browsers armazenem redirects permanentemente (bug do HTTP 301).
 */
@Filter("/**")
public class NoCacheRedirectFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                      ServerFilterChain chain) {
        return Publishers.map(chain.proceed(request), response -> {
            String path = request.getPath();
            if (!path.startsWith("/static/")) {
                response.header("Cache-Control", "no-store, no-cache, must-revalidate");
                response.header("Pragma", "no-cache");
            }

            int code = response.getStatus().getCode();
            if (code >= 300 && code < 400) {
                response.header("Cache-Control", "no-store, no-cache, must-revalidate");
                response.header("Pragma", "no-cache");
            }
            return response;
        });
    }
}
