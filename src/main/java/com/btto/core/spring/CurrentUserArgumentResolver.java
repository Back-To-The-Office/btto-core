package com.btto.core.spring;

import com.btto.core.domain.User;
import com.btto.core.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.google.common.base.Preconditions.checkArgument;

@SuppressWarnings("NullableProblems")
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    @Autowired
    public CurrentUserArgumentResolver(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null;
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) {
        checkArgument(parameter.getParameter().getType().isAssignableFrom(User.class),
                String.format(
                        "Method %s has incompatible with @CurrentUser annotation type: %s",
                        parameter.getMethod(),
                        parameter.getParameter().getType().getName()
                ));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new IllegalStateException(String.format(
                    "Error mapping parameter %s in method %s: Authentication not found for @CurrentUser parameter",
                    parameter.getParameterName(),
                    parameter.getMethod() != null ? parameter.getMethod().getName() : ""
            ));
        }

        final String email = (String) auth.getPrincipal();
        checkArgument(StringUtils.isNotBlank(email));

        return userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Can't find user with email " + email + ". Something wrong with authorization system")
                );
    }
}
