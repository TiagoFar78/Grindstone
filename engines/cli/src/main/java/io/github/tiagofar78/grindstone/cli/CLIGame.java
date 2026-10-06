package io.github.tiagofar78.grindstone.cli;

public interface CLIGame {

    String getName();
    
    void process(int id, String[] args);
    
    void disconnect(int id);

}
