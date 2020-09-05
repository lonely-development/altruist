package com.altruist.account

import spock.lang.Specification

class AccountServiceTest extends Specification {

    AccountRepository mockAccountRepository = Mock()
    AccountService service = new AccountService(mockAccountRepository)

    def "Should save account and address"() {
        given: "an account"
        AccountDto account = new AccountDto(
                id: null,
                username: "username123",
                email: "email@example.com",
                address: new AccountDto.AddressDto(
                        name: "Some Name",
                        street: "Some street",
                        city: "Some city",
                        state: "CA",
                        zipcode: 99999
                )
        )
        UUID expectedAddressId = UUID.randomUUID()
        UUID expectedAccountId = UUID.randomUUID()

        when:
        UUID accountId = service.createAccount(account)

        then: "the account & address is saved"
        1 * mockAccountRepository.save(_) >> { AccountEntity arg ->
            with(arg) {
                username == account.username
                email == account.email
                address.name == account.address.name
                address.street == account.address.street
                address.city == account.address.city
                address.state == account.address.state
                address.zipcode == account.address.zipcode as Integer
            }

            arg.id = expectedAccountId
            arg.address.id = expectedAddressId
            arg
        }

        and:
        accountId == expectedAccountId
    }

}
