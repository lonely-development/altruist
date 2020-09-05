package com.altruist.account

import com.altruist.config.DbConfig
import com.altruist.config.RepositoryConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.EntityManager
import javax.persistence.PersistenceException

@ActiveProfiles("test")
@DataJpaTest(includeFilters = [@ComponentScan.Filter(type = FilterType.ANNOTATION, value = [Repository])])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [DbConfig, RepositoryConfig])
class AccountRepositoryTest extends Specification {

    @Autowired
    AccountRepository repository

    @Autowired
    EntityManager entityManager

    @Shared
    AccountEntity account

    def "Inserts an account with address"() {

        given: "an account with an address"
        account = new AccountEntity(
                email: "email@email.com",
                username: "username",
                address: new AddressEntity(
                        name: "name",
                        street: "street",
                        state: "CA",
                        city: "city",
                        zipcode: 12345
                )
        )

        when:
        account = repository.save(account)

        then: "the account id is returned"
        account.id
        and: "the address id is also returned"
        account.address.id
    }

    def "Inserts an account without address"() {

        given: "an account"
        account = new AccountEntity(
                username: "username",
                email: "email@email.com",
                address: null
        )

        when:
        account = repository.save(account)

        then: "the account id is returned"
        account.id
    }

    def "Retrieves an account by id"() {

        given: "an account"
        account = new AccountEntity(
                email: "email3@email.com",
                username: "username2",
                address: new AddressEntity(
                        name: "name1",
                        street: "street1",
                        state: "CA",
                        city: "city1",
                        zipcode: 0234
                )
        )

        when:
        account = repository.save(account)

        then: "account should be retrievable by id"
        repository.findById(account.id).isPresent()
    }

    def "Should validate for missing account field #field"() {

        given: "an account missing fields"
        account = new AccountEntity(
                username: "username",
                email: "email@email.com",
        )
        account[field] = null

        when:
        account = repository.save(account)
        entityManager.flush()

        then:
        thrown(PersistenceException)

        where:
        field << ["username", "email"]
    }

    def "Should validate for missing address field #field"() {
        given: "an address missing fields"
        account = new AccountEntity(
                email: "email3@email.com",
                username: "username2",
                address: new AddressEntity(
                        name: "name1",
                        street: "street1",
                        state: "CA",
                        city: "city1",
                        zipcode: 0234
                )
        )
        account.address[field] = null

        when:
        account = repository.save(account)
        entityManager.flush()

        then:
        thrown(PersistenceException)

        where:
        field << ["name", "street", "city", "state", "zipcode"]
    }

}
