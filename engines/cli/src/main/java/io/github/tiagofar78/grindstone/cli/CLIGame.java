package io.github.tiagofar78.grindstone.cli;

public interface CLIGame {
    
    void process(int id, String[] args);
    
    void disconnect(int id);

}
