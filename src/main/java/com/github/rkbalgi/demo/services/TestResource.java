package com.github.rkbalgi.demo.services;

import java.time.LocalDateTime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class TestResource {

  @GET
  @Path("/test")
  public String test() {
    return "The time in Bangalore is - " + LocalDateTime.now();
  }
}
