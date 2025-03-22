package ai.recon;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;

import java.util.*;
import java.util.stream.Collectors;

public class RequestUtils {

    /* public static Set<String> getScopedHosts(MontoyaApi api) {
        return api.proxy().history().stream()
                .filter(req -> {
                    try {
                        return api.scope().isInScope(req.finalRequest().url());
                        
                                            } catch (Exception e) {
                        return false; // malformed URL
                    }
                })
                .map(req -> req.finalRequest().httpService().host())
                .collect(Collectors.toSet());
    } */
   public static Set<String> getScopedHosts(MontoyaApi api) {
    Set<String> scopedHosts = new HashSet<>();

    for (ProxyHttpRequestResponse req : api.proxy().history()) {
        try {
            var url = req.finalRequest().url();
            boolean inScope = api.scope().isInScope(url);

            System.out.println("Checked URL: " + url + " | inScope: " + inScope);

            if (inScope) {
                String host = req.finalRequest().httpService().host();
                scopedHosts.add(host);
            }
        } catch (Exception e) {
            System.err.println("Error parsing request: " + e.getMessage());
        }
    }

    return scopedHosts;
    }


    public static List<ProxyHttpRequestResponse> getRequestsForHost(MontoyaApi api, String host) {
        return api.proxy().history().stream()
                .filter(req -> {
                    try {
                        return req.finalRequest().httpService().host().equals(host);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public static String formatRequests(List<ProxyHttpRequestResponse> requests) {
        StringBuilder sb = new StringBuilder();
        for (ProxyHttpRequestResponse r : requests) {
            try {
                var req = r.finalRequest();
                sb.append("REQUEST: ")
                        .append(req.method()).append(" ")
                        .append(req.url()).append("\n");

                if (!req.bodyToString().isEmpty()) {
                    sb.append("BODY: ").append(req.bodyToString()).append("\n");
                }

                sb.append("\n");
            } catch (Exception e) {
                sb.append("Error parsing request.\n\n");
            }
        }
        return sb.toString();
    }
}
