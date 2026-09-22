package ru.akpaev.keycloak;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpnOsUserMapperTest {

    @Test
    void convertsUpnToOsUser() {
        assertEquals("local\\akpaev", UpnOsUserMapper.toOsUser("akpaev@local"));
    }

    @Test
    void usesOnlyFirstDomainLabel() {
        assertEquals("local\\akpaev", UpnOsUserMapper.toOsUser("akpaev@local.corp.example"));
    }

    @Test
    void returnsEmptyForNull() {
        assertEquals("", UpnOsUserMapper.toOsUser(null));
    }

    @Test
    void returnsEmptyForUpnWithoutAtSign() {
        assertEquals("", UpnOsUserMapper.toOsUser("akpaev"));
    }
}
