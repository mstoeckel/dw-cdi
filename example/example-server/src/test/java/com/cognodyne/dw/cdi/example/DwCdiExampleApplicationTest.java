package com.cognodyne.dw.cdi.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

public class DwCdiExampleApplicationTest {
    private static Client       client;
    private static ObjectMapper mapper;

    @BeforeClass
    public static void prepare() {
        DwCdiExampleApplication.main("server", "var/conf/test-server.yml");
        mapper = Jackson.newObjectMapper();
        client = new JerseyClientBuilder().sslContext(getSslContext()).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        }).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPing() throws Exception {
        Response resp = client.target("http://localhost:8081/admin").path("healthcheck").request().accept(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus()).isEqualTo(200);
        Map<String, Object> map = readEntity(resp, Map.class, String.class, Object.class);
        assertThat(((Map<String, Boolean>) map.get("ping")).get("healthy")).isEqualTo(true);
    }

    private <T> T readEntity(Response resp, Class<T> resultClass, Class<?>... parametricClasses) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(resp.readEntity(String.class), mapper.getTypeFactory().constructParametricType(resultClass, parametricClasses));
    }

    private static SSLContext getSslContext() {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new TrustManager[] { getTrustManager() }, new SecureRandom());
        } catch (java.security.GeneralSecurityException ex) {
        }
        return ctx;
    }

    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        };
    }
}
