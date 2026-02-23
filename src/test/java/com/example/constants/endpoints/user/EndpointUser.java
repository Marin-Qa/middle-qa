package com.example.constants.endpoints.user;

public abstract class EndpointUser {

    public EndpointUser() {}

    public static final String FILTERED = "/api/users/filtered";
    public static final String USERS = "/api/users";
    public static final String USER_BY_ID = "/api/users/{id}";
    public static final String PUT = "/api/users/{id}";
    public static final String DELETE = "/api/users/{id}";
    public static final String CREATE = "/api/users/create";
    public static final String SYNC = "/api/users/sync";
}
