package io.github.chengsean.suda.sample.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.chengsean.suda.sample.util.CustomLocalDateSerializer;

import java.time.LocalDate;

/**
 * @author chengshaozhuang
 */
public class Account implements java.io.Serializable {
    private Long sn;
    private String id;
    private String name;
    private String email;
    private LocalDate birthday;

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Account{" +
                "sn=" + sn +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
