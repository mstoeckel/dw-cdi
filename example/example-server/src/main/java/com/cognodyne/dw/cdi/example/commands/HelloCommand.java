package com.cognodyne.dw.cdi.example.commands;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class HelloCommand extends Command {
    protected HelloCommand() {
        super("HelloCommand", "Test Command");
    }

    @Override
    public void configure(Subparser subparser) {
        // TODO Auto-generated method stub
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        // TODO Auto-generated method stub
    }
}
