package service

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountService
import ru.kulikovskiy.trading.investmantanalysistinkoff.service.AccountServiceImpl
import spock.lang.Specification

class AccountServiceImplTest extends Specification {

    def CHAT_ID = "1"
    private hazelcastInstance = Mock(HazelcastInstance) {
        getMap(_) >> Mock(IMap)
    }

    private AccountService accountService = new AccountServiceImpl(
            hazelcastInstance: hazelcastInstance,
    )


    def "get account from token SUCCESS"() {
        given:
        def response
        when:
        response = accountService.saveToken("TOKEN_TEST", CHAT_ID)

        then:
        response == null
    }
}
