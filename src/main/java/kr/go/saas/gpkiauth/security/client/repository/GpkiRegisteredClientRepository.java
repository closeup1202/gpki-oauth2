package kr.go.saas.gpkiauth.security.client.repository;

import kr.go.saas.gpkiauth.common.exception.NoSuchClientIdException;
import kr.go.saas.gpkiauth.security.client.entity.Client;
import kr.go.saas.gpkiauth.security.client.service.ClientHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
@RequiredArgsConstructor
public class GpkiRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientJpaRepository repository;
    private final ClientHelper clientHelper;

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registered client cannot be null");
        Client entity = clientHelper.toEntity(registeredClient);
        repository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        Client entity = repository.findByClientId(id).orElseThrow(() -> new NoSuchClientIdException(id));
        return clientHelper.toClient(entity, repository);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Client entity = repository.findByClientId(clientId).orElseThrow(() -> new NoSuchClientIdException(clientId));
        return clientHelper.toClient(entity, repository);
    }
}
