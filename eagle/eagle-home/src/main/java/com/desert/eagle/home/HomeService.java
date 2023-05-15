package com.desert.eagle.home;

public interface HomeService {
    String index(String invite, boolean need);

    String wxSignInUrl(String host, String fingerprint);

    String wxSignIn(String code, String host);

    String toAuth(String fingerprint);

    String auth(String code);
}
