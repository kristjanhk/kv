package eu.kyngas.kv.kv.service;

import eu.kyngas.kv.core.audit.model.KvAuditEntity;
import eu.kyngas.kv.core.audit.service.AuditRepository;
import eu.kyngas.kv.kv.model.Kv;
import eu.kyngas.kv.kv.model.KvEntity;
import eu.kyngas.kv.kv.model.KvMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Slf4j
@Path("kv")
public class KvResource {
  @Inject
  KvRepository kvRepository;
  @Inject
  AuditRepository auditRepository;
  @Inject
  KvMapper kvMapper;

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Optional<Kv> getKv(@PathParam("id") long id) {
    return kvRepository.findByIdOptional(id).map(entity -> kvMapper.convert(entity));
  }

  @POST
  @Consumes(APPLICATION_JSON)
  @Produces(TEXT_PLAIN)
  @Transactional
  public long createKv(@NotNull Kv kv) {
    KvEntity entity = kvMapper.inverse(kv);
    kvRepository.persist(entity);
    return entity.getId();
  }

  @PUT
  @Consumes(APPLICATION_JSON)
  @Produces(TEXT_PLAIN)
  @Transactional
  public long updateKv(@NotNull Kv kv) {
    KvEntity entity = kvMapper.inverse(kv);
    return kvRepository.getEntityManager().merge(entity).getId();
  }

  @DELETE
  @Path("{id}")
  @Produces(TEXT_PLAIN)
  @Transactional
  public boolean deleteKv(@PathParam("id") long id) {
    return kvRepository.deleteById(id);
  }

  @GET
  @Path("audit/{id}")
  @Produces(APPLICATION_JSON)
  public Map<Long, List<KvAuditEntity>> findChanges(@PathParam("id") long id) {
    return auditRepository.findChanges(KvAuditEntity.class, KvEntity.class, List.of(id));
  }
}
