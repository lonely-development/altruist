package com.altruist.account;


import com.altruist.common.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private UUID id;

    @NotBlank
    @JsonView(View.Create.class)
    private String username;

    @NotBlank
    @Email
    @JsonView(View.Create.class)
    private String email;

    @Nullable
    @JsonView(View.Create.class)
    private AddressDto address;

    public AccountDto(AccountEntity account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.address = account.getAddress() == null ? null
                : new AddressDto(account.getAddress());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {

        private UUID id;

        @NotBlank
        @JsonView(View.Create.class)
        private String name;

        @NotBlank
        @JsonView(View.Create.class)
        private String street;

        @NotBlank
        @JsonView(View.Create.class)
        private String city;

        @NotNull
        @JsonView(View.Create.class)
        private AddressEntity.State state;

        @Size(max = 9)
        @NotNull
        @JsonView(View.Create.class)
        private Integer zipcode;

        public AddressDto(AddressEntity address) {
            this.id = address.getId();
            this.name = address.getName();
            this.street = address.getStreet();
            this.city = address.getCity();
            this.state = address.getState();
            this.zipcode = address.getZipcode();
        }
    }


}
