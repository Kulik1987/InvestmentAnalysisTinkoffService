package ru.kulikovskiy.trading.investmantanalysistinkoff.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kulikovskiy.trading.investmantanalysistinkoff.config.TcsBrokerConfig;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountTinkoffDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.InstrumentsTinkoffDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.OperationsDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Instruments;
import ru.kulikovskiy.trading.investmantanalysistinkoff.dto.AccountDto;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Operations;
import ru.kulikovskiy.trading.investmantanalysistinkoff.model.Position;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
public class InvestmentTinkoffServiceImpl implements InvestmentTinkoffService {
    @Autowired
    private TcsBrokerConfig tcsBrokerConfig;
    @Autowired
    private RestTemplate tcsRestTemplate;

    @Override
    public List<AccountDto>

    getAccounts(String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/user").path("/accounts").toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<AccountTinkoffDto> response = tcsRestTemplate.exchange(url, HttpMethod.GET, request, AccountTinkoffDto.class);
        return Objects.requireNonNull(response.getBody()).getPayload().getAccounts();
    }

    @Override
    public List<Instruments> getStocks(String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/market").path("/stocks").toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<InstrumentsTinkoffDto> response = tcsRestTemplate.exchange(url, HttpMethod.GET, request, InstrumentsTinkoffDto.class);
        return Objects.requireNonNull(response.getBody()).getPayload().getInstruments();
    }

    @Override
    public List<Instruments> getBonds(String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/market").path("/bonds").toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<InstrumentsTinkoffDto> response = tcsRestTemplate.exchange(url, HttpMethod.GET, request, InstrumentsTinkoffDto.class);
        return Objects.requireNonNull(response.getBody()).getPayload().getInstruments();
    }

    @Override
    public List<Instruments> getEtfs(String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/market").path("/etfs").toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<InstrumentsTinkoffDto> response = tcsRestTemplate.exchange(url, HttpMethod.GET, request, InstrumentsTinkoffDto.class);
        return Objects.requireNonNull(response.getBody()).getPayload().getInstruments();
    }

    @Override
    public List<Instruments> getCurrencies(String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/market").path("/currencies").toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<InstrumentsTinkoffDto> response = tcsRestTemplate.exchange(url, HttpMethod.GET, request, InstrumentsTinkoffDto.class);
        return Objects.requireNonNull(response.getBody()).getPayload().getInstruments();
    }

    @Override
    public List<Operations> getOperations(String from, String to, String brokerAccountId, String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/operations")
                .queryParam("from", from).queryParam("to", to).queryParam("brokerAccountId", brokerAccountId).toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<OperationsDto> responseEntity = tcsRestTemplate.exchange(url, HttpMethod.GET, request, OperationsDto.class);
        return Objects.requireNonNull(responseEntity.getBody()).getPayload().getOperations();
    }

    @Override
    public List<Operations> getOperationsByFigi(String from, String to, String brokerAccountId, String token, String figi) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/operations")
                .queryParam("from", from).queryParam("figi", figi).queryParam("to", to).queryParam("brokerAccountId", brokerAccountId).toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);

        ResponseEntity<OperationsDto> responseEntity = tcsRestTemplate.exchange(url, HttpMethod.GET, request, OperationsDto.class);
        return Objects.requireNonNull(responseEntity.getBody()).getPayload().getOperations();
    }

    @Override
    public List<Position> getPosition(String brokerAccountId, String token) {
        String url = UriComponentsBuilder.newInstance().uri(URI.create(tcsBrokerConfig.getUrl())).path("/portfolio")
                .queryParam("brokerAccountId", brokerAccountId).toUriString();
        HttpEntity request = createInvestmentTinkoffRequest(token);
        ResponseEntity<OperationsDto> responseEntity = tcsRestTemplate.exchange(url, HttpMethod.GET, request, OperationsDto.class);

        return responseEntity.getBody().getPayload().getPositions();
    }

    @NotNull
    private HttpEntity createInvestmentTinkoffRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return new HttpEntity(headers);
    }
}