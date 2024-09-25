package com.blog.app.webservice.security;


public class SecurityConstants {
    public static final long EXPIRATION_TIME= 864_000_000L; //10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/create";
    //public static final String LOGIN_URL = "/login";
    public static final String TOKEN_SECRET = "oYVJd72OYGe2N4ojAq9whxUnT9JnS4dAAB#AVF8MAs3+LnF94Nt#ll9Hgu9+qz6+hr5oYVJd72OYGe2N4ojAq9whxUnT9";

}
