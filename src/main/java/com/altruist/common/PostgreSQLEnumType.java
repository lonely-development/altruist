package com.altruist.common;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Class responsible for converting Java Enum to PostgreSQL Enum. Hibernate lacks
 * automatic conversion support.
 * <p>
 * I would have rather used TEXT for the database fields, but Account
 * is already using PostgreSQL enum, making migration easier.
 */
public class PostgreSQLEnumType<T extends Enum<T>>
        extends org.hibernate.type.EnumType<T> {

    @Override
    public void nullSafeSet(
            PreparedStatement statement,
            Object value,
            int index,
            SharedSessionContractImplementor session)
            throws SQLException {
        statement.setObject(
                index,
                value != null ? ((Enum) value).name() : null,
                Types.OTHER
        );
    }
}