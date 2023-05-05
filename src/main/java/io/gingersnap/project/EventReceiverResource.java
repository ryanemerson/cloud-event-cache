package io.gingersnap.project;

import java.io.IOException;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE})
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class EventReceiverResource {

   private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverResource.class);

   @Inject
   ObjectMapper mapper;

   @Inject
   Validator validator;

   @Inject
   Cache cache;

   @POST
   @ResponseStatus(202)
   public void receive(CloudEvent object) throws IOException {
      LOGGER.debug("Received event: {}", object.getId());
      Set<ConstraintViolation<CloudEvent>> violations = validator.validate(object);
      if (!violations.isEmpty()) {
         try {

            throw new BadRequestException(mapper.writeValueAsString(violations));
         } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e);
         }
      }
      var jsonData = mapper.readValue(object.getData().toBytes(), ObjectNode.class);
      var key = jsonData.get("key").asText();
      var value = jsonData.get("value").asText();
      LOGGER.info("Key: {}, Value: {}", key, value);
      cache.put(key, value);
   }
}
