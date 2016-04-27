package com.vjoon.se.core.uow;

/**
 * Created by guido on 25.09.15.
 */
public interface RollbackableCommand {
    void commit();
    void rollback();
}
