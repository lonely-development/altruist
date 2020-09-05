package com.altruist.account;

import com.altruist.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Account")
@Table(name = "account")
public class AccountEntity extends BaseEntity {

  @Column(name = "username", nullable = false)
  private String username;

  @Email
  @Column(name = "email", nullable = false)
  private String email;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id", referencedColumnName = "id")
  private AddressEntity address;

  public AccountEntity(
          String username,
          @Email String email,
          @Nullable AddressEntity address) {
    this.username = username;
    this.email = email;
    this.address = address;
  }

}
