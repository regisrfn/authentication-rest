package com.rufino.server.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.rufino.server.enumeration.Authority;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "authorities")
public class AuthorityModel {

    @Id
    private int id;

    @NotNull
    private Authority authority;

    public AuthorityModel() {
    };

    public AuthorityModel(Authority authority) {
        setAuthority(authority);
    }

    public void setAuthority(String authority) {
        try {
            this.authority = Authority.valueOf(authority);
            this.id = this.authority.ordinal();
        } catch (Exception e) {
            this.authority = null;
        }

    }

    public void setAuthority(Authority authority) {
        try {
            this.authority = authority;
            this.id = this.authority.ordinal();
        } catch (Exception e) {
            this.authority = null;
        }

    }

}
