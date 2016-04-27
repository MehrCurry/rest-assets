package com.vjoon.se.core.uow;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Slf4j
public class UnitOfWork {
    private List<RollbackableCommand> commands=new ArrayList<>();

    public void add(RollbackableCommand cmd) {
        commands.add(cmd);
    }

    public void commit() {
        Stack<RollbackableCommand> stack=new Stack<>();
        commands.stream().forEach(cmd -> {
            try {
                stack.push(cmd);
                cmd.commit();
            } catch (Exception e) {
                rollback(stack);
                throw e;
            }
            ;
        });
    }

    private void rollback(Stack<RollbackableCommand> stack) {
        stack.stream().forEach(cmd -> {
            try {
                cmd.rollback();
            } catch (Exception e) {
                log.error("Error during rollback",e);
            }
        });
    }
}
