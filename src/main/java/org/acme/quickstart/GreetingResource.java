package org.acme.quickstart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

@Path("/greeting")
public class GreetingResource {

  private static final HttpClient httpClient = HttpClientBuilder.create().build();
  private static final URI uri = URI.create("http://greeting.local:8080/greeting/hello_impl");

  @GET
  @Path("/test")
  @Produces(MediaType.TEXT_PLAIN)
  public String test() {

    try {
      InetAddress greetingIp = InetAddress.getByName("gateway.local");
      System.out.println(
          "#####################1 - "
              + greetingIp.getHostAddress()
              + "- "
              + greetingIp.getHostName());
    } catch (UnknownHostException e) {
      System.out.println("Unknown - gateway" + e.getMessage());
    }

    try {
      InetAddress greetingIp = InetAddress.getByName("greeting.local");
      System.out.println(
          "#####################2 - "
              + greetingIp.getHostAddress()
              + "- "
              + greetingIp.getHostName());
    } catch (UnknownHostException e) {
      System.out.println("Unknown - greeting" + e.getMessage());
    }

    try {
      InetAddress greetingIp = InetAddress.getByName("greeting_1.local");
      System.out.println(
          "##################### - "
              + greetingIp.getHostAddress()
              + "- "
              + greetingIp.getHostName());
    } catch (UnknownHostException e) {
      System.out.println("Unknown - greeting_1" + e.getMessage());
    }
    return "OK";
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      HttpResponse response = httpClient.execute(new HttpGet(uri));
      response.getEntity().writeTo(baos);
      return new String(baos.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();

      throw new InternalServerErrorException(e);
    }
  }

  @GET
  @Path("/hello_impl")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloImpl() {
    return System.getenv("greeting_text");
  }
}
